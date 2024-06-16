package org.sbv.pockettracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;

public class MainActivity extends AppCompatActivity implements NumberPaneFragment.CustomDialogListener{

    private Player player1, player2, turnPlayer;
    private PoolTable table;
    private ScoreSheet scoreSheet;
    private TextView player1ScoreView, player2ScoreView, ballNumberView, newBallNumberView;
    private TextInputLayout player1NameLayout, player2NameLayout, player1ClubLayout, player2ClubLayout, winningPointsLayout;
    private TextInputEditText player1NameInput, player2NameInput, player1ClubInput, player2ClubInput, winningPointsInput;
    private MaterialCardView player1Card, player2Card;
    private MaterialButton foulButton, missButton, safeButton, redoButton, undoButton, newGameButton, swapPlayersButton, viewScoreSheetButton;
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

        player1Card = findViewById(R.id.player1CardView);
        player2Card = findViewById(R.id.player2CardView);

        ballNumberView = findViewById(R.id.ballNumber);

        newBallNumberView = findViewById(R.id.newBallNumberView);

        winningPointsLayout = findViewById(R.id.winningPointsLayout);
        winningPointsInput = findViewById(R.id.winningPointsInput);

        winnerPoints = 40; //default value
        System.out.println(winnerPoints);
        winningPointsInput.setText(getString(R.string.winnerPoints_format, winnerPoints));

        missButton = findViewById(R.id.missButton);
        safeButton = findViewById(R.id.safeButton);
        foulButton = findViewById(R.id.foulButton);
        undoButton = findViewById(R.id.undoButton);
        redoButton = findViewById(R.id.redoButton);
        newGameButton = findViewById(R.id.newGame);
        swapPlayersButton = findViewById(R.id.swapPlayers);
        viewScoreSheetButton = findViewById(R.id.viewScoreSheet);

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

        newBallNumberView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                NumberPaneFragment numberPaneFragment = NumberPaneFragment.newInstance(table.getNumberOfBalls());
                numberPaneFragment.show(getSupportFragmentManager(), "NumberPane");
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
                        winningPointsInput.setTextColor(getResources().getColor(R.color.score_color));
                    } else {
                        winningPointsInput.setTextColor(getResources().getColor(R.color.warning_color));
                    }

                } else {
                    winningPointsInput.setTextColor(getResources().getColor(R.color.warning_color));
                }
            }
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
            turnPlayer.addPoints( (scoreSheet.length() == 0) ? -2:-1 );
            newTurn(getString(R.string.foul_string));
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

        viewScoreSheetButton.setOnClickListener(v -> {
            if (scoreSheet.isHealthy()) {
                Intent intent = new Intent(MainActivity.this, ScoreSheetActivity.class);
                intent.putExtra("scoreSheet", scoreSheet);
                startActivity(intent);
            }
        });
    }

    private void updateScoreUI() {
        player1ScoreView.setText(getString(R.string.player_score_format, player1.getScore()));
        player2ScoreView.setText(getString(R.string.player_score_format, player2.getScore()));
        ballNumberView.setText(getString(R.string.ballsOnTable_format, table.getNumberOfBalls()));
        newBallNumberView.setText(getString(R.string.newBallNumber_format, table.getNumberOfBalls()));

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
    }

    private void updatePlayerUI(){
        player1NameInput.setText(getString(R.string.player_name_format, player1.getName()));
        player2NameInput.setText(getString(R.string.player_name_format, player2.getName()));
        player1ClubInput.setText(getString(R.string.player_club_format, player1.getClub()));
        player2ClubInput.setText(getString(R.string.player_club_format, player2.getClub()));
    }



    private void newTurn(String reason){
        switchTurnPlayer();
        scoreSheet.update(reason);
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
            player1Card.setCardBackgroundColor(getResources().getColor(R.color.turnplayer_color));
            player1Card.setCardElevation(10);
            player1ScoreView.setEnabled(true);
            player2Card.setCardBackgroundColor(getResources().getColor(R.color.notturnplayer_color));
            player2Card.setCardElevation(0);
            player2ScoreView.setEnabled(false);
        }else {
            player1Card.setCardBackgroundColor(getResources().getColor(R.color.notturnplayer_color));
            player1Card.setCardElevation(0);
            player1ScoreView.setEnabled(false);
            player2Card.setCardBackgroundColor(getResources().getColor(R.color.turnplayer_color));
            player2Card.setCardElevation(10);
            player2ScoreView.setEnabled(true);
        }
    }

    private void updateWinner(){
        if (player1.getScore() >= winnerPoints){
            player1Card.setCardBackgroundColor(getResources().getColor(R.color.winner_color));
            newGameButton.setVisibility(View.VISIBLE);
        }else if (player2.getScore() >= winnerPoints){
            player2Card.setCardBackgroundColor(getResources().getColor(R.color.winner_color));
            newGameButton.setVisibility(View.VISIBLE);
        } else {
            //card backgrounds will be ungoldened by updateFocus
            updateFocus();
            newGameButton.setVisibility(View.INVISIBLE);
        }
    }


    private void assignPoints(){
        String newNumberOfBallsString = Objects.requireNonNull(newBallNumberView.getText()).toString();
        int newNumberOfBalls;
        if (newNumberOfBallsString.isEmpty() ) {
            newNumberOfBalls = table.getNumberOfBalls();
        } else {
            newNumberOfBalls = Integer.parseInt(newNumberOfBallsString);
        }
        if (table.isValidBallNumber(newNumberOfBalls)) {
            int points = table.setNewNumberOfBallsAndGiveDifference(newNumberOfBalls);
            turnPlayer.addPoints(points);
        }
    }

    //numberpanelistener methods
    @Override
    public void onDialogClick(int number){
        if (number == 1) {
            int points = table.getNumberOfBalls() - 1;
            turnPlayer.addPoints(points);
            table.rerack();
            updateScoreUI();
        }
        newBallNumberView.setText(getString(R.string.newBallNumber_format, number));
    }
}
