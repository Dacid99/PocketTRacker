package org.sbv.pockettracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import org.apache.commons.lang3.math.NumberUtils;

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

public class MainActivity extends AppCompatActivity implements NumberPaneFragment.NumberPaneFragmentProvider, PlayerFragment.PlayerFragmentProvider {

    public static final String SCORESHEETPARAMETER = "scoresheet";
    public static final String SCOREBOARDPARAMETER = "scoreboard";
    public static final String PLAYERSPARAMETER = "player1";
    private ActivityResultLauncher<Intent> createFileActivityLauncher, readFileActivityLauncher;
    private SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private PlayersViewModel playersViewModel;
    private ScoreBoardViewModel scoreBoardViewModel;
    private PoolTableViewModel poolTableViewModel;
    private ScoreSheetViewModel scoreSheetViewModel;
    private TextView player1NameView, player2NameView, player1ClubView, player2ClubView, player1ScoreView, player2ScoreView, ballsOnTableFloatingButton;
    private TextInputEditText winningPointsInput;
    private MaterialCardView player1Card, player2Card;
    private MaterialButton counterButton, scoreSheetButton, settingsButton, foulButton, missButton, safeButton, redoButton, undoButton, newGameButton, saveloadGameButton;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applyPreferences();


        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
                if (key != null) {
                    switch (key) {
                       case "winnerPoints_default":
                           String winnerPointsString = sharedPreferences.getString(key, "40");
                           int oldWinnerPoints = ScoreBoard.defaultWinnerPoints;
                           try{
                               ScoreBoard.defaultWinnerPoints = Integer.parseInt(winnerPointsString);
                               if (Objects.requireNonNull(winningPointsInput.getText()).toString().isEmpty() || winningPointsInput.getText().toString().equals(String.valueOf(oldWinnerPoints))){
                                   winningPointsInput.setText(winnerPointsString);
                               }
                           }catch (NumberFormatException e) {
                               Log.d("Bad preference","In SettingsActivity.onSharedPreferenceChanged: winnerpoints_default is not a parseable String! e");
                               ScoreBoard.defaultWinnerPoints = 40;
                           }
                           break;

                        case "player1_name_default":
                            String newNamePlayer1 = sharedPreferences.getString(key,"");
                            if (player1NameView.getText().toString().isEmpty() || player1NameView.getText().toString().equals(Players.defaultPlayerNames[0])){
                                playersViewModel.updatePlayerName(1,newNamePlayer1);
                            }
                            Players.defaultPlayerNames[0] = newNamePlayer1;
                            break;

                        case "player2_name_default":
                            String newNamePlayer2 = sharedPreferences.getString(key,"");
                            if (player2NameView.getText().toString().isEmpty() || player2NameView.getText().toString().equals(Players.defaultPlayerNames[1])){
                                playersViewModel.updatePlayerName(2,newNamePlayer2);
                            }
                            Players.defaultPlayerNames[1] = newNamePlayer2;
                            break;

                        case "player1_club_default":
                            String newClubPlayer1 = sharedPreferences.getString(key,"");
                            if (player1ClubView.getText().toString().isEmpty() || player1ClubView.getText().toString().equals(Players.defaultPlayerClubs[0])){
                                playersViewModel.updateClubName(1, newClubPlayer1);
                            }
                            Players.defaultPlayerClubs[0] = newClubPlayer1;
                            break;

                        case "player2_club_default":
                            String newClubPlayer2 = sharedPreferences.getString(key,"");
                            if (player2ClubView.getText().toString().isEmpty() || player2ClubView.getText().toString().equals(Players.defaultPlayerClubs[1])){
                                playersViewModel.updateClubName(2, newClubPlayer2);
                            }
                            Players.defaultPlayerClubs[1] =newClubPlayer2;
                            break;

                        case "club_toggle":
                            Players.haveClubs = sharedPreferences.getBoolean(key, true);
                            if (!Players.haveClubs){
                                player1ClubView.setVisibility(View.GONE);
                                player2ClubView.setVisibility(View.GONE);
                            }else {
                                player1ClubView.setVisibility(View.VISIBLE);
                                player2ClubView.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                }
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        //toolbar buttons
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        counterButton = findViewById(R.id.counter_button);
        scoreSheetButton = findViewById(R.id.scoresheet_button);
        settingsButton = findViewById(R.id.settings_button);
        //deactivate counter button in this activity
        counterButton.setClickable(false);
        counterButton.setTextColor(getResources().getColor(R.color.current_activity_color));

        player1ScoreView = findViewById(R.id.player1ScoreView);
        player2ScoreView = findViewById(R.id.player2ScoreView);

        player1NameView = findViewById(R.id.player1NameView);
        player2NameView = findViewById(R.id.player2NameView);

        player1ClubView = findViewById(R.id.player1ClubView);
        player2ClubView = findViewById(R.id.player2ClubView);

        if (!Players.haveClubs){
            player1ClubView.setVisibility(View.GONE);
            player2ClubView.setVisibility(View.GONE);
        }

        player1Card = findViewById(R.id.player1CardView);
        player2Card = findViewById(R.id.player2CardView);

        ballsOnTableFloatingButton = findViewById(R.id.ballsOnTableFloatingButton);

        winningPointsInput = findViewById(R.id.winningPointsInput);

        missButton = findViewById(R.id.missButton);
        safeButton = findViewById(R.id.safeButton);
        foulButton = findViewById(R.id.foulButton);
        undoButton = findViewById(R.id.undoButton);
        redoButton = findViewById(R.id.redoButton);
        newGameButton = findViewById(R.id.newGame);
        saveloadGameButton = findViewById(R.id.saveloadGame);

        playersViewModel = new ViewModelProvider(this).get(PlayersViewModel.class);
        playersViewModel.getPlayers().observe(this, new Observer<Players>() {
            @Override
            public void onChanged(Players players) {
                if (players != null){
                    player1NameView.setText(getString(R.string.player_name_format, players.getNames()[0]));
                    player1ClubView.setText(getString(R.string.player_club_format, players.getClubs()[0]));
                    player2NameView.setText(getString(R.string.player_name_format, players.getNames()[1]));
                    player2ClubView.setText(getString(R.string.player_club_format, players.getClubs()[1]));
                }
            }
        });
        scoreBoardViewModel = new ViewModelProvider(this).get(ScoreBoardViewModel.class);
        scoreBoardViewModel.getScoreBoard().observe(this, new Observer<ScoreBoard>() {
            @Override
            public void onChanged(ScoreBoard scoreBoard) {
                if (scoreBoard != null){
                    player1ScoreView.setText(getString(R.string.player_score_format, scoreBoard.getPlayerScores()[0]));
                    player2ScoreView.setText(getString(R.string.player_score_format, scoreBoard.getPlayerScores()[1]));
                    winningPointsInput.setText(getString(R.string.winnerPoints_format, scoreBoard.getWinnerPoints()));
                    if (scoreBoard.getWinner() == 0){
                        player1Card.setCardBackgroundColor(getResources().getColor(R.color.winner_color));
                        newGameButton.setVisibility(View.VISIBLE);
                    }else if (scoreBoard.getWinner() == 1){
                        player2Card.setCardBackgroundColor(getResources().getColor(R.color.winner_color));
                        newGameButton.setVisibility(View.VISIBLE);
                    } else {
                        newGameButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        poolTableViewModel = new ViewModelProvider(this).get(PoolTableViewModel.class);
        poolTableViewModel.getPoolTable().observe(this, new Observer<PoolTable>() {
            @Override
            public void onChanged(PoolTable poolTable) {
                if (poolTable != null) {
                    ballsOnTableFloatingButton.setText(getString(R.string.ballsOnTable_format, poolTable.getNumberOfBalls()));
                }
            }
        });

        ScoreSheetViewModelFactory factory = new ScoreSheetViewModelFactory(poolTableViewModel, scoreBoardViewModel);
        scoreSheetViewModel = new ViewModelProvider(this, factory).get(ScoreSheetViewModel.class);
        scoreSheetViewModel.getScoreSheet().observe(this, new Observer<ScoreSheet>() {
            @Override
            public void onChanged(ScoreSheet scoreSheet) {
                if (scoreSheet != null) {
                    if (scoreSheet.turnplayerNumber() == 0) {
                        player1Card.setCardBackgroundColor(getResources().getColor(R.color.turnplayer_color));
                        player1Card.setCardElevation(10);
                        player1ScoreView.setEnabled(true);
                        player1NameView.setEnabled(true);
                        player1ClubView.setEnabled(true);
                        player2Card.setCardBackgroundColor(getResources().getColor(R.color.notturnplayer_color));
                        player2Card.setCardElevation(0);
                        player2ScoreView.setEnabled(false);
                        player2NameView.setEnabled(false);
                        player2ClubView.setEnabled(false);
                    } else if (scoreSheet.turnplayerNumber() == 1) {
                        player1Card.setCardBackgroundColor(getResources().getColor(R.color.notturnplayer_color));
                        player1Card.setCardElevation(0);
                        player1ScoreView.setEnabled(false);
                        player1NameView.setEnabled(false);
                        player1ClubView.setEnabled(false);
                        player2Card.setCardBackgroundColor(getResources().getColor(R.color.turnplayer_color));
                        player2Card.setCardElevation(10);
                        player2ScoreView.setEnabled(true);
                        player2NameView.setEnabled(true);
                        player2ClubView.setEnabled(true);
                    } else {
                        Log.e("Failed ifelse", "In MainActivity.updateFocusUI: Turnplayer is neither player1 or player2!");
                    }
                    if (scoreSheetViewModel.isStart()){
                        saveloadGameButton.setOnClickListener(v -> openReadDocumentIntent());
                        saveloadGameButton.setText(getString(R.string.loadGame_string));
                    } else {
                        saveloadGameButton.setOnClickListener(v -> openCreateDocumentIntent());
                        saveloadGameButton.setText(getString(R.string.saveGame_string));
                    }
                    if (scoreSheetViewModel.isLatest()){
                        redoButton.setVisibility(View.INVISIBLE);
                    }else{
                        redoButton.setVisibility(View.VISIBLE);
                    }
                    if (scoreSheetViewModel.isStart()){
                        undoButton.setVisibility(View.INVISIBLE);
                    }else {
                        undoButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        winningPointsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                if ( NumberUtils.isParsable(newText)) {
                    int newWinnerPoints = Integer.parseInt(newText);
                    if (newWinnerPoints >= 1){
                        scoreBoardViewModel.updateWinnerPoints(newWinnerPoints);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = s.toString();
                if ( NumberUtils.isParsable(newText)) {
                    if (Integer.parseInt(newText) > 0) {
                        winningPointsInput.setTextColor(getResources().getColor(R.color.score_color));
                    } else {
                        winningPointsInput.setTextColor(getResources().getColor(R.color.warning_color));
                    }
                } else {
                    winningPointsInput.setTextColor(getResources().getColor(R.color.warning_color));
                }
            }
        });

        player1Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerFragment playerFragment = PlayerFragment.newInstance(0);
                playerFragment.show(getSupportFragmentManager(), "Player1Fragment");
            }
        });

        player2Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerFragment playerFragment = PlayerFragment.newInstance(1);
                playerFragment.show(getSupportFragmentManager(), "Player2Fragment");
            }
        });

        ballsOnTableFloatingButton.setOnClickListener(v -> {
            NumberPaneFragment numberPaneFragment = NumberPaneFragment.newInstance(poolTableViewModel.getOldNumberOfBalls());
            numberPaneFragment.show(getSupportFragmentManager(), "NumberPane");
        });

        missButton.setOnClickListener(v -> {
            assignPoints();
            newTurn(getString(R.string.miss_string));
        });

        safeButton.setOnClickListener(v -> {
            assignPoints();
            newTurn(getString(R.string.safe_string));
        });

        foulButton.setOnClickListener(v -> {
            assignPoints();
            scoreBoardViewModel.addPoints( scoreSheetViewModel.turnplayerNumber(), (scoreSheetViewModel.currentTurn() == 0) ? -2:-1 );
            newTurn(getString(R.string.foul_string));
        });

        undoButton.setOnClickListener(v -> {
            scoreSheetViewModel.rollback();
        });

        undoButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                scoreSheetViewModel.toStart();
                return true;
            }
        });

        redoButton.setOnClickListener(v -> {
            scoreSheetViewModel.progress();
        });

        redoButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                scoreSheetViewModel.toLatest();
                return true;
            }
        });

        newGameButton.setOnClickListener(v -> {
            newGame();
            newGameButton.setVisibility(View.INVISIBLE);
        });

        scoreSheetButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ScoreSheetActivity.class);
            intent.putExtra(SCORESHEETPARAMETER, scoreSheetViewModel.getScoreSheet().getValue());
            intent.putExtra(PLAYERSPARAMETER, playersViewModel.getPlayers().getValue());
            intent.putExtra(SCOREBOARDPARAMETER, scoreBoardViewModel.getScoreBoard().getValue());
            startActivity(intent);

        });

        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

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
                        }else {
                            Toast.makeText(MainActivity.this, "Failed to save game: no data returned" , Toast.LENGTH_LONG).show();
                        }
                    }else{
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
    }

    private void newTurn(String reason){
        scoreSheetViewModel.update(reason);
    }

    private void newGame(){
        playersViewModel.reset();

        poolTableViewModel.reset();

        scoreBoardViewModel.reset();

        scoreSheetViewModel.reset(poolTableViewModel, scoreBoardViewModel);
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

    //IO for saving and loading
    private void openCreateDocumentIntent(){
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

    private void openReadDocumentIntent(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        readFileActivityLauncher.launch(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }
}
