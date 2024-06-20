package org.sbv.pockettracker;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

// this class in is charge of the games history
// every turn is noted and this log can be accessed for review
// makes revert feature possible
public class ScoreSheet implements Parcelable {
    private final ArrayList<Integer> player1ScoresList;
    private final ArrayList<Integer> player2ScoresList;
    private final ArrayList<Integer> ballsOnTableList;
    private final ArrayList<String> switchReasonsList;

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
        ballsOnTableList = new ArrayList<>();
        pointer = -1;
        //watched objects
        this.trackedTable = table;
        this.trackedPlayer1 = player1;
        this.trackedPlayer2 = player2;
        //enter starting values
        update("   ");
    }

    //Parcelable methods
    protected ScoreSheet(@NonNull Parcel in){
        this.switchReasonsList = in.readArrayList(String.class.getClassLoader());
        this.player1ScoresList = in.readArrayList(Integer.class.getClassLoader());
        this.player2ScoresList = in.readArrayList(Integer.class.getClassLoader());
        this.ballsOnTableList = in.readArrayList(Integer.class.getClassLoader());
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeList(switchReasonsList);
        dest.writeList(player1ScoresList);
        dest.writeList(player2ScoresList);
        dest.writeList(ballsOnTableList);
    }
    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<ScoreSheet> CREATOR = new Creator<ScoreSheet>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public ScoreSheet createFromParcel(Parcel in) {
            return new ScoreSheet(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public ScoreSheet[] newArray(int size) {
            return new ScoreSheet[size];
        }
    };


    public void update(String reason){
        if (!isLatest()){
            clearAfterPointer();
        }
        player1ScoresList.add(trackedPlayer1.getScore());
        player2ScoresList.add(trackedPlayer2.getScore());
        ballsOnTableList.add(trackedTable.getNumberOfBalls());
        switchReasonsList.add(reason);
        pointer++;
    }

    public void rollback(){
        if (pointer == 0) return;
        pointer--;
        trackedPlayer1.setScore( player1ScoresList.get(pointer) );
        trackedPlayer2.setScore( player2ScoresList.get(pointer) );
        trackedTable.setOldNumberOfBalls( ballsOnTableList.get(pointer));
        trackedTable.setNumberOfBalls( ballsOnTableList.get(pointer) );
    }

    public void progress(){
        if (isLatest()) return;
        pointer++;
        trackedPlayer1.setScore( player1ScoresList.get(pointer) );
        trackedPlayer2.setScore( player2ScoresList.get(pointer) );
        trackedTable.setOldNumberOfBalls( ballsOnTableList.get(pointer));
        trackedTable.setNumberOfBalls( ballsOnTableList.get(pointer) );
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
            ballsOnTableList.remove(n);
            //switchReasonsList.remove(n);
        }
    }

    private void clearUntilPresent(){
        int n;
        while (!isLatest()){
            n = length() - 1;
            player1ScoresList.remove(n);
            player2ScoresList.remove(n);
            ballsOnTableList.remove(n);
            switchReasonsList.remove(n);
        }
    }

    public boolean isHealthy(){
        boolean pointerCheck = pointer >= 0 && pointer < player1ScoresList.size(); //pointer must not be negative or larger than the list
        boolean sizeChecks = ( player1ScoresList.size() == player2ScoresList.size() ) && ( player2ScoresList.size() == ballsOnTableList.size() );

        return pointerCheck && sizeChecks;
    }

    public int getScoreOfPlayer1At(int turn){
        return player1ScoresList.get(turn);
    }
    public int getScoreOfPlayer2At(int turn){
        return player2ScoresList.get(turn);
    }
    public int getRunOfPlayer1At(int turn) {
        if (turn <= 0 || turn >= length()) {
            return 0;
        }
        return player1ScoresList.get(turn) - player1ScoresList.get(turn - 1 );
    }
    public int getRunOfPlayer2At(int turn) {
        if (turn <= 0 || turn >= length()) {
            return 0;
        }
        return player2ScoresList.get(turn) - player2ScoresList.get(turn - 1);
    }

    public int getBallsOnTableAt(int turn){
        return ballsOnTableList.get(turn);
    }

    public ArrayList<Integer> getPlayer1ScoresList(){
        return player1ScoresList;
    }

    public ArrayList<Integer> getPlayer1IncrementsList(){
        ArrayList<Integer> player1IncrementsList = new ArrayList<>();
        for (int index =  1; index<length(); index++){
            player1IncrementsList.add(player1ScoresList.get(index)-player1ScoresList.get(index-1));
        }
        return player1IncrementsList;
    }

    public ArrayList<Integer> getPlayer2ScoresList(){
        return player2ScoresList;
    }

    public ArrayList<Integer> getPlayer2IncrementsList(){
        ArrayList<Integer> player2IncrementsList = new ArrayList<>();
        for (int index =  1; index<length(); index++){
            player2IncrementsList.add(player2ScoresList.get(index)-player2ScoresList.get(index-1));
        }
        return player2IncrementsList;
    }

    public ArrayList<Integer> getBallsOnTableList(){
        return ballsOnTableList;
    }

    public char getSwitchReasonAt(int turn){
        return switchReasonsList.get(turn).charAt(0);
    }

}
