package org.sbv.pockettracker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.sbv.pockettracker.R;
import org.sbv.pockettracker.model.ScoreSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GamePlotter {

    public static void drawScoresPlots(ImageView[] imageViews, int playerNumber, ScoreSheet scoreSheet){
        if (playerNumber != 0 && playerNumber != 1){
            return;
        }
        Context context = imageViews[playerNumber].getContext();

        LineChart lineChart = new LineChart(context);

        List<Entry> playerScoreData = new ArrayList<>();
        int index = 0;
        for (ScoreSheet.Inning inning : scoreSheet){
            playerScoreData.add( new Entry(index, inning.playerScores[playerNumber]) );
            index++;
        }
        LineDataSet lineDataSet = new LineDataSet(playerScoreData, context.getResources().getString(R.string.scoresPlotLabel));
        lineDataSet.setColor(context.getResources().getColor(R.color.plotLineColor));
        lineDataSet.setCircleColor(context.getResources().getColor(R.color.plotLineColor));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPointLabel(Entry entry) {
                return String.valueOf( (int) entry.getY() );
            }
        });

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        lineChart.measure(
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY)
        );
        lineChart.layout(0,0,1000,600);

        Description description = new Description();
        description.setText(context.getResources().getString(R.string.scoresPlot_description));
        description.setTextColor(context.getResources().getColor(R.color.onBackground));

        lineChart.setDescription(description);
        lineChart.setBackgroundColor(context.getResources().getColor(R.color.background));

        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setDrawInside(false);
        legend.setTextColor(context.getResources().getColor(R.color.onBackground));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(true);
        xAxis.setLabelRotationAngle(0);
        xAxis.setTextColor(context.getResources().getColor(R.color.onBackground));

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setTextColor(context.getResources().getColor(R.color.onBackground));

        lineChart.setDrawingCacheEnabled(true);
        lineChart.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(lineChart.getDrawingCache());
        lineChart.setDrawingCacheEnabled(false);

        imageViews[playerNumber].setImageBitmap(bitmap);
    }

    public static void drawRunsPlots(ImageView[] imageViews, int playerNumber, ScoreSheet scoreSheet){
        if (playerNumber != 0 && playerNumber != 1){
            return;
        }
        Context context = imageViews[playerNumber].getContext();
        HashMap<Integer, Integer> runsHistogram = GameStatistics.getIncrementsHistogram(playerNumber, scoreSheet);
        BarChart barChart = new BarChart(context);

        List<BarEntry> playerScoreData = new ArrayList<>();
        for (HashMap.Entry<Integer, Integer> entry: runsHistogram.entrySet()){
            playerScoreData.add( new BarEntry(entry.getKey(), entry.getValue()) );
        }
        BarDataSet barDataSet = new BarDataSet(playerScoreData, context.getResources().getString(R.string.runsPlotLabel));
        barDataSet.setColor(context.getResources().getColor(R.color.plotLineColor));
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.valueOf( (int) barEntry.getY());
            }
        });

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.measure(
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(600, View.MeasureSpec.EXACTLY)
        );
        barChart.layout(0,0,1000,600);

        Description description = new Description();
        description.setText(context.getResources().getString(R.string.runsPlot_description));
        description.setTextColor(context.getResources().getColor(R.color.onBackground));

        barChart.setDescription(description);
        barChart.setBackgroundColor(context.getResources().getColor(R.color.background));

        Legend legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setDrawInside(false);
        legend.setTextColor(context.getResources().getColor(R.color.onBackground));

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(0);
        xAxis.setTextColor(context.getResources().getColor(R.color.onBackground));

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setTextColor(context.getResources().getColor(R.color.onBackground));

        barChart.setDrawingCacheEnabled(true);
        barChart.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(barChart.getDrawingCache());
        barChart.setDrawingCacheEnabled(false);

        imageViews[playerNumber].setImageBitmap(bitmap);
    }
}
