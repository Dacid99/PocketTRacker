package org.sbv.pockettracker;

import java.util.Collections;

public class GameStatistics {
    ScoreSheet scoreSheet ;

    public GameStatistics(ScoreSheet scoreSheet){
        this.scoreSheet = scoreSheet;
    }

    public int maxRunPlayer1(){
        return Collections.max(scoreSheet.getPlayer1IncrementsList());
    }
    public int maxRunPlayer2(){
        return Collections.max(scoreSheet.getPlayer2IncrementsList());
    }

    public int player1Innings(){
        return (int) Math.ceil(scoreSheet.length()/2.0);
    }

    public int player2Innings(){
        return (int) Math.floor(scoreSheet.length()/2.0);
    }
}
