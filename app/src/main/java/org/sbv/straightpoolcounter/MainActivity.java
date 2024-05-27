package org.sbv.straightpoolcounter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private Player player1, player2;
    private TextView player1ScoreView, player2ScoreView, player1NameView, player2NameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player1 = new Player("Alice");
        player2 = new Player("Bob");

        player1ScoreView = findViewById(R.id.player1Score);
        player2ScoreView = findViewById(R.id.player2Score);

        player1NameView = findViewById(R.id.player1Name);
        player2NameView = findViewById(R.id.player2Name);

        MaterialButton player1Button = findViewById(R.id.player1Button);
        MaterialButton player2Button = findViewById(R.id.player2Button);

        player1NameView.setText(getString(R.string.player_name_format, player1.getName()));
        player2NameView.setText(getString(R.string.player_name_format, player2.getName()));

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
                updateScores();
            }
        });

        updateScores();
    }

    private void updateScores() {
        player1ScoreView.setText(getString(R.string.player_score_format, player1.getScore()));
        player2ScoreView.setText(getString(R.string.player_score_format, player2.getScore()));
    }
}
