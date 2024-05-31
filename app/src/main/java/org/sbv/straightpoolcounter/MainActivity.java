package org.sbv.straightpoolcounter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private Player player1, player2;
    private PoolTable table;
    private TextView player1ScoreView, player2ScoreView, ballNumberView;
    private TextInputLayout player1NameView, player2NameView;
    private TextInputEditText player1NameInput, player2NameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1 = new Player("Alice");
        player2 = new Player("Bob");

        table = new PoolTable();


        player1ScoreView = findViewById(R.id.player1Score);
        player2ScoreView = findViewById(R.id.player2Score);

        player1NameView = findViewById(R.id.player1Name);
        player2NameView = findViewById(R.id.player2Name);

        ballNumberView = findViewById(R.id.ballNumber);

        MaterialButton player1Button = findViewById(R.id.player1Button);
        MaterialButton player2Button = findViewById(R.id.player2Button);

        player1NameView.setText(getString(R.string.player_name_format, player1.getName()));
        player2NameView.setText(getString(R.string.player_name_format, player2.getName()));

        ballNumberView.setText(getString(R.string.ball_number_format, table.getNumberOfBalls()));

        
        player1NameView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String newName = v.getText().toString();
                player1.setName(newName);
                updateNames();
                return true;
            }
        });

        player2NameView.addTextChangedListener(new TextWatcher() {
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

        player1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player1.addPoints(1);
                updateScores();
            }
        });

        player2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player2.addPoints(1);
                table.removeBall();
                updateScores();
            }
        });

        updateScores();
    }

    private void updateScores() {
        player1ScoreView.setText(getString(R.string.player_score_format, player1.getScore()));
        player2ScoreView.setText(getString(R.string.player_score_format, player2.getScore()));
        ballNumberView.setText(getString(R.string.ball_number_format, table.getNumberOfBalls()));

    }

    private void updateNames(){
        player1NameView.setText(getString(R.string.player_name_format, player1.getName()));
        player2NameView.setText(getString(R.string.player_name_format, player2.getName()));
    }

    private void updateClubs(){
        player1NameView.setText(getString(R.string.player_club_format, player1.getClub()));
        player2NameView.setText(getString(R.string.player_club_format, player2.getClub()));
    }
}
