package org.sbv.pockettracker;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

// this class in is charge of the games history
// every turn is noted and this log can be accessed for review
// makes revert feature possible
public class ScoreSheet implements Parcelable, Iterable<ScoreSheet.Inning> {
    private ArrayList<Inning> inningsList;

    //this member holds the index of the current entry in the ArrayList
    //for going back in history and rewriting from there
    private int pointer;
    private PoolTable trackedTable;
    private Player trackedPlayer1, trackedPlayer2;

    @NonNull
    @Override
    public Iterator<Inning> iterator() {
        return inningsList.iterator();
    }

    public static class Inning implements Parcelable{
        public String switchReason;
        public int player1Score;
        public int player2Score;
        public int ballsOnTable;

        public Inning(){
        }
        public Inning(String[] array){
            if (array.length == 4) {
                switchReason = array[0];
                try {
                    player1Score = Integer.parseInt(array[1]);
                    player2Score = Integer.parseInt(array[2]);
                    ballsOnTable = Integer.parseInt(array[3]);
                }catch (NumberFormatException e){
                    Log.d("Exception occurred", "In ScoreSheet.Turn.fromStringArray: "+e.getMessage());
                }
            }else {
                Log.d("Bad argument", "In ScoreSheet.Turn.fromStringArray: array.length is not 4!"+ Arrays.toString(array));
            }
        }
        public String[] toStringArray(){
            String[] array =  new String[4];
            array[0] = switchReason;
            array[1] = String.valueOf(player1Score);
            array[2] = String.valueOf(player2Score);
            array[3] = String.valueOf(ballsOnTable);
            return array;
        }
        // parcelable methods
        protected Inning(Parcel in){
            switchReason = in.readString();
            player1Score = in.readInt();
            player2Score = in.readInt();
            ballsOnTable = in.readInt();
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(switchReason);
            dest.writeInt(player1Score);
            dest.writeInt(player2Score);
            dest.writeInt(ballsOnTable);
        }
        public static final Creator<Inning> CREATOR = new Creator<Inning>() {
            @NonNull
            @Contract("_ -> new")
            @Override
            public Inning createFromParcel(Parcel in) {
                return new Inning(in);
            }

            @NonNull
            @Contract(value = "_ -> new", pure = true)
            @Override
            public Inning[] newArray(int size) {
                return new Inning[size];
            }
        };
    }

    public ScoreSheet(PoolTable table, Player player1, Player player2){
        //watched objects
        this.trackedTable = table;
        this.trackedPlayer1 = player1;
        this.trackedPlayer2 = player2;
        //set up containers for data
        this.inningsList = new ArrayList<>();

        this.pointer = -1; //directly incremented by update
        //enter starting values
        // this is crucial for statistics, as the list is never empty!
        update("   ");
    }

    public void include(ScoreSheet scoreSheet){
        inningsList = scoreSheet.getInningsList();
        pointer = scoreSheet.currentTurn() - 1;
        progress();
    }

    public void trackPlayer1(Player player1){
        this.trackedPlayer1 = player1;
    }

    public void trackPlayer2(Player player2){
        this.trackedPlayer2 = player2;
    }
    //Parcelable methods
    protected ScoreSheet(@NonNull Parcel in){
        pointer = in.readInt();
        inningsList = in.createTypedArrayList(ScoreSheet.Inning.CREATOR);
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(pointer);
        dest.writeTypedList(inningsList);
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
        Inning turn = new Inning();
        turn.switchReason = reason;
        turn.player1Score = trackedPlayer1.getScore();
        turn.player2Score = trackedPlayer2.getScore();
        turn.ballsOnTable = trackedTable.getNumberOfBalls();

        inningsList.add(turn);
        pointer++;
    }

    public void rollback(){
        if (!isStart()) {
            pointer--;
            trackedPlayer1.setScore( inningsList.get(pointer).player1Score );
            trackedPlayer2.setScore( inningsList.get(pointer).player2Score );
            trackedTable.setOldNumberOfBalls( inningsList.get(pointer).ballsOnTable );
            trackedTable.setNumberOfBalls( inningsList.get(pointer).ballsOnTable );
        }

    }

    public void toStart(){
        if (!isStart()){
            pointer = 0;
            trackedPlayer1.setScore( inningsList.get(pointer).player1Score );
            trackedPlayer2.setScore( inningsList.get(pointer).player2Score );
            trackedTable.setOldNumberOfBalls( inningsList.get(pointer).ballsOnTable );
            trackedTable.setNumberOfBalls( inningsList.get(pointer).ballsOnTable );
        }
    }

    public void progress(){
        if (!isLatest()) {
            pointer++;
            trackedPlayer1.setScore(inningsList.get(pointer).player1Score);
            trackedPlayer2.setScore(inningsList.get(pointer).player2Score);
            trackedTable.setOldNumberOfBalls(inningsList.get(pointer).ballsOnTable);
            trackedTable.setNumberOfBalls(inningsList.get(pointer).ballsOnTable);
        }
    }
    public void toLatest(){
        if (!isLatest()){
            pointer = length() - 1;
            trackedPlayer1.setScore( inningsList.get(pointer).player1Score );
            trackedPlayer2.setScore( inningsList.get(pointer).player2Score );
            trackedTable.setOldNumberOfBalls( inningsList.get(pointer).ballsOnTable );
            trackedTable.setNumberOfBalls( inningsList.get(pointer).ballsOnTable );
        }
    }

    public int length(){
        return inningsList.size();
    }

    public boolean isEmpty(){
        return inningsList.isEmpty();
    }

    public int currentTurn(){
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

    public boolean isPlayer1Turn(){
        return pointer % 2 == 0;
    }

    private void clearAfterPointer(){
        if (length() > pointer + 1) {
            inningsList.subList(pointer + 1, length()).clear();
        }
    }

    public boolean isHealthy(){
        return pointer >= 0 && pointer < length();
    }

    public ArrayList<Inning> getInningsList(){
        return inningsList;
    }
    public ArrayList<String> getSwitchReasonList(){
        ArrayList<String> switchReasonsList = new ArrayList<>();
        for (Inning turn : inningsList){
            switchReasonsList.add(turn.switchReason);
        }
        return switchReasonsList;
    }
    public ArrayList<Integer> getPlayer1ScoresList(){
        ArrayList<Integer> player1ScoresList = new ArrayList<>();
        for (Inning turn : inningsList){
            player1ScoresList.add(turn.player1Score);
        }
        return player1ScoresList;
    }
    public ArrayList<Integer> getPlayer2ScoresList(){
        ArrayList<Integer> player2ScoresList = new ArrayList<>();
        for (Inning turn : inningsList){
            player2ScoresList.add(turn.player2Score);
        }
        return player2ScoresList;
    }
    public ArrayList<Integer> getBallsOnTableList(){
        ArrayList<Integer> ballsOnTableList = new ArrayList<>();
        for (Inning turn : inningsList){
            ballsOnTableList.add(turn.ballsOnTable);
        }
        return ballsOnTableList;
    }
    public int getScoreOfPlayer1At(int turn){
        return inningsList.get(turn).player1Score;
    }
    public int getScoreOfPlayer2At(int turn){
        return inningsList.get(turn).player2Score;
    }
    public int getBallsOnTableAt(int turn){
        return inningsList.get(turn).ballsOnTable;
    }

    public int getRunOfPlayer1At(int turn) {
        if (turn <= 0 || turn >= length()) {
            return 0;
        }
        return getScoreOfPlayer1At(turn) - getScoreOfPlayer1At(turn - 1 );
    }
    public int getRunOfPlayer2At(int turn) {
        if (turn <= 0 || turn >= length()) {
            return 0;
        }
        return getScoreOfPlayer2At(turn) - getScoreOfPlayer2At(turn - 1);
    }

    public ArrayList<Integer> getPlayer1IncrementsList(){
        ArrayList<Integer> player1IncrementsList = new ArrayList<>();
        for (int index =  1; index<length(); index++){
            player1IncrementsList.add(getRunOfPlayer1At(index));
        }
        return player1IncrementsList;
    }
    public ArrayList<Integer> getPlayer2IncrementsList(){
        ArrayList<Integer> player2IncrementsList = new ArrayList<>();
        for (int index =  1; index<length(); index++){
            player2IncrementsList.add(getRunOfPlayer2At(index));
        }
        return player2IncrementsList;
    }

    public char getSwitchReasonAt(int turn){
        return inningsList.get(turn).switchReason.charAt(0);
    }

    public void update(Inning turn){
        if (!isLatest()){
            clearAfterPointer();
        }
        inningsList.add(turn);
        progress();
    }
}
