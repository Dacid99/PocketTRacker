package org.sbv.pockettracker;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatisticsFragment extends Fragment {

    private ScoreSheetViewModel scoreSheetViewModel;
    private TextView maxRunPlayer1View, maxRunPlayer2View, inningsPlayer1View, inningsPlayer2View, meanInningPlayer1View, meanInningPlayer2View, meanRunPlayer1View, meanRunPlayer2View;

    public static StatisticsFragment newInstance() {
        return new StatisticsFragment();
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

        scoreSheetViewModel = new ViewModelProvider(requireActivity()).get(ScoreSheetViewModel.class);
        scoreSheetViewModel.getScoreSheet().observe(getViewLifecycleOwner(), new Observer<ScoreSheet>() {
            @Override
            public void onChanged(ScoreSheet scoreSheet) {
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
        });

        return view;
    }

}