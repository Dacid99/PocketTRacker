package org.sbv.straightpoolcounter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;

public class MainActivity extends AppCompatActivity {

    private Player player1, player2, turnPlayer;
    private PoolTable table;
    private ScoreSheet scoreSheet;
    private TextView player1ScoreView, player2ScoreView, ballNumberView;
    private TextInputLayout player1NameLayout, player2NameLayout, player1ClubLayout, player2ClubLayout, newBallNumberLayout, winningPointsLayout;
    private TextInputEditText player1NameInput, player2NameInput, player1ClubInput, player2ClubInput, newBallNumberInput, winningPointsInput;
    private MaterialButton foulButton, missButton, safeButton, rerackButton, redoButton, undoButton, newGameButton, swapPlayersButton;
    private int winnerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1ScoreView = findViewById(R.id.player1Score);
        player2ScoreView = findViewById(R.id.player2Score);

        player1NameLayout = findViewById(R.id.player1NameLayout);
        player1NameInput = findViewById(R.id.player1Name);

        player2NameLayout= findViewById(R.id.player2NameLayout);
        player2NameInput = findViewById(R.id.player2Name);

        player1ClubLayout = findViewById(R.id.player1ClubLayout);
        player1ClubInput = findViewById(R.id.player1Club);

        player2ClubLayout= findViewById(R.id.player2ClubLayout);
        player2ClubInput = findViewById(R.id.player2Club);

        ballNumberView = findViewById(R.id.ballNumber);

        newBallNumberLayout = findViewById(R.id.newBallNumberLayout);
        newBallNumberInput = findViewById(R.id.newBallNumberInput);

        winningPointsLayout = findViewById(R.id.winningPointsLayout);
        winningPointsInput = findViewById(R.id.winningPointsInput);

        winnerPoints = 40; //default value
        System.out.println(winnerPoints);
        winningPointsInput.setText(getString(R.string.winnerPoints_format, winnerPoints));

        missButton = findViewById(R.id.missButton);
        safeButton = findViewById(R.id.safeButton);
        foulButton = findViewById(R.id.foulButton);
        rerackButton = findViewById(R.id.rerackButton);
        undoButton = findViewById(R.id.undoButton);
        redoButton = findViewById(R.id.redoButton);
        newGameButton = findViewById(R.id.newGame);
        swapPlayersButton = findViewById(R.id.swapPlayers);

        newGame();

        player1NameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newName = s.toString();
                if (!newName.equals( player1.getName() ) ) {
                    player1.setName(newName);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        player2NameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newName = s.toString();
                if (!newName.equals( player2.getName()) ) {
                    player2.setName(newName);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        player1ClubInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newClub = s.toString();
                if (!newClub.equals( player1.getClub() ) ) {
                    player1.setClub(newClub);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        player2ClubInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newClub = s.toString();
                if (!newClub.equals( player2.getClub()) ) {
                    player2.setClub(newClub);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newBallNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = s.toString();
                if ( NumberUtils.isParsable(newText)) {
                    if (!table.isValidBallNumber(Integer.parseInt(newText))) {
                        newBallNumberInput.setTextColor(getResources().getColor(R.color.warning_color));
                    }
                    else {
                        newBallNumberInput.setTextColor(getResources().getColor(R.color.score_color));
                    }
                }
                else {
                    newBallNumberInput.setTextColor(getResources().getColor(R.color.warning_color));
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
                    if (newWinnerPoints != winnerPoints && newWinnerPoints >= 1){
                        winnerPoints = newWinnerPoints;
                        updateWinner();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = s.toString();
                if ( NumberUtils.isParsable(newText)) {
                    if (Integer.parseInt(newText) > 0) {
                        winningPointsInput.setTextColor(getResources().getColor(R.color.black));
                    } else {
                        winningPointsInput.setTextColor(getResources().getColor(R.color.warning_color));
                    }

                } else {
                    winningPointsInput.setTextColor(getResources().getColor(R.color.warning_color));
                }
            }
        });

        missButton.setOnClickListener(v -> {
            int points = calculatePoints();
            if (points == -1) {
                return;
            }
            turnPlayer.addPoints(points);
            newTurn(getString(R.string.miss_string));
        });

        safeButton.setOnClickListener(v -> {
            int points = calculatePoints();
            if (points == -1) {
                return;
            }
            turnPlayer.addPoints(points);
            newTurn(getString(R.string.safe_string));
        });

        foulButton.setOnClickListener(v -> {
            int points = calculatePoints();
            if (points == -1) {
                return;
            }
            turnPlayer.addPoints(points);
            turnPlayer.addPoints( (scoreSheet.length() == 0) ? -2:-1 );
            newTurn(getString(R.string.foul_string));
        });

        rerackButton.setOnClickListener(v -> {
            int points = table.getNumberOfBalls() - 1 ;
            turnPlayer.addPoints(points);
            table.rerack();
            updateScoreUI();
        });

        undoButton.setOnClickListener(v -> {
            scoreSheet.rollback();
            switchTurnPlayer();
            updateScoreUI();
        });

        redoButton.setOnClickListener(v -> {
            scoreSheet.progress();
            switchTurnPlayer();
            updateScoreUI();
        });

        newGameButton.setOnClickListener(v -> {
            newGame();
            newGameButton.setVisibility(View.INVISIBLE);
        });

        swapPlayersButton.setOnClickListener(v -> {
            player1.swapNameAndClubWith(player2);
            updatePlayerUI();
        });
    }

    private void updateScoreUI() {
        player1ScoreView.setText(getString(R.string.player_score_format, player1.getScore()));
        player2ScoreView.setText(getString(R.string.player_score_format, player2.getScore()));
        ballNumberView.setText(getString(R.string.ball_number_format, table.getNumberOfBalls()));
        newBallNumberInput.setText(getString(R.string.newBallNumber_format, table.getNumberOfBalls()));

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

        // blocks the user from any more input after theres a winner
        // disabled to give the user more freedom
        //winningPointsInput.setClickable(scoreSheet.turn() == 0);
        //winningPointsInput.setFocusable(scoreSheet.turn() == 0);
        //winningPointsInput.setEnabled(scoreSheet.turn() == 0);
        //setButtonsStatus( (player1.getScore() < winnerPoints) && (player2.getScore() < winnerPoints) );
        updateWinner();
    }

    private void setButtonsStatus(boolean toggle){
        foulButton.setClickable(toggle);
        safeButton.setClickable(toggle);
        missButton.setClickable(toggle);
        rerackButton.setClickable(toggle);
    }

    private void updatePlayerUI(){
        player1NameInput.setText(getString(R.string.player_name_format, player1.getName()));
        player2NameInput.setText(getString(R.string.player_name_format, player2.getName()));
        player1ClubInput.setText(getString(R.string.player_club_format, player1.getClub()));
        player2ClubInput.setText(getString(R.string.player_club_format, player2.getClub()));
    }



    private void newTurn(String reason){
        switchTurnPlayer();
        scoreSheet.update();
        scoreSheet.writeSwitchReason(reason);
        updateScoreUI();
    }

    private void newGame(){
        player1 = new Player();
        player2 = new Player();

        turnPlayer = player1;

        table = new PoolTable();

        scoreSheet = new ScoreSheet(table, player1, player2);

        updateScoreUI();
        updatePlayerUI();
        updateFocus();
    }

    private void switchTurnPlayer(){
        if (turnPlayer == player1) {
            turnPlayer = player2;
        }else {
            turnPlayer = player1;
        }
        updateFocus();
    }

    private void updateFocus(){
        if (turnPlayer == player1) {
            player1NameInput.setBackgroundColor(getResources().getColor(R.color.turnplayer_color));
            player1ClubInput.setBackgroundColor(getResources().getColor(R.color.turnplayer_color));
            player1ScoreView.setEnabled(true);
            player2NameInput.setBackgroundColor(getResources().getColor(R.color.notturnplayer_color));
            player2ClubInput.setBackgroundColor(getResources().getColor(R.color.notturnplayer_color));
            player2ScoreView.setEnabled(false);
        }else {
            player1NameInput.setBackgroundColor(getResources().getColor(R.color.notturnplayer_color));
            player1ClubInput.setBackgroundColor(getResources().getColor(R.color.notturnplayer_color));
            player1ScoreView.setEnabled(false);
            player2NameInput.setBackgroundColor(getResources().getColor(R.color.turnplayer_color));
            player2ClubInput.setBackgroundColor(getResources().getColor(R.color.turnplayer_color));
            player2ScoreView.setEnabled(true);
        }
    }

    private void updateWinner(){
        if (player1.getScore() >= winnerPoints){
            player1NameInput.setBackgroundColor(getResources().getColor(R.color.winner_color));
            player1ClubInput.setBackgroundColor(getResources().getColor(R.color.winner_color));
            player1ScoreView.setTextColor(getResources().getColor(R.color.winner_color));
            newGameButton.setVisibility(View.VISIBLE);
        }else if (player2.getScore() >= winnerPoints){
            player2NameInput.setBackgroundColor(getResources().getColor(R.color.winner_color));
            player2ClubInput.setBackgroundColor(getResources().getColor(R.color.winner_color));
            player2ScoreView.setTextColor(getResources().getColor(R.color.winner_color));
            newGameButton.setVisibility(View.VISIBLE);
        } else {
            //input backgrounds will be ungoldened by updateFocus
            player1ScoreView.setTextColor(AppCompatResources.getColorStateList(this, R.color.text_color_selector));
            player2ScoreView.setTextColor(AppCompatResources.getColorStateList(this, R.color.text_color_selector));
            newGameButton.setVisibility(View.INVISIBLE);
        }
    }


    private int calculatePoints(){
        String newNumberOfBallsString = Objects.requireNonNull(newBallNumberInput.getText()).toString();
        int newNumberOfBalls;
        if (newNumberOfBallsString.isEmpty() ) {
            newNumberOfBalls = table.getNumberOfBalls();
        } else {
            newNumberOfBalls = Integer.parseInt(newNumberOfBallsString);
        }
        return table.setNewNumberOfBallsAndGiveDifference(newNumberOfBalls);

    }
}
