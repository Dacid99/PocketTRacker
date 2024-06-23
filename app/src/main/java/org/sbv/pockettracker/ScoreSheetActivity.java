package org.sbv.pockettracker;

import android.app.Activity;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ScoreSheetActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private TextView player1TableHeader, player2TableHeader, player1StatisticsHeader, player2StatisticsHeader;
    private TextView maxRunPlayer1View, maxRunPlayer2View, inningsPlayer1View, inningsPlayer2View, meanInningPlayer1View, meanInningPlayer2View, meanRunPlayer1View, meanRunPlayer2View;
    private MaterialButton saveloadButton;
    private ScoreSheet scoreSheet;
    private GameStatistics gameStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoresheet);

        Intent intent = getIntent();
        scoreSheet = intent.getParcelableExtra("scoreSheet");
        String player1Name = intent.getStringExtra("player1Name");  //just to be safe; this should not be necessary as playernames should never be uninitialized
        player1Name = player1Name==null ? "" : player1Name;
        String player2Name = intent.getStringExtra("player2Name");
        player2Name = player2Name==null ? "" : player2Name;


        gameStatistics = new GameStatistics(scoreSheet);

        tableLayout = findViewById(R.id.score_table);

        player1TableHeader = findViewById(R.id.player1table_header);
        player2TableHeader = findViewById(R.id.player2table_header);
        player1StatisticsHeader = findViewById(R.id.player1statistics_header);
        player2StatisticsHeader = findViewById(R.id.player2statistics_header);

        if (!player1Name.isEmpty()) {
            player1TableHeader.setText(getString(R.string.player_name_format, player1Name));
            player1StatisticsHeader.setText(getString(R.string.player_name_format, player1Name));
        }
        if (!player2Name.isEmpty()) {
            player2TableHeader.setText(getString(R.string.player_name_format, player2Name));
            player2StatisticsHeader.setText(getString(R.string.player_name_format, player2Name));
        }

        // Add rows
        for (int index = 0; index < scoreSheet.length(); index++) {
            appendTableRow(index);
        }

        saveloadButton = findViewById(R.id.saveload_button);

        if (scoreSheet.length() > 1) {
            saveloadButton.setText(getResources().getString(R.string.saveGame_string));
            saveloadButton.setOnClickListener(v -> openCreateDocumentIntent());  //save if game has started
        }else{
            saveloadButton.setText(getResources().getString(R.string.loadGame_string));
            saveloadButton.setOnClickListener(v -> openReadDocumentIntent());   //load if game has not started
        }

        maxRunPlayer1View = findViewById(R.id.player1statistics_maxRun);
        maxRunPlayer2View = findViewById(R.id.player2statistics_maxRun);
        inningsPlayer1View = findViewById(R.id.player1statistics_innings);
        inningsPlayer2View = findViewById(R.id.player2statistics_innings);
        meanInningPlayer1View = findViewById(R.id.player1statistics_meanInning);
        meanInningPlayer2View = findViewById(R.id.player2statistics_meanInning);
        meanRunPlayer1View = findViewById(R.id.player1statistics_meanRun);
        meanRunPlayer2View = findViewById(R.id.player2statistics_meanRun);

        maxRunPlayer1View.setText(getString(R.string.player_maxrun_format, gameStatistics.maxRunPlayer1()));
        maxRunPlayer2View.setText(getString(R.string.player_maxrun_format, gameStatistics.maxRunPlayer2()));
        inningsPlayer1View.setText(getString(R.string.player_innings_format, gameStatistics.player1Innings()));
        inningsPlayer2View.setText(getString(R.string.player_innings_format, gameStatistics.player2Innings()));
        meanInningPlayer1View.setText(getString(R.string.meanInning_format, gameStatistics.meanInningPlayer1()));
        meanInningPlayer2View.setText(getString(R.string.meanInning_format, gameStatistics.meanInningPlayer2()));
        meanRunPlayer1View.setText(getString(R.string.meanRun_format, gameStatistics.meanRunPlayer1()));
        meanRunPlayer2View.setText(getString(R.string.meanRun_format, gameStatistics.meanRunPlayer2()));
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

    private void openCreateDocumentIntent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, ScoreSheetIO.REQUEST_CODE_PERMISSIONS);
            }
        }
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getID()));
        String nameProposal = "game_" + formatter.format(now) + ".csv";
        intent.putExtra(Intent.EXTRA_TITLE, nameProposal);
        startActivityForResult(intent, ScoreSheetIO.REQUEST_CODE_CREATE_DOCUMENT);
    }

    private void openReadDocumentIntent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ScoreSheetIO.REQUEST_CODE_PERMISSIONS);
            }
        }
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        startActivityForResult(intent, ScoreSheetIO.REQUEST_CODE_READ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ScoreSheetIO.REQUEST_CODE_CREATE_DOCUMENT && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                try (OutputStream outputStream = getContentResolver().openOutputStream(uri);
                     OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)){
                    ScoreSheetIO.writeToFile(outputStreamWriter, scoreSheet);
                    Toast.makeText(this, "Game saved successfully!", Toast.LENGTH_SHORT).show();
                } catch(IOException e){
                    Toast.makeText(this, "Failed to save game:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
        if (requestCode == ScoreSheetIO.REQUEST_CODE_READ && resultCode == Activity.RESULT_OK){
            if (data != null && data.getData() != null){
                Uri uri = data.getData();
                try (InputStream inputStream = getContentResolver().openInputStream(uri);
                     InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                    ScoreSheetIO.loadFromFile(inputStreamReader, scoreSheet);
                    Toast.makeText(this, "Game loaded successfully!", Toast.LENGTH_SHORT).show();
                }catch (IOException e){
                    Toast.makeText(this, "Failed to load game:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}