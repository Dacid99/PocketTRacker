package org.sbv.pockettracker.ui;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.sbv.pockettracker.utils.GamePlotter;
import org.sbv.pockettracker.utils.GameStatistics;
import org.sbv.pockettracker.model.Players;
import org.sbv.pockettracker.model.PlayersViewModel;
import org.sbv.pockettracker.R;
import org.sbv.pockettracker.model.ScoreSheet;
import org.sbv.pockettracker.model.ScoreSheetViewModel;

public class StatisticsFragment extends Fragment {

    public interface StatisticsFragmentListener {
        void onScoreSheetButtonClick();
    }

    private StatisticsFragmentListener listener;
    private ScoreSheetViewModel scoreSheetViewModel;
    private PlayersViewModel playersViewModel;
    private TextView player1StatisticsHeader, player2StatisticsHeader, maxRunPlayer1View, maxRunPlayer2View, inningsPlayer1View, inningsPlayer2View, meanInningPlayer1View, meanInningPlayer2View, meanRunPlayer1View, meanRunPlayer2View;
    private final ImageView[] playerScorePlots = new ImageView[2];
    private final ImageView[] playerRunsPlots = new ImageView[2];
    private MaterialButton toScoreSheetButton;

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        try{
            listener = (StatisticsFragmentListener) context;
        }catch (ClassCastException e) {
            throw new ClassCastException(context + "must implement StatisticsFragmentListener!");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        maxRunPlayer1View = view.findViewById(R.id.player1statistics_maxRun);
        maxRunPlayer2View = view.findViewById(R.id.player2statistics_maxRun);
        inningsPlayer1View = view.findViewById(R.id.player1statistics_innings);
        inningsPlayer2View = view.findViewById(R.id.player2statistics_innings);
        meanInningPlayer1View = view.findViewById(R.id.player1statistics_meanInning);
        meanInningPlayer2View = view.findViewById(R.id.player2statistics_meanInning);
        meanRunPlayer1View = view.findViewById(R.id.player1statistics_meanRun);
        meanRunPlayer2View = view.findViewById(R.id.player2statistics_meanRun);

        playerScorePlots[0] = view.findViewById(R.id.player1ScorePlot);
        playerScorePlots[1] = view.findViewById(R.id.player2ScorePlot);

        playerRunsPlots[0] = view.findViewById(R.id.player1RunsPlot);
        playerRunsPlots[1] = view.findViewById(R.id.player2RunsPlot);

        player1StatisticsHeader = view.findViewById(R.id.player1statistics_header);
        player2StatisticsHeader = view.findViewById(R.id.player2statistics_header);

        scoreSheetViewModel = new ViewModelProvider(requireActivity()).get(ScoreSheetViewModel.class);
        scoreSheetViewModel.getScoreSheet().observe(getViewLifecycleOwner(), new Observer<ScoreSheet>() {
            @Override
            public void onChanged(ScoreSheet scoreSheet) {
                double[] meanInnings = GameStatistics.meanInnings(scoreSheet);
                double[] meanRuns = GameStatistics.meanRuns(scoreSheet);
                int[] playerInnings = scoreSheet.innings();
                int[] maxRuns = GameStatistics.maxRuns(scoreSheet);
                maxRunPlayer1View.setText(getString(R.string.player_maxrun_format, maxRuns[0]));
                maxRunPlayer2View.setText(getString(R.string.player_maxrun_format, maxRuns[1]));
                inningsPlayer1View.setText(getString(R.string.player_innings_format, playerInnings[0]));
                inningsPlayer2View.setText(getString(R.string.player_innings_format, playerInnings[1]));
                meanInningPlayer1View.setText(getString(R.string.meanInning_format, meanInnings[0]));
                meanInningPlayer2View.setText(getString(R.string.meanInning_format, meanInnings[1]));
                meanRunPlayer1View.setText(getString(R.string.meanRun_format, meanRuns[0]));
                meanRunPlayer2View.setText(getString(R.string.meanRun_format, meanRuns[1]));

                GamePlotter.drawScoresPlots(playerScorePlots,0, scoreSheet);
                GamePlotter.drawScoresPlots(playerScorePlots,1, scoreSheet);
                GamePlotter.drawRunsPlots(playerRunsPlots,0, scoreSheet);
                GamePlotter.drawRunsPlots(playerRunsPlots,1, scoreSheet);
            }
        });

        playersViewModel = new ViewModelProvider(requireActivity()).get(PlayersViewModel.class);
        playersViewModel.getPlayers().observe(getViewLifecycleOwner(), new Observer<Players>() {
            @Override
            public void onChanged(Players players) {
                player1StatisticsHeader.setText((players.getNames()[0].isEmpty()) ? getString(R.string.player1_default) : getString(R.string.player_name_format, players.getNames()[0]));
                player2StatisticsHeader.setText((players.getNames()[1].isEmpty()) ? getString(R.string.player2_default) : getString(R.string.player_name_format, players.getNames()[1]));
            }
        });

        toScoreSheetButton = view.findViewById(R.id.toScoreSheetButton);
        toScoreSheetButton.setOnClickListener( v -> listener.onScoreSheetButtonClick());
        return view;
    }

}