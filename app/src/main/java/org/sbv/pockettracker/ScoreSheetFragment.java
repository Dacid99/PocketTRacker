package org.sbv.pockettracker;


import android.content.Context;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

public class ScoreSheetFragment extends Fragment {
    private TableLayout tableLayout;
    private TextView player1TableHeader, player2TableHeader, player1StatisticsHeader, player2StatisticsHeader;
    private MaterialButton loadGameButton, saveGameButton;
    private ScoreSheetViewModel scoreSheetViewModel;
    private PlayersViewModel playersViewModel;
    private ScoreBoardViewModel scoreBoardViewModel;
    private View view;
    private ScoreSheetFragmentListener listener;

    public interface ScoreSheetFragmentListener{
        void onSaveButtonClick();
        void onLoadButtonClick();
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try{
            listener = (ScoreSheetFragment.ScoreSheetFragmentListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context + "must implement CounterFragmentListener!");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_scoresheet, container, false);

        tableLayout = view.findViewById(R.id.score_table);

        player1TableHeader = view.findViewById(R.id.player1table_header);
        player2TableHeader = view.findViewById(R.id.player2table_header);
        player1StatisticsHeader = view.findViewById(R.id.player1statistics_header);
        player2StatisticsHeader = view.findViewById(R.id.player2statistics_header);

        saveGameButton = view.findViewById(R.id.saveGame);
        loadGameButton = view.findViewById(R.id.loadGame);

        playersViewModel = new ViewModelProvider(requireActivity()).get(PlayersViewModel.class);
        playersViewModel.getPlayers().observe(getViewLifecycleOwner(), new Observer<Players>() {
            @Override
            public void onChanged(Players players) {
                player1TableHeader.setText((players.getNames()[0].isEmpty()) ? getString(R.string.player1_default) : getString(R.string.player_name_format, players.getNames()[0]));
                player1StatisticsHeader.setText((players.getNames()[0].isEmpty()) ? getString(R.string.player1_default) : getString(R.string.player_name_format, players.getNames()[0]));
                player2TableHeader.setText((players.getNames()[1].isEmpty()) ? getString(R.string.player2_default) : getString(R.string.player_name_format, players.getNames()[1]));
                player2StatisticsHeader.setText((players.getNames()[1].isEmpty()) ? getString(R.string.player2_default) : getString(R.string.player_name_format, players.getNames()[1]));
            }
        });

        scoreSheetViewModel = new ViewModelProvider(requireActivity()).get(ScoreSheetViewModel.class);
        scoreSheetViewModel.getScoreSheet().observe(getViewLifecycleOwner(), new Observer<ScoreSheet>() {
            @Override
            public void onChanged(ScoreSheet scoreSheet) {
                fillScoreSheetLayout(scoreSheet);
                highlightScoreSheet(scoreSheet);
            }
        });

        scoreBoardViewModel = new ViewModelProvider(requireActivity()).get(ScoreBoardViewModel.class);
        scoreBoardViewModel.getScoreBoard().observe(getViewLifecycleOwner(), new Observer<ScoreBoard>() {
            @Override
            public void onChanged(ScoreBoard scoreBoard) {
                if (scoreBoard.getWinner() == 1){
                    player1TableHeader.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.cell_separator_winner));
                }
                if (scoreBoard.getWinner() == 2){
                    player2TableHeader.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.cell_separator_winner));
                }
            }
        });

        loadGameButton.setOnClickListener(v -> listener.onLoadButtonClick());

        saveGameButton.setOnClickListener(v -> listener.onSaveButtonClick());

        return view;
    }

    private void fillScoreSheetLayout(ScoreSheet scoreSheet) {
        // Add rows
        for (int index = 0; index < scoreSheet.length(); index++) {
            appendTableRow(index, scoreSheet);
        }
    }

    private void highlightScoreSheet(ScoreSheet scoreSheet){
        if (scoreSheet.currentTurn() >= 0 && scoreSheet.currentTurn() < tableLayout.getChildCount()){
            TableRow turnRow = (TableRow) tableLayout.getChildAt(scoreSheet.currentTurn());
            Drawable background;
            if (scoreSheet.turnplayerNumber() == 0){
                background = ContextCompat.getDrawable(requireContext(), R.drawable.cell_separator_turn);
            }else {
                background = ContextCompat.getDrawable(requireContext(), R.drawable.cell_separator_turnplayer_turn);
            }
            for (int index = 0; index < turnRow.getChildCount(); index++){
                turnRow.getChildAt(index).setBackground(background);
            }
        }else{
            Log.d("Failed ifelse", "ScoreSheetActivity.highlightScoreSheet: check of pointer failed");
        }
    }

    private void appendTableRow(int turn, ScoreSheet scoreSheet) {

        TableRow newTableRow = new TableRow(requireContext());
        TableLayout.LayoutParams rowLayoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newTableRow.setLayoutParams(rowLayoutParams);

        TextView turnText = new TextView(requireContext());
        TextView switchReasonText = new TextView(requireContext());
        TextView player1IncrementText = new TextView(requireContext());
        TextView player1TotalText = new TextView(requireContext());
        TextView player2IncrementText = new TextView(requireContext());
        TextView player2TotalText = new TextView(requireContext());
        TextView ballsOnTableText = new TextView(requireContext());


        turnText.setText(getString(R.string.turnnumber_format, turn));
        switchReasonText.setText(getString(R.string.switchReason_format, scoreSheet.getSwitchReasonAt(turn)));
        //only show increments for turnplayers
        //also not for 0th turn
        Drawable background = ContextCompat.getDrawable(requireContext(), R.drawable.cell_separator);
        if (turn % 2 == 1 ) {
            player1IncrementText.setText(getString(R.string.player_score_format, scoreSheet.getRunOfPlayer1At(turn)));
            background = ContextCompat.getDrawable(requireContext(), R.drawable.cell_separator_turnplayer);
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