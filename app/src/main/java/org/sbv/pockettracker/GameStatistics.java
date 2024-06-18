package org.sbv.pockettracker;

import java.util.Collections;
import java.util.NoSuchElementException;

public class GameStatistics {
    ScoreSheet scoreSheet ;

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
}
