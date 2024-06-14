package org.sbv.pockettracker;

import android.app.admin.DevicePolicyResourcesManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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
    private GameStatistics gameStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoresheet);

        Intent intent = getIntent();
        scoreSheet = intent.getParcelableExtra("scoreSheet");
        gameStatistics = new GameStatistics(scoreSheet);

        tableLayout = findViewById(R.id.score_table);

        // Add rows
        for (int index = 0; index < scoreSheet.length(); index++) {
            appendTableRow(index);
        }

    }

    private void appendTableRow(int turn) {

        TableRow newTableRow = new TableRow(this);
        TableLayout.LayoutParams rowLayoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newTableRow.setLayoutParams(rowLayoutParams);

        TextView turnText = new TextView(this);
        TextView player1IncrementText = new TextView(this);
        TextView player1TotalText = new TextView(this);
        TextView player2IncrementText = new TextView(this);
        TextView player2TotalText = new TextView(this);
        TextView ballsOnTableText = new TextView(this);

        turnText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        player1IncrementText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        player1TotalText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        player2IncrementText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        player2TotalText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        ballsOnTableText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));

        turnText.setText(getString(R.string.turnnumber_format, turn));
        player1IncrementText.setText(getString(R.string.player_score_format, scoreSheet.getRunOfPlayer1At(turn)));
        player1TotalText.setText(getString(R.string.player_score_format, scoreSheet.getScoreOfPlayer1At(turn)));
        player2IncrementText.setText(getString(R.string.player_score_format, scoreSheet.getRunOfPlayer2At(turn)));
        player2TotalText.setText(getString(R.string.player_score_format, scoreSheet.getScoreOfPlayer2At(turn)));
        ballsOnTableText.setText(getString(R.string.remainingBalls_format, scoreSheet.getBallsOnTableAt(turn)));

        turnText.setGravity(Gravity.CENTER);
        player1IncrementText.setGravity(Gravity.CENTER);
        player1TotalText.setGravity(Gravity.CENTER);
        player2IncrementText.setGravity(Gravity.CENTER);
        player2TotalText.setGravity(Gravity.CENTER);
        ballsOnTableText.setGravity(Gravity.CENTER);

        turnText.setPadding(4, 4, 4, 4);
        player1IncrementText.setPadding(4, 4, 4, 4);
        player1TotalText.setPadding(4, 4, 4, 4);
        player2IncrementText.setPadding(4, 4, 4, 4);
        player2TotalText.setPadding(4, 4, 4, 4);
        ballsOnTableText.setPadding(4, 4, 4, 4);

        turnText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.turnnumber_column_weight)));
        player1IncrementText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player1Increment_column_weight)));
        player1TotalText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player1Total_column_weight)));
        player2IncrementText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player2Increment_column_weight)));
        player2TotalText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player2Total_column_weight)));
        ballsOnTableText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.ballsOnTable_column_weight)));

        newTableRow.addView(turnText);
        newTableRow.addView(player1IncrementText);
        newTableRow.addView(player1TotalText);
        newTableRow.addView(player2IncrementText);
        newTableRow.addView(player2TotalText);
        newTableRow.addView(ballsOnTableText);

        tableLayout.addView(newTableRow);
    }
}