package org.sbv.pockettracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

public class GameStatistics {
    final ScoreSheet scoreSheet ;

    public GameStatistics(ScoreSheet scoreSheet){
        this.scoreSheet = scoreSheet;
    }

    public int maxRunPlayer1(){
        int maxRun;
        try {
            maxRun = Collections.max(scoreSheet.getPlayer1IncrementsList());
        } catch (NoSuchElementException e){ //indicating an empty list
            maxRun = 0;
        }
        return maxRun;
    }
    public int maxRunPlayer2(){
        int maxRun;
        try {
            maxRun = Collections.max(scoreSheet.getPlayer2IncrementsList());
        } catch (NoSuchElementException e){
            maxRun = 0;
        }
        return maxRun;
    }

    public int player1Innings(){
        return (int) Math.ceil(Math.abs(scoreSheet.length()/2.0 - 0.5));
    }

    public int player2Innings(){
        return (int) Math.floor(Math.abs(scoreSheet.length()/2.0 - 0.5));
    }

    public double meanInningPlayer1(){
        ArrayList<Integer> player1Innings = scoreSheet.getPlayer1IncrementsList() ;
        double sum = 0.0;
        for (int inning : player1Innings){
            sum += inning;
        }
        return sum / scoreSheet.length();
    }

    public double meanInningPlayer2(){
        ArrayList<Integer> player2Innings = scoreSheet.getPlayer2IncrementsList() ;
        double sum = 0.0;
        for (int inning : player2Innings){
            sum += inning;
        }
        return sum / scoreSheet.length();
    }

    public double meanRunPlayer1(){
        ArrayList<Integer> player1Innings = scoreSheet.getPlayer1IncrementsList() ;
        double sum = 0.0;
        int count = 0;
        for (int inning : player1Innings){
            if (inning > 0) {
                sum += inning;
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }

    public double meanRunPlayer2(){
        ArrayList<Integer> player2Innings = scoreSheet.getPlayer2IncrementsList() ;
        double sum = 0.0;
        int count = 0;
        for (int inning : player2Innings){
            if (inning > 0) {
                sum += inning;
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    }
}
