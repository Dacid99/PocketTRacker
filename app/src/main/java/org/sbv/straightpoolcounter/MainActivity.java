package org.sbv.straightpoolcounter;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
    private TextInputLayout player1NameLayout, player2NameLayout, player1ClubLayout, player2ClubLayout, newBallNumberLayout;
    private TextInputEditText player1NameInput, player2NameInput, player1ClubInput, player2ClubInput, newBallNumberInput;
    private MaterialButton redoButton, undoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1 = new Player();
        player2 = new Player();

        turnPlayer = player1;

        table = new PoolTable();

        scoreSheet = new ScoreSheet(table, player1, player2);

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

        MaterialButton missButton = findViewById(R.id.missButton);
        MaterialButton safeButton = findViewById(R.id.safeButton);
        MaterialButton foulButton = findViewById(R.id.foulButton);
        MaterialButton rerackButton = findViewById(R.id.rerackButton);
        undoButton = findViewById(R.id.undoButton);
        redoButton = findViewById(R.id.redoButton);

        ballNumberView.setText(getString(R.string.ball_number_format, table.getNumberOfBalls()));


        player1NameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newName = s.toString();
                if (!newName.equals( player1NameInput.getText().toString() ) ) {
                    player1.setName(newName);
                    updateNameUI();
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
                if (!newName.equals( player2NameInput.getText().toString() ) ) {
                    player2.setName(newName);
                    updateNameUI();
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
                        newBallNumberInput.setTextColor(getResources().getColor(R.color.red));
                    }
                    else {
                        newBallNumberInput.setTextColor(getResources().getColor(R.color.black));
                    }
                }
                else {
                    newBallNumberInput.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });

        missButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = calculatePoints();
                if (points == -1) {
                    return;
                }
                turnPlayer.addPoints(points);
                updateScoreUI();
                newTurn(getString(R.string.miss_string));
            }
        });

        safeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = calculatePoints();
                if (points == -1) {
                    return;
                }
                turnPlayer.addPoints(points);
                newTurn(getString(R.string.safe_string));
            }
        });

        foulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = calculatePoints();
                if (points == -1) {
                    return;
                }
                turnPlayer.addPoints(points);
                turnPlayer.addPoints( (scoreSheet.length() == 0) ? -2:-1 );
                newTurn(getString(R.string.foul_string));
            }
        });

        rerackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = table.getNumberOfBalls() - 1 ;
                turnPlayer.addPoints(points);
                table.rerack();
                updateScoreUI();
            }
        });

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreSheet.rollback();
                switchTurnPlayer();
                updateScoreUI();
            }
        });

        redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scoreSheet.progress();
                switchTurnPlayer();
                updateScoreUI();
            }
        });

        updateScoreUI();
        updateNameUI();
        updateClubUI();
        updateFocusUI();
    }

    private void updateScoreUI() {
        player1ScoreView.setText(getString(R.string.player_score_format, player1.getScore()));
        player2ScoreView.setText(getString(R.string.player_score_format, player2.getScore()));
        ballNumberView.setText(getString(R.string.ball_number_format, table.getNumberOfBalls()));
        newBallNumberInput.setHint(getString(R.string.newBallNumber_hint_format, table.getNumberOfBalls()));
        newBallNumberInput.setText("");
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

    private void updateNameUI(){
        player1NameInput.setText(getString(R.string.player_name_format, player1.getName()));
        player2NameInput.setText(getString(R.string.player_name_format, player2.getName()));
    }


    private void updateClubUI(){
        player1ClubInput.setText(getString(R.string.player_club_format, player1.getClub()));
        player2ClubInput.setText(getString(R.string.player_club_format, player2.getClub()));
    }


    private void newTurn(String reason){
        switchTurnPlayer();
        scoreSheet.update();
        scoreSheet.writeSwitchReason(reason);
        updateScoreUI();
    }

    private void switchTurnPlayer(){
        if (turnPlayer == player1) {
            turnPlayer = player2;
        }else {
            turnPlayer = player1;
        }
        updateFocusUI();
    }

    private void updateFocusUI(){
        if (turnPlayer == player1) {
            player1NameInput.setBackgroundColor(Color.GREEN);
            player2NameInput.setBackgroundColor(Color.LTGRAY);
        }else {
            player1NameInput.setBackgroundColor(Color.LTGRAY);
            player2NameInput.setBackgroundColor(Color.GREEN);
        }
    }

    private int calculatePoints(){
        String newNumberOfBallsString = Objects.requireNonNull(newBallNumberInput.getText()).toString();
        int newNumberOfBalls;
        if (!newNumberOfBallsString.isEmpty() ) {
            newNumberOfBalls = Integer.parseInt(newNumberOfBallsString);
        } else {
            newNumberOfBalls = table.getNumberOfBalls();
        }
        return table.setNewNumberOfBallsAndGiveDifference(newNumberOfBalls);

    }
}
