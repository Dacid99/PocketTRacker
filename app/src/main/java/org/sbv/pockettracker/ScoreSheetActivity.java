package org.sbv.pockettracker;


import android.content.Intent;

import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;

public class ScoreSheetActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private TextView player1TableHeader, player2TableHeader, player1StatisticsHeader, player2StatisticsHeader;
    private TextView maxRunPlayer1View, maxRunPlayer2View, inningsPlayer1View, inningsPlayer2View, meanInningPlayer1View, meanInningPlayer2View, meanRunPlayer1View, meanRunPlayer2View;
    private ScoreSheet scoreSheet;
    private Player player1, player2;
    private MaterialToolbar toolbar;
    private MaterialButton counterButton, scoreSheetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoresheet);

        Intent intent = getIntent();
        scoreSheet = intent.getParcelableExtra(MainActivity.SCORESHEETPARAMETER);
        Player player1 = intent.getParcelableExtra(MainActivity.PLAYER1PARAMETER);
        Player player2 = intent.getParcelableExtra(MainActivity.PLAYER2PARAMETER);
        assert player1 != null;
        assert player2 != null;

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        counterButton = findViewById(R.id.counter_button);
        scoreSheetButton = findViewById(R.id.scoresheet_button);
        //deactivate scoreSheet button in this activity
        scoreSheetButton.setClickable(false);
        scoreSheetButton.setTextColor(getResources().getColor(R.color.current_activity_color));

        counterButton.setOnClickListener(v -> finish());


        tableLayout = findViewById(R.id.score_table);

        player1TableHeader = findViewById(R.id.player1table_header);
        player2TableHeader = findViewById(R.id.player2table_header);
        player1StatisticsHeader = findViewById(R.id.player1statistics_header);
        player2StatisticsHeader = findViewById(R.id.player2statistics_header);

        player1TableHeader.setText(getString(R.string.player_name_format, player1.getName()));
        player1StatisticsHeader.setText(getString(R.string.player_name_format, player1.getName()));

        player2TableHeader.setText(getString(R.string.player_name_format, player2.getName()));
        player2StatisticsHeader.setText(getString(R.string.player_name_format, player2.getName()));


        maxRunPlayer1View = findViewById(R.id.player1statistics_maxRun);
        maxRunPlayer2View = findViewById(R.id.player2statistics_maxRun);
        inningsPlayer1View = findViewById(R.id.player1statistics_innings);
        inningsPlayer2View = findViewById(R.id.player2statistics_innings);
        meanInningPlayer1View = findViewById(R.id.player1statistics_meanInning);
        meanInningPlayer2View = findViewById(R.id.player2statistics_meanInning);
        meanRunPlayer1View = findViewById(R.id.player1statistics_meanRun);
        meanRunPlayer2View = findViewById(R.id.player2statistics_meanRun);


        fillScoreSheetLayout();
        highlightScoreSheet();
    }

    private void fillScoreSheetLayout() {
        // Add rows
        for (int index = 0; index < scoreSheet.length(); index++) {
            appendTableRow(index);
        }
        double[] meanInnings = GameStatistics.meanInnings(scoreSheet);
        double[] meanRuns = GameStatistics.meanRuns(scoreSheet);
        int[] playerInnings = GameStatistics.playerInnings(scoreSheet);
        int[] maxRuns = GameStatistics.maxRuns(scoreSheet);
        maxRunPlayer1View.setText(getString(R.string.player_maxrun_format, maxRuns[0]));
        maxRunPlayer2View.setText(getString(R.string.player_maxrun_format, maxRuns[1]));
        inningsPlayer1View.setText(getString(R.string.player_innings_format, playerInnings[0]));
        inningsPlayer2View.setText(getString(R.string.player_innings_format, playerInnings[1]));
        meanInningPlayer1View.setText(getString(R.string.meanInning_format, meanInnings[0]));
        meanInningPlayer2View.setText(getString(R.string.meanInning_format, meanInnings[1]));
        meanRunPlayer1View.setText(getString(R.string.meanRun_format, meanRuns[0]));
        meanRunPlayer2View.setText(getString(R.string.meanRun_format, meanRuns[1]));
    }

    private void highlightScoreSheet(){
        if (scoreSheet.currentTurn() >= 0 && scoreSheet.currentTurn() < tableLayout.getChildCount()){
            TableRow turnRow = (TableRow) tableLayout.getChildAt(scoreSheet.currentTurn());
            Drawable background;
            if (scoreSheet.isPlayer1Turn()){
                background = ContextCompat.getDrawable(this, R.drawable.cell_separator_turn);
            }else {
                background = ContextCompat.getDrawable(this, R.drawable.cell_separator_turnplayer_turn);
            }
            for (int index = 0; index < turnRow.getChildCount(); index++){
                turnRow.getChildAt(index).setBackground(background);
            }
        }else{
            Log.d("Failed ifelse", "ScoreSheetActivity.highlightScoreSheet: check of pointer failed");
        }
        if (player1.isWinner()){
            player1TableHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator_winner));
            player1StatisticsHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator_winner));
        }
        if (player2.isWinner()){
            player2TableHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator_winner));
            player2StatisticsHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.cell_separator_winner));
        }
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

        turnText.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        switchReasonText.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        player1IncrementText.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        player1TotalText.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        player2IncrementText.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        player2TotalText.setTextSize(getResources().getDimension(R.dimen.tableTextSize));
        ballsOnTableText.setTextSize(getResources().getDimension(R.dimen.tableTextSize));

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