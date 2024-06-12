package org.sbv.pockettracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ScoreSheetActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    //private TextView player1Header, player2Header;
    private ScoreSheet scoreSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoresheet);

        Intent intent = getIntent();
        scoreSheet = intent.getParcelableExtra("scoreSheet");

        tableLayout = findViewById(R.id.score_table);

        //player1Header.setText(getString(R.string.player_columnlabel_format, scoreSheet.getPlayer1Name()));
        //player2Header.setText(getString(R.string.player_columnlabel_format, scoreSheet.getPlayer2Name()));


        // Add rows

        for (int index = 0; index < scoreSheet.length(); index++) {
            appendTableRow(index);
        }

    }

    private void appendTableRow(int turn){

        TableRow newTableRow = new TableRow(this);

        TextView turnText = new TextView(this);
        TextView player1Text = new TextView(this);
        TextView player2Text = new TextView(this);
        TextView ballsOnTableText = new TextView(this);

        //float weigth = getResources().getFraction(R.fraction.turnnumber_column_weight ,1, 2);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        turnText.setLayoutParams(layoutParams);
        // layoutParams.weight = getResources().getDimension(R.dimen.player1Score_column_weight);
        player1Text.setLayoutParams(layoutParams);
        // layoutParams.weight = getResources().getDimension(R.dimen.player2Score_column_weight);
        player2Text.setLayoutParams(layoutParams);
        // layoutParams.weight = getResources().getDimension(R.dimen.ballsOnTable_column_weight);
        ballsOnTableText.setLayoutParams(layoutParams);

        turnText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        player1Text.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        player2Text.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        ballsOnTableText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));

        turnText.setText(getString(R.string.turnnumber_format, turn+1));
        player1Text.setText(getString(R.string.player_score_format, scoreSheet.getScoreOfPlayer1At(turn)));
        player2Text.setText(getString(R.string.player_score_format, scoreSheet.getScoreOfPlayer2At(turn)));
        ballsOnTableText.setText(getString(R.string.remainingBalls_format, scoreSheet.getBallsOnTableAt(turn)));

        newTableRow.addView(turnText);
        newTableRow.addView(player1Text);
        newTableRow.addView(player2Text);
        newTableRow.addView(ballsOnTableText);

        tableLayout.addView(newTableRow);
    }
}