package org.sbv.pockettracker.utils;

import org.sbv.pockettracker.model.ScoreSheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

public class GameStatistics {

    public static int[] getIncrementsAt(int turn, ScoreSheet scoreSheet){
        if (turn <= 0 || turn >= scoreSheet.length()) {
            return new int[]{0,0};
        }
        int[] increments = new int[2];
        int[] scores = scoreSheet.getPlayerScoresAt(turn);
        int[] prevScores = scoreSheet.getPlayerScoresAt(turn - 1 );
        increments[0] = scores[0] - prevScores[0];
        increments[1] = scores[1] - prevScores[1];
        return increments;
    }

    public static ArrayList<Integer>[] getPlayerIncrementsList(ScoreSheet scoreSheet){
        ArrayList<Integer>[] playerIncrementsList = new ArrayList[2];
        playerIncrementsList[0] = new ArrayList<>();
        playerIncrementsList[1] = new ArrayList<>();
        int[] run;
        for (int index =  1; index<scoreSheet.length(); index++){
            run = getIncrementsAt(index, scoreSheet);
            playerIncrementsList[0].add(run[0]);
            playerIncrementsList[1].add(run[1]);
        }
        return playerIncrementsList;
    }


    public static int[] maxRunIndices(ScoreSheet scoreSheet){
        int[] maxRuns = maxRuns(scoreSheet);
        int[] maxRunsIndices = new int[2];
        ArrayList<Integer>[] incrementsList = getPlayerIncrementsList(scoreSheet);
        maxRunsIndices[0] = incrementsList[0].indexOf(maxRuns[0]);
        maxRunsIndices[1] = incrementsList[1].indexOf(maxRuns[1]);
        return maxRunsIndices;
    }

    public static int[] maxRuns(ScoreSheet scoreSheet){
        int[] maxRun = new int[2];
        ArrayList<Integer>[] incrementsList = getPlayerIncrementsList(scoreSheet);
        try { //should be separated, here in one because both lists are of same length by design -> exception should be thrown at first line
            maxRun[0] = Collections.max(incrementsList[0]);
            maxRun[1] = Collections.max(incrementsList[1]);
        } catch (NoSuchElementException e){ //indicating an empty list
            maxRun[0] = 0;
            maxRun[1] = 0;
        }
        return maxRun;
    }

    public static double[] meanInnings(ScoreSheet scoreSheet){
        double [] meanInnings = new double[2];
        ArrayList<Integer>[] playerInnings = getPlayerIncrementsList(scoreSheet) ;
        double sumPlayer1 = 0.0;
        double sumPlayer2 = 0.0;
        for (int index = 0; index < scoreSheet.length() - 1; index++){
            sumPlayer1 += playerInnings[0].get(index);
            sumPlayer2 += playerInnings[1].get(index);
        }

        //scoresheet should never be empty as the constructor of scoresheet set a first element
        meanInnings[0] = (scoreSheet.length() <= 1) ? 0 : sumPlayer1 / scoreSheet.innings()[0];
        meanInnings[1] = (scoreSheet.length() <= 2) ? 0 : sumPlayer2 / scoreSheet.innings()[1];

        return meanInnings;
    }

    public static double[] meanRuns(ScoreSheet scoreSheet){
        double [] meanRuns = new double[2];
        ArrayList<Integer>[] playerInnings = getPlayerIncrementsList(scoreSheet) ;
        double sumPlayer1 = 0.0;
        double sumPlayer2 = 0.0;
        for (int index = 0; index < scoreSheet.length() - 1; index++){
            sumPlayer1 += playerInnings[0].get(index);
            sumPlayer2 += playerInnings[1].get(index);
        }

        playerInnings[0].removeAll(Collections.singleton(0));
        playerInnings[1].removeAll(Collections.singleton(0));

        meanRuns[0] = (playerInnings[0].isEmpty()) ? 0 : sumPlayer1 / playerInnings[0].size();
        meanRuns[1] = (playerInnings[1].isEmpty()) ? 0 : sumPlayer2/ playerInnings[1].size();

        return meanRuns;
    }
}
