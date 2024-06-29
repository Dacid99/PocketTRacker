package org.sbv.pockettracker;

import android.app.Activity;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements NumberPaneFragment.NumberPaneFragmentProvider, PlayerFragment.PlayerFragmentProvider {

    private ActivityResultLauncher<Intent> createFileActivityLauncher, readFileActivityLauncher;
    private Player player1, player2, turnPlayer;
    private PoolTable table;
    private ScoreSheet scoreSheet;
    private TextView player1NameView, player2NameView, player1ClubView, player2ClubView, player1ScoreView, player2ScoreView, ballsOnTableFloatingButton;
    private TextInputLayout winningPointsLayout;
    private TextInputEditText winningPointsInput;
    private MaterialCardView player1Card, player2Card;
    private MaterialButton foulButton, missButton, safeButton, redoButton, undoButton, newGameButton, viewScoreSheetButton, saveloadGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1ScoreView = findViewById(R.id.player1ScoreView);
        player2ScoreView = findViewById(R.id.player2ScoreView);

        player1NameView = findViewById(R.id.player1NameView);
        player2NameView = findViewById(R.id.player2NameView);

        player1ClubView = findViewById(R.id.player1ClubView);
        player2ClubView = findViewById(R.id.player2ClubView);

        player1Card = findViewById(R.id.player1CardView);
        player2Card = findViewById(R.id.player2CardView);

        ballsOnTableFloatingButton = findViewById(R.id.ballsOnTableFloatingButton);

        winningPointsLayout = findViewById(R.id.winningPointsLayout);
        winningPointsInput = findViewById(R.id.winningPointsInput);

        winningPointsInput.setText(getString(R.string.winnerPoints_format, Player.winningPoints));

        missButton = findViewById(R.id.missButton);
        safeButton = findViewById(R.id.safeButton);
        foulButton = findViewById(R.id.foulButton);
        undoButton = findViewById(R.id.undoButton);
        redoButton = findViewById(R.id.redoButton);
        newGameButton = findViewById(R.id.newGame);
        saveloadGameButton = findViewById(R.id.saveloadGame);
        viewScoreSheetButton = findViewById(R.id.viewScoreSheet);

        newGame();


        winningPointsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                if ( NumberUtils.isParsable(newText)) {
                    int newWinnerPoints = Integer.parseInt(newText);
                    if (newWinnerPoints != Player.winningPoints && newWinnerPoints >= 1){
                        Player.winningPoints = newWinnerPoints;
                        updateWinnerUI();
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
                PlayerFragment playerFragment = PlayerFragment.newInstance(1);
                playerFragment.show(getSupportFragmentManager(), "Player1Fragment");
            }
        });

        player2Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerFragment playerFragment = PlayerFragment.newInstance(2);
                playerFragment.show(getSupportFragmentManager(), "Player2Fragment");
            }
        });

        ballsOnTableFloatingButton.setOnClickListener(v -> {
            NumberPaneFragment numberPaneFragment = NumberPaneFragment.newInstance(table.getOldNumberOfBalls());
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
            turnPlayer.addPoints( (scoreSheet.turn() == 0) ? -2:-1 );
            newTurn(getString(R.string.foul_string));
        });

        undoButton.setOnClickListener(v -> {
            scoreSheet.rollback();
            updateScoreUI();
            updateUnRedoUI();
            switchTurnPlayer();
            updateFocusUI();
            updateWinnerUI();
            updateSaveLoadUI();
        });

        redoButton.setOnClickListener(v -> {
            scoreSheet.progress();
            updateScoreUI();
            updateUnRedoUI();
            switchTurnPlayer();
            updateFocusUI();
            updateWinnerUI();
            updateSaveLoadUI();
        });

        newGameButton.setOnClickListener(v -> {
            newGame();
            newGameButton.setVisibility(View.INVISIBLE);
        });

        viewScoreSheetButton.setOnClickListener(v -> {
            if (scoreSheet.isHealthy()) {
                Intent intent = new Intent(MainActivity.this, ScoreSheetActivity.class);
                intent.putExtra("scoreSheet", scoreSheet);
                intent.putExtra("player1Name", player1.getName());
                intent.putExtra("player2Name", player2.getName());
                startActivity(intent);
            } else{
                Toast.makeText(this, getResources().getText(R.string.cantOpenScoreSheet_toast), Toast.LENGTH_SHORT).show();
            }
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
                                ScoreSheetIO.writeToFile(outputStreamWriter, scoreSheet);
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
                                ScoreSheetIO.loadFromFile(inputStreamReader, scoreSheet);
                                if (scoreSheet.turn() % 2 == 0){
                                    turnPlayer = player1;
                                }else {
                                    turnPlayer = player2;
                                }
                                updateFocusUI();
                                updateScoreUI();
                                updateUnRedoUI();
                                updateWinnerUI();
                                updateSaveLoadUI();
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

    private void updateScoreUI() {
        player1ScoreView.setText(getString(R.string.player_score_format, player1.getScore()));
        player2ScoreView.setText(getString(R.string.player_score_format, player2.getScore()));
        ballsOnTableFloatingButton.setText(getString(R.string.ballsOnTable_format, table.getNumberOfBalls()));

        // blocks the user from any more input after theres a winner
        // disabled to give the user more freedom
        //winningPointsInput.setClickable(scoreSheet.turn() == 0);
        //winningPointsInput.setFocusable(scoreSheet.turn() == 0);
        //winningPointsInput.setEnabled(scoreSheet.turn() == 0);
        //setButtonsStatus( (player1.getScore() < winnerPoints) && (player2.getScore() < winnerPoints) );
    }

    private void updateUnRedoUI(){
        if (scoreSheet.isLatest()){
            redoButton.setVisibility(View.INVISIBLE);
        }else{
            redoButton.setVisibility(View.VISIBLE);
        }
        if (scoreSheet.isStart()){
            undoButton.setVisibility(View.INVISIBLE);
        }else {
            undoButton.setVisibility(View.VISIBLE);
        }
    }

    private void setButtonsStatus(boolean toggle){
        foulButton.setClickable(toggle);
        safeButton.setClickable(toggle);
        missButton.setClickable(toggle);
    }

    private void updatePlayerUI(){
        player1NameView.setText(getString(R.string.player_name_format, player1.getName()));
        player2NameView.setText(getString(R.string.player_name_format, player2.getName()));
        player1ClubView.setText(getString(R.string.player_club_format, player1.getClub()));
        player2ClubView.setText(getString(R.string.player_club_format, player2.getClub()));
    }

    private void newTurn(String reason){
        switchTurnPlayer();
        updateFocusUI();
        scoreSheet.update(reason);
        updateUnRedoUI();
        updateScoreUI();
        updateWinnerUI();
        updateSaveLoadUI();
    }

    private void newGame(){
        player1 = new Player();
        player2 = new Player();

        turnPlayer = player1;

        table = new PoolTable();

        scoreSheet = new ScoreSheet(table, player1, player2);

        updateScoreUI();
        updateUnRedoUI();
        updateWinnerUI();
        updatePlayerUI();
        updateFocusUI();
        updateSaveLoadUI();
    }

    private void switchTurnPlayer(){
        if (turnPlayer == player1) {
            turnPlayer = player2;
        }else if (turnPlayer == player2){
            turnPlayer = player1;
        }else {
            Log.e("Failed ifelse", "In MainActivity.switchTurnPlayer: Turnplayer is neither player1 or player2!");
        }
    }

    private void updateFocusUI(){
        if (turnPlayer == player1) {
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
        }else if (turnPlayer == player2) {
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
        }else {
            Log.e("Failed ifelse", "In MainActivity.updateFocusUI: Turnplayer is neither player1 or player2!");
        }
    }

    private void updateWinnerUI(){
        if (player1.isWinner()){
            player1Card.setCardBackgroundColor(getResources().getColor(R.color.winner_color));
            newGameButton.setVisibility(View.VISIBLE);
        }else if (player2.isWinner()){
            player2Card.setCardBackgroundColor(getResources().getColor(R.color.winner_color));
            newGameButton.setVisibility(View.VISIBLE);
        } else {
            updateFocusUI(); //card backgrounds ungoldened by updateFocus
            newGameButton.setVisibility(View.INVISIBLE);
        }
    }

    private void updateSaveLoadUI(){
        if (scoreSheet.turn() == 0){
            saveloadGameButton.setOnClickListener(v -> openReadDocumentIntent());
            saveloadGameButton.setText(getString(R.string.loadGame_string));
        } else {
            saveloadGameButton.setOnClickListener(v -> openCreateDocumentIntent());
            saveloadGameButton.setText(getString(R.string.saveGame_string));
        }
    }

    private void assignPoints(){
        int points = table.evaluate();
        turnPlayer.addPoints(points);
    }

    //numberpanelistener methods
    @Override
    public void onNumberPaneClick(int number){
        table.setNumberOfBalls(number);
        if (number == 1) {
            assignPoints();
        }
        updateScoreUI();
        updateWinnerUI();
    }

    //playerfragmentprovider methods
    @Override
    public void onNameInput(int playerNumber, String name){
        if (playerNumber == 1) {
           player1.setName(name);
        }else if (playerNumber == 2){
            player2.setName(name);
        }else{
            Log.d("argument error", "In MainActivity.onNameInput: No such playerNumber:" + playerNumber);
        }
        updatePlayerUI();
    }
    @Override
    public void onClubInput(int playerNumber, String club){
        if (playerNumber == 1) {
            player1.setClub(club);
        }else if (playerNumber == 2){
            player2.setClub(club);
        }else{
            Log.d("argument error", "In MainActivity.onNameInput: No such playerNumber:" + playerNumber);
        }
        updatePlayerUI();
    }
    @Override
    public void onSwapButtonClick(){
        player1.swapNameAndClubWith(player2);
        updatePlayerUI();
    }
    @Override
    public Player requestPlayer(int playerNumber){
        if (playerNumber == 1) {
            return player1;
        }else if (playerNumber == 2){
            return player2;
        }else {
            Log.d("argument error", "In MainActivity.requestPlayer: No such playerNumber:" + playerNumber);
            return null;
        }
    }
    @Override
    public ScoreSheet requestScoreSheet(){
        return scoreSheet;
    }

    //IO for saving and loading
    private void openCreateDocumentIntent(){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");

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
}
