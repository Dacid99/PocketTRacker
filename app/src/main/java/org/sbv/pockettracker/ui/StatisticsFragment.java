package org.sbv.pockettracker.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.components.AxisBase;
import com.google.android.material.button.MaterialButton;

import org.sbv.pockettracker.utils.GameStatistics;
import org.sbv.pockettracker.model.Players;
import org.sbv.pockettracker.model.PlayersViewModel;
import org.sbv.pockettracker.R;
import org.sbv.pockettracker.model.ScoreSheet;
import org.sbv.pockettracker.model.ScoreSheetViewModel;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {

    public interface StatisticsFragmentListener {
        public void onScoreSheetButtonClick();
    }

    private StatisticsFragmentListener listener;
    private ScoreSheetViewModel scoreSheetViewModel;
    private PlayersViewModel playersViewModel;
    private TextView player1StatisticsHeader, player2StatisticsHeader, maxRunPlayer1View, maxRunPlayer2View, inningsPlayer1View, inningsPlayer2View, meanInningPlayer1View, meanInningPlayer2View, meanRunPlayer1View, meanRunPlayer2View;
    private final ImageView[] playerScorePlots = new ImageView[2];
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
                putPlots(0, scoreSheet);
                putPlots(1, scoreSheet);
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

    private void putPlots(int playerNumber, ScoreSheet scoreSheet){
        if (playerNumber != 0 && playerNumber != 1){
            return;
        }

        LineChart lineChart = new LineChart(requireContext());

        List<Entry> playerScoreData = new ArrayList<>();
        int index = 0;
        for (ScoreSheet.Inning inning : scoreSheet){
            playerScoreData.add( new Entry(index, inning.playerScores[playerNumber]) );
            index++;
        }
        LineDataSet lineDataSet = new LineDataSet(playerScoreData, getResources().getString(R.string.plotLabel));
        lineDataSet.setColor(getResources().getColor(R.color.plotLineColor));
        lineDataSet.setCircleColor(getResources().getColor(R.color.plotLineColor));
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        lineChart.measure(
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY)
        );
        lineChart.layout(0,0,1000,600);

        lineChart.setDrawGridBackground(true);
        Description description = new Description();
        description.setText(getResources().getString(R.string.scoresPlot_description));

        lineChart.setDescription(description);
        lineChart.setBackgroundColor(getResources().getColor(R.color.background));

        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setDrawInside(false);
        legend.setTextColor(getResources().getColor(R.color.onBackground));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(true);
        xAxis.setLabelRotationAngle(0);
        xAxis.setTextColor(getResources().getColor(R.color.onBackground));

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setTextColor(getResources().getColor(R.color.onBackground));

        lineChart.setDrawingCacheEnabled(true);
        lineChart.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(lineChart.getDrawingCache());
        lineChart.setDrawingCacheEnabled(false);

        playerScorePlots[playerNumber].setImageBitmap(bitmap);
    }
}