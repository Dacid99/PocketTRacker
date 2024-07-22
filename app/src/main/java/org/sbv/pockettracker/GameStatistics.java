package org.sbv.pockettracker;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

public class GameStatistics {

    public static int[] maxRunIndices(ScoreSheet scoreSheet){
        int[] maxRuns = maxRuns(scoreSheet);
        int[] maxRunsIndices = new int[2];
        maxRunsIndices[0] = scoreSheet.getPlayer1IncrementsList().indexOf(maxRuns[0]);
        maxRunsIndices[1] = scoreSheet.getPlayer2IncrementsList().indexOf(maxRuns[1]);
        System.out.println(Arrays.toString(maxRunsIndices));
        return maxRunsIndices;
    }

    public static int[] maxRuns(ScoreSheet scoreSheet){
        int[] maxRun = new int[2];
        try { //should be separated, here in one because both lists are of same length by design -> exception should be thrown at first line
            maxRun[0] = Collections.max(scoreSheet.getPlayer1IncrementsList());
            maxRun[1] = Collections.max(scoreSheet.getPlayer2IncrementsList());
        } catch (NoSuchElementException e){ //indicating an empty list
            maxRun[0] = 0;
            maxRun[1] = 0;
        }
        return maxRun;
    }

    public static int[] playerInnings(ScoreSheet scoreSheet){
        int[] innings = new int[2];
        innings[0] = (int) Math.ceil(Math.abs(scoreSheet.length()/2.0 - 0.5));
        innings[1] = (int) Math.floor(Math.abs(scoreSheet.length()/2.0 - 0.5));
        return innings;
    }

    public static double[] meanInnings(ScoreSheet scoreSheet){
        double [] meanInnings = new double[2];
        ArrayList<Integer> player1Innings = scoreSheet.getPlayer1IncrementsList() ;
        ArrayList<Integer> player2Innings = scoreSheet.getPlayer2IncrementsList() ;
        double sumPlayer1 = 0.0;
        double sumPlayer2 = 0.0;
        for (int index = 0; index < scoreSheet.length() - 1; index++){
            sumPlayer1 += player1Innings.get(index);
            sumPlayer2 += player2Innings.get(index);
        }

        //scoresheet should never be empty as the constructor of scoresheet set a first element
        meanInnings[0] = (scoreSheet.length()== 1) ? 0 : sumPlayer1 / playerInnings(scoreSheet)[0];
        meanInnings[1] = (scoreSheet.length() <= 2) ? 0 : sumPlayer2 / playerInnings(scoreSheet)[1];

        return meanInnings;
    }

    public static double[] meanRuns(ScoreSheet scoreSheet){
        double [] meanRuns = new double[2];
        ArrayList<Integer> player1Innings = scoreSheet.getPlayer1IncrementsList() ;
        ArrayList<Integer> player2Innings = scoreSheet.getPlayer2IncrementsList() ;
        player1Innings.removeAll(Collections.singleton(0));
        player2Innings.removeAll(Collections.singleton(0));
        double sumPlayer1 = 0.0;
        double sumPlayer2 = 0.0;
        for (int index = 0; index < player1Innings.size(); index++){
            sumPlayer1 += player1Innings.get(index);
        }
        for (int index = 0; index < player2Innings.size(); index++){
            sumPlayer2 += player2Innings.get(index);
        }
        meanRuns[0] = (player1Innings.isEmpty()) ? 0 : sumPlayer1 / player1Innings.size();
        meanRuns[1] = (player2Innings.isEmpty()) ? 0 : sumPlayer2/ player2Innings.size();

        return meanRuns;
    }
}
