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

        putHeaderRow();
        // Add rows
        for (int index = 0; index < scoreSheet.length(); index++) {
            appendTableRow(index);
        }

    }

    private void appendTableRow(int turn){

        TableRow newTableRow = new TableRow(this);
        TableLayout.LayoutParams rowLayoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newTableRow.setLayoutParams(rowLayoutParams);

        TextView turnText = new TextView(this);
        TextView player1Text = new TextView(this);
        TextView player2Text = new TextView(this);
        TextView ballsOnTableText = new TextView(this);

        turnText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        player1Text.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        player2Text.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        ballsOnTableText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));

        turnText.setText(getString(R.string.turnnumber_format, turn));
        player1Text.setText(getString(R.string.player_score_format, scoreSheet.getScoreOfPlayer1At(turn)));
        player2Text.setText(getString(R.string.player_score_format, scoreSheet.getScoreOfPlayer2At(turn)));
        ballsOnTableText.setText(getString(R.string.remainingBalls_format, scoreSheet.getBallsOnTableAt(turn)));

        turnText.setGravity(Gravity.CENTER);
        player1Text.setGravity(Gravity.CENTER);
        player2Text.setGravity(Gravity.CENTER);
        ballsOnTableText.setGravity(Gravity.CENTER);

        turnText.setPadding(4,4,4,4);
        player1Text.setPadding(4,4,4,4);
        player2Text.setPadding(4,4,4,4);
        ballsOnTableText.setPadding(4,4,4,4);

        turnText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.turnnumber_column_weight)));
        player1Text.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player1Score_column_weight)));
        player2Text.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player2Score_column_weight)));
        ballsOnTableText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.ballsOnTable_column_weight)));

        newTableRow.addView(turnText);
        newTableRow.addView(player1Text);
        newTableRow.addView(player2Text);
        newTableRow.addView(ballsOnTableText);

        tableLayout.addView(newTableRow);
    }

    private void putHeaderRow(){
        TableRow newTableRow = new TableRow(this);
        TableLayout.LayoutParams rowLayoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newTableRow.setLayoutParams(rowLayoutParams);

        TextView turnText = new TextView(this);
        TextView player1Text = new TextView(this);
        TextView player2Text = new TextView(this);
        TextView ballsOnTableText = new TextView(this);

        turnText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        player1Text.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        player2Text.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));
        ballsOnTableText.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator));

        turnText.setText(getString(R.string.turnnumber_columnlabel));
        player1Text.setText(getString(R.string.player1_columnlabel));
        player2Text.setText(getString(R.string.player2_columnlabel));
        ballsOnTableText.setText(getString(R.string.ballsOnTable_columnlabel));

        turnText.setGravity(Gravity.CENTER);
        player1Text.setGravity(Gravity.CENTER);
        player2Text.setGravity(Gravity.CENTER);
        ballsOnTableText.setGravity(Gravity.CENTER);

        turnText.setPadding(8,8,8,8);
        player1Text.setPadding(8,8,8,8);
        player2Text.setPadding(8,8,8,8);
        ballsOnTableText.setPadding(8,8,8,8);

        turnText.setTypeface(Typeface.DEFAULT_BOLD);
        player1Text.setTypeface(Typeface.DEFAULT_BOLD);
        player2Text.setTypeface(Typeface.DEFAULT_BOLD);
        ballsOnTableText.setTypeface(Typeface.DEFAULT_BOLD);

        turnText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.turnnumber_column_weight)));
        player1Text.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player1Score_column_weight)));
        player2Text.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player2Score_column_weight)));
        ballsOnTableText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.ballsOnTable_column_weight)));

        newTableRow.addView(turnText);
        newTableRow.addView(player1Text);
        newTableRow.addView(player2Text);
        newTableRow.addView(ballsOnTableText);

        tableLayout.addView(newTableRow);
    }
}