package org.sbv.straightpoolcounter;

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

public class MainActivity extends AppCompatActivity {

    private Player player1, player2, turnPlayer;
    private PoolTable table;

    private ScoreSheet scoreSheet;
    private TextView player1ScoreView, player2ScoreView, ballNumberView;
    private TextInputLayout player1NameLayout, player2NameLayout, newBallNumberLayout;
    private TextInputEditText player1NameInput, player2NameInput, newBallNumberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1 = new Player();
        player2 = new Player();

        turnPlayer = player1;

        table = new PoolTable();

        scoreSheet = new ScoreSheet();

        player1ScoreView = findViewById(R.id.player1Score);
        player2ScoreView = findViewById(R.id.player2Score);

        player1NameLayout = findViewById(R.id.player1NameLayout);
        player1NameInput = findViewById(R.id.player1Name);

        player2NameLayout= findViewById(R.id.player2NameLayout);
        player2NameInput = findViewById(R.id.player2Name);

        ballNumberView = findViewById(R.id.ballNumber);

        newBallNumberLayout = findViewById(R.id.newBallNumberLayout);
        newBallNumberInput = findViewById(R.id.newBallNumberInput);

        MaterialButton missButton = findViewById(R.id.missButton);
        MaterialButton safeButton = findViewById(R.id.safeButton);
        MaterialButton foulButton = findViewById(R.id.foulButton);
        MaterialButton rerackButton = findViewById(R.id.rerackButton);


        ballNumberView.setText(getString(R.string.ball_number_format, table.getNumberOfBalls()));


        player1NameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newName = s.toString();
                player1.setName(newName);
                updateNames();
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
                player2.setName(newName);
                updateNames();
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

            }
        });

        missButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newNumberOfBalls = Integer.parseInt(Objects.requireNonNull(newBallNumberInput.getText()).toString());
                int points = table.setNewNumberOfBallsAndGiveDifference(newNumberOfBalls);
                turnPlayer.addPoints(points);
                updateScores();
                switchFocus(v.toString());
            }
        });

        safeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newNumberOfBalls = Integer.parseInt(Objects.requireNonNull(newBallNumberInput.getText()).toString());
                int points = table.setNewNumberOfBallsAndGiveDifference(newNumberOfBalls);
                turnPlayer.addPoints(points);
                updateScores();
                switchFocus(v.toString());
            }
        });

        foulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newNumberOfBalls = Integer.parseInt(Objects.requireNonNull(newBallNumberInput.getText()).toString());
                int points = table.setNewNumberOfBallsAndGiveDifference(newNumberOfBalls);
                turnPlayer.addPoints(points);
                turnPlayer.deductPoints( (scoreSheet.length() == 0) ? 2:1 );
                updateScores();
                switchFocus(v.toString());
            }
        });

        rerackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = table.getNumberOfBalls() - 1 ;
                turnPlayer.addPoints(points);
                table.rerack();
                updateScores();
            }
        });

        updateScores();
        updateNames();
    }

    private void updateScores() {
        player1ScoreView.setText(getString(R.string.player_score_format, player1.getScore()));
        player2ScoreView.setText(getString(R.string.player_score_format, player2.getScore()));
        ballNumberView.setText(getString(R.string.ball_number_format, table.getNumberOfBalls()));
    }

    private void updateNames(){
        player1NameInput.setText(getString(R.string.player_name_format, player1.getName()));
        player2NameInput.setText(getString(R.string.player_name_format, player2.getName()));
    }

    /*
    private void updateClubs(){
        player1ClubInput.setText(getString(R.string.player_club_format, player1.getClub()));
        player2ClubInput.setText(getString(R.string.player_club_format, player2.getClub()));
    }
    */

    private void switchFocus(String reason){
        if (turnPlayer == player1) {
            turnPlayer = player2;
        }else {
            turnPlayer = player1;
        }
        scoreSheet.writeScoreLine(player1.getScore(), player2.getScore());
        scoreSheet.writeSwitchReason(reason);

    }
}
