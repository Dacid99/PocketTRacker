package org.sbv.pockettracker;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ScoreSheetActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    //private TextView player1Header, player2Header;

    private TextView maxRunPlayer1View, maxRunPlayer2View, inningsPlayer1View, inningsPlayer2View;
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

        maxRunPlayer1View = findViewById(R.id.player1statistics_maxrun);
        maxRunPlayer2View = findViewById(R.id.player2statistics_maxrun);
        inningsPlayer1View = findViewById(R.id.player1statistics_innings);
        inningsPlayer2View = findViewById(R.id.player2statistics_innings);

        maxRunPlayer1View.setText(getString(R.string.player_score_format, gameStatistics.maxRunPlayer1()));
        maxRunPlayer2View.setText(getString(R.string.player_score_format, gameStatistics.maxRunPlayer2()));
        inningsPlayer1View.setText(getString(R.string.player_score_format, gameStatistics.player1Innings()));
        inningsPlayer2View.setText(getString(R.string.player_score_format, gameStatistics.player2Innings()));
    }

    private void appendTableRow(int turn) {

        TableRow newTableRow = new TableRow(this);
        TableLayout.LayoutParams rowLayoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newTableRow.setLayoutParams(rowLayoutParams);

        TextView turnText = new TextView(this);
        TextView switchReasonText = new TextView(this);
        TextView player1IncrementText = new TextView(this);
        TextView player1TotalText = new TextView(this);
        TextView player2IncrementText = new TextView(this);
        TextView player2TotalText = new TextView(this);
        TextView ballsOnTableText = new TextView(this);


        turnText.setText(getString(R.string.turnnumber_format, turn));
        switchReasonText.setText(getString(R.string.switchReason_format, scoreSheet.getSwitchReasonAt(turn)));
        //only show increments for turnplayers
        //also not for 0th turn
        Drawable background = ContextCompat.getDrawable(this, R.drawable.cell_separator);
        if (turn % 2 == 1 ) {
            player1IncrementText.setText(getString(R.string.player_score_format, scoreSheet.getRunOfPlayer1At(turn)));
            background = ContextCompat.getDrawable(this, R.drawable.cell_separator_turnplayer);
        } else if (turn != 0){
            player2IncrementText.setText(getString(R.string.player_score_format, scoreSheet.getRunOfPlayer2At(turn)));
        }
        player1TotalText.setText(getString(R.string.player_score_format, scoreSheet.getScoreOfPlayer1At(turn)));
        player2TotalText.setText(getString(R.string.player_score_format, scoreSheet.getScoreOfPlayer2At(turn)));
        ballsOnTableText.setText(getString(R.string.remainingBalls_format, scoreSheet.getBallsOnTableAt(turn)));

        turnText.setBackground( background);
        switchReasonText.setBackground( background);
        player1IncrementText.setBackground( background);
        player1TotalText.setBackground( background);
        player2IncrementText.setBackground(background);
        player2TotalText.setBackground( background);
        ballsOnTableText.setBackground( background);

        turnText.setGravity(Gravity.CENTER);
        switchReasonText.setGravity(Gravity.CENTER);
        player1IncrementText.setGravity(Gravity.CENTER);
        player1TotalText.setGravity(Gravity.CENTER);
        player2IncrementText.setGravity(Gravity.CENTER);
        player2TotalText.setGravity(Gravity.CENTER);
        ballsOnTableText.setGravity(Gravity.CENTER);

        turnText.setPadding(4, 4, 4, 4);
        switchReasonText.setPadding(4, 4, 4, 4);
        player1IncrementText.setPadding(4, 4, 4, 4);
        player1TotalText.setPadding(4, 4, 4, 4);
        player2IncrementText.setPadding(4, 4, 4, 4);
        player2TotalText.setPadding(4, 4, 4, 4);
        ballsOnTableText.setPadding(4, 4, 4, 4);

        turnText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.turnnumber_column_weight)));
        switchReasonText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.switchReason_column_weight)));
        player1IncrementText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player1Increment_column_weight)));
        player1TotalText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player1Total_column_weight)));
        player2IncrementText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player2Increment_column_weight)));
        player2TotalText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.player2Total_column_weight)));
        ballsOnTableText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) getResources().getInteger(R.integer.ballsOnTable_subcolumn_weight)));

        newTableRow.addView(turnText);
        newTableRow.addView(switchReasonText);
        newTableRow.addView(player1IncrementText);
        newTableRow.addView(player1TotalText);
        newTableRow.addView(player2IncrementText);
        newTableRow.addView(player2TotalText);
        newTableRow.addView(ballsOnTableText);


        tableLayout.addView(newTableRow);
    }
}