package org.sbv.straightpoolcounter;
import java.util.ArrayList;
import java.util.Collections;

public class ScoreSheet {
    private ArrayList<ArrayList<Integer>> scoresList;
    private ArrayList<String> switchReasonsList;

    public ScoreSheet(){
        switchReasonsList = new ArrayList<>();
        scoresList = new ArrayList<>();
        scoresList.add(new ArrayList<>());
        scoresList.add(new ArrayList<>());
    }

    public void writeScoreLine(int player1score, int player2score){
        scoresList.get(0).add(player1score);
        scoresList.get(1).add(player2score);
    }

    public void writeSwitchReason(String reason){
        switchReasonsList.add(reason);
    }

    public ArrayList<ArrayList<Integer>> getScoresList(){
        return scoresList;
    }

    public ArrayList<String> getSwitchReasonList(){
        return switchReasonsList;
    }

    public int length(){
        return scoresList.size();
    }

    public int longestRunPlayer1(){
        return Collections.max(scoresList.get(0));
    }

    public int longestRunPlayer2(){
        return Collections.max(scoresList.get(1));
    }
}
