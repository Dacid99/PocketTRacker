package org.sbv.straightpoolcounter;
import java.util.ArrayList;
import java.util.Collections;

// this class in is charge of the games history
// every turn is noted and this log can be accessed for review
// makes revert feature possible
public class ScoreSheet {
    private ArrayList<Integer> player1ScoresList, player2ScoresList, tableBallNumberList;
    private ArrayList<String> switchReasonsList;

    //this member holds the index of the current entry in the ArrayList
    //for going back in history and rewriting from there
    private int pointer;
    private PoolTable trackedTable;
    private Player trackedPlayer1, trackedPlayer2;

    public ScoreSheet(PoolTable table, Player player1, Player player2){
        //set up containers for data
        switchReasonsList = new ArrayList<>();
        player1ScoresList = new ArrayList<>();
        player2ScoresList = new ArrayList<>();
        tableBallNumberList = new ArrayList<>();
        pointer = -1;
        //watched objects
        this.trackedTable = table;
        this.trackedPlayer1 = player1;
        this.trackedPlayer2 = player2;
        //enter starting values
        update();
        System.out.println(isHealthy());
    }

    public void update(){
        if (!isLatest()){
            clearAfterPointer();
        }
        player1ScoresList.add(trackedPlayer1.getScore());
        player2ScoresList.add(trackedPlayer2.getScore());
        tableBallNumberList.add(trackedTable.getNumberOfBalls());
        pointer++;
    }

    public void rollback(){
        if (pointer == 0) return;
        pointer--;
        trackedPlayer1.setScore( player1ScoresList.get(pointer) );
        trackedPlayer2.setScore( player2ScoresList.get(pointer) );
        trackedTable.setNumberOfBalls( tableBallNumberList.get(pointer) );
    }

    public void progress(){
        if (isLatest()) return;
        pointer++;
        trackedPlayer1.setScore( player1ScoresList.get(pointer) );
        trackedPlayer2.setScore( player2ScoresList.get(pointer) );
        trackedTable.setNumberOfBalls( tableBallNumberList.get(pointer) );
    }

    public void writeSwitchReason(String reason){
        switchReasonsList.add(reason);
    }

    public ArrayList<String> getSwitchReasonList(){
        return switchReasonsList;
    }

    public int length(){
        return player1ScoresList.size();
    }

    public int turn(){
        return pointer;
    }
    public int longestRunPlayer1(){
        return Collections.max(player1ScoresList);
    }

    public int longestRunPlayer2(){
        return Collections.max(player2ScoresList);
    }

    public boolean isLatest(){
        //for writing pointer must be at the last index
        return pointer == length() - 1;
    }

    public boolean isStart(){
        //for writing pointer must be at the last index
        return pointer == 0;
    }

    private void clearAfterPointer(){
        for (int n = length()-1; n>pointer; n--){
            player1ScoresList.remove(n);
            player2ScoresList.remove(n);
            tableBallNumberList.remove(n);
            //switchReasonsList.remove(n);
        }
    }

    private void clearUntilPresent(){
        int n;
        while (!isLatest()){
            n = length() - 1;
            player1ScoresList.remove(n);
            player2ScoresList.remove(n);
            tableBallNumberList.remove(n);
            switchReasonsList.remove(n);
        }
    }

    public boolean isHealthy(){
        boolean pointerCheck = pointer >= 0 && pointer < player1ScoresList.size(); //pointer must not be negative or larger than the list
        boolean sizeChecks = ( player1ScoresList.size() == player2ScoresList.size() ) && ( player2ScoresList.size() == tableBallNumberList.size() );

        return pointerCheck && sizeChecks;
    }
}
