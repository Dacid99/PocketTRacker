package org.sbv.pockettracker;

import static androidx.navigation.Navigation.findNavController;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.preference.PreferenceManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements CounterFragment.CounterFragmentListener, ScoreSheetFragment.ScoreSheetFragmentListener, NumberPaneFragment.NumberPaneFragmentProvider, PlayerFragment.PlayerFragmentProvider{

    private PlayersViewModel playersViewModel;
    private ScoreBoardViewModel scoreBoardViewModel;
    private PoolTableViewModel poolTableViewModel;
    private ScoreSheetViewModel scoreSheetViewModel;
    private SharedPreferences preferences;
    private NavController navController;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;


    private ActivityResultLauncher<Intent> createFileActivityLauncher;
    private ActivityResultLauncher<Intent> readFileActivityLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playersViewModel = new ViewModelProvider(this).get(PlayersViewModel.class);

        scoreBoardViewModel = new ViewModelProvider(this).get(ScoreBoardViewModel.class);

        poolTableViewModel = new ViewModelProvider(this).get(PoolTableViewModel.class);

        ScoreSheetViewModelFactory factory = new ScoreSheetViewModelFactory(poolTableViewModel, scoreBoardViewModel);
        scoreSheetViewModel = new ViewModelProvider(this, factory).get(ScoreSheetViewModel.class);


        applyPreferences();

        applyNavigation();

        createFileActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            Intent data = o.getData();
                            if (data != null && data.getData() != null) {
                                Uri uri = data.getData();
                                try (OutputStream outputStream = getContentResolver().openOutputStream(uri);
                                     OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {
                                    ScoreSheetIO.writeToFile(outputStreamWriter, Objects.requireNonNull(playersViewModel.getPlayers().getValue()), Objects.requireNonNull(scoreSheetViewModel.getScoreSheet().getValue()));
                                    Toast.makeText(MainActivity.this, "Game saved successfully!", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Toast.makeText(MainActivity.this, "Failed to save game:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to save game: no data returned", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Log.d("IO", "MainActivity.createFileActivityLauncher in Callback: Failed to load game: operation cancelled");
                        }
                    }
                }
        );

        readFileActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            Intent data = o.getData();
                            if (data != null && data.getData() != null) {
                                Uri uri = data.getData();
                                try (InputStream inputStream = getContentResolver().openInputStream(uri);
                                     InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                                    ScoreSheetIO.readFromFile(inputStreamReader, playersViewModel, scoreSheetViewModel);
                                    Toast.makeText(MainActivity.this, "Game loaded successfully!", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Toast.makeText(MainActivity.this, "Failed to load game:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(MainActivity.this, "Failed to load game: no data returned" , Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Log.d("IO", "MainActivity.readFileActivityLauncher in Callback: Failed to load game: operation cancelled");
                        }
                    }
                });
    }
    private void applyPreferences(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String stringWinnerPoints = preferences.getString("winnerPoints_default", "40");
        try{
            ScoreBoard.defaultWinnerPoints = Integer.parseInt(stringWinnerPoints);
        } catch (NumberFormatException ne){
            Log.d("Bad preference", "In MainActivity.onCreate: winnerpoints is not saved as a parseable String!", ne);
            ScoreBoard.defaultWinnerPoints = 40;
        }

        Players.defaultPlayerNames[0] = preferences.getString("player1_name_default", "");
        Players.defaultPlayerNames[1] = preferences.getString("player2_name_default", "");
        Players.defaultPlayerClubs[0] = preferences.getString("player1_club_default", "");
        Players.defaultPlayerClubs[1] = preferences.getString("player2_club_default", "");
        Players.haveClubs = preferences.getBoolean("club_toggle", true);

        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
                if (key != null) {

                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private void applyNavigation(){
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        NavigationRailView navigationRailView = findViewById(R.id.rail_navigation);
        if (navigationRailView != null) {
            NavigationUI.setupWithNavController(navigationRailView, navController);
        }
    }

    private void assignPoints(){
        int points = poolTableViewModel.evaluate();
        scoreBoardViewModel.addPoints(scoreSheetViewModel.turnplayerNumber(), points);
    }

    //numberpanelistener methods
    @Override
    public void onNumberPaneClick(int number){
        poolTableViewModel.updateNumberOfBalls(number);
        if (number == 1) {
            assignPoints();
        }
    }

    //playerfragmentprovider methods
    @Override
    public void onNameInput(int playerNumber, String name){
        playersViewModel.updatePlayerName(playerNumber, name);
    }
    @Override
    public void onClubInput(int playerNumber, String club){
        playersViewModel.updateClubName(playerNumber, club);
    }
    @Override
    public void onSwapButtonClick(){
        playersViewModel.swap();
    }

    //counterfragment listener methods
    @Override
    public void onSaveButtonClick() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        String nameProposal = "game_" + formatter.format(now) + ".csv";
        intent.putExtra(Intent.EXTRA_TITLE, nameProposal);

        createFileActivityLauncher.launch(intent);
    }

    @Override
    public void onLoadButtonClick() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        readFileActivityLauncher.launch(intent);
    }

    @Override
    public void onPlayerCardClick(int playerNumber) {
        PlayerFragment playerFragment = PlayerFragment.newInstance(playerNumber);
        playerFragment.show(getSupportFragmentManager(), "Player"+playerNumber+1+"Fragment");
    }

    @Override
    public void onBallsOnTableFloatingButtonClick() {
        NumberPaneFragment numberPaneFragment = NumberPaneFragment.newInstance(poolTableViewModel.getOldNumberOfBalls());
        numberPaneFragment.show(getSupportFragmentManager(), "NumberPane");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
}