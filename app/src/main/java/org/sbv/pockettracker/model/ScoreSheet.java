package org.sbv.pockettracker.model;
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
    public static final String MAX_INNINGS_DEFAULT = "-1";
    public static final String FIRST_INNINGS_WARNING_DEFAULT = "5";
    public static final String SECOND_INNINGS_WARNING_DEFAULT = "1";

    private final ArrayList<Inning> inningsList;

    //this member holds the index of the current entry in the ArrayList
    //for going back in history and rewriting from there
    private int pointer;
    private PoolTableViewModel trackedPoolTableViewModel;
    private ScoreBoardViewModel trackedScoreBoardViewModel;

    @NonNull
    @Override
    public Iterator<Inning> iterator() {
        return inningsList.iterator();
    }

    public static class Inning implements Parcelable{
        public String switchReason;
        public int[] playerScores = new int[2];
        public int ballsOnTable;

        public Inning(){
        }
        public Inning(String[] array){
            if (array.length == 4) {
                switchReason = array[0];
                try {
                    playerScores[0] = Integer.parseInt(array[1]);
                    playerScores[1] = Integer.parseInt(array[2]);
                    ballsOnTable = Integer.parseInt(array[3]);
                }catch (NumberFormatException e){
                    Log.d("Exception occurred", "In ScoreSheet.Turn.fromStringArray: "+e.getMessage());
                }
            }else {
                Log.d("Bad argument", "In ScoreSheet.Turn.fromStringArray: array.length is not 4!"+ Arrays.toString(array));
            }
        }
        public final String[] toStringArray(){
            String[] array =  new String[4];
            array[0] = switchReason;
            array[1] = String.valueOf(playerScores[0]);
            array[2] = String.valueOf(playerScores[1]);
            array[3] = String.valueOf(ballsOnTable);
            return array;
        }
        // parcelable methods
        protected Inning(Parcel in){
            switchReason = in.readString();
            playerScores = in.createIntArray();
            ballsOnTable = in.readInt();
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(switchReason);
            dest.writeIntArray(playerScores);
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

    public ScoreSheet(){
        //set up containers for data
        this.inningsList = new ArrayList<>();

        this.pointer = -1; //directly incremented by update
        //enter starting values
        // this is crucial for statistics, as the list is never empty!
        Inning firstInning = new Inning(new String[]{" ","0","0","15"});
        update(firstInning);
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


    public void update(Inning newInning){
        if (!isLatest()){
            clearAfterPointer();
        }
        inningsList.add(newInning);
        pointer++;
    }

    public Inning rollback(){
        if (!isStart()) {
            pointer--;
            return inningsList.get(pointer);
        }
        return null;
    }

    public Inning toStart(){
        if (!isStart()){
            pointer = 0;
            return inningsList.get(pointer);
        }
        return null;
    }

    public Inning progress(){
        if (!isLatest()) {
            pointer++;
            return inningsList.get(pointer);
        }
        return null;
    }

    public Inning toLatest(){
        if (!isLatest()){
            pointer = length() - 1;
            return inningsList.get(pointer);
        }
        return null;
    }

    public final int length(){
        return inningsList.size();
    }

    public final boolean isEmpty(){
        return inningsList.isEmpty();
    }

    public final int currentTurn(){
        return pointer;
    }

    public final int[] innings(){
        int[] innings = new int[2];
        innings[0] = (int) Math.ceil(Math.abs(length()/2.0 - 0.5));
        innings[1] = (int) Math.floor(Math.abs(length()/2.0 - 0.5));
        return innings;
    }

    public final boolean isLatest(){
        //for writing pointer must be at the last index
        return pointer == length() - 1;
    }

    public final boolean isStart(){
        //for writing pointer must be at the last index
        return pointer == 0;
    }

    public final boolean isSecondConsecutiveFoul(){
        if (pointer < 3){
            return false;
        }
        return inningsList.get(pointer-1).switchReason.equals("Foul") && inningsList.get(pointer-3).switchReason.equals("Foul");
    }

    public int turnplayerNumber(){
        return pointer % 2;
    }

    private void clearAfterPointer() {
        if (length() > pointer + 1) {
            inningsList.subList(pointer + 1, length()).clear();
        }
    }

    public final int getScoreOfPlayer1At(int turn){
        return inningsList.get(turn).playerScores[0];
    }
    public final int getScoreOfPlayer2At(int turn){
        return inningsList.get(turn).playerScores[1];
    }
    public final int[] getPlayerScoresAt(int turn) {
        return inningsList.get(turn).playerScores;
    }
    public final ArrayList<Integer>[] getPlayerScoresList(){
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] playerScoresList = new ArrayList[2];
        playerScoresList[0] = new ArrayList<>();
        playerScoresList[1] = new ArrayList<>();
        for (Inning inning : inningsList){
            playerScoresList[0].add(inning.playerScores[0]);
            playerScoresList[1].add(inning.playerScores[1]);
        }
        return playerScoresList;
    }
    public final int getBallsOnTableAt(int turn){
        return inningsList.get(turn).ballsOnTable;
    }

    public final String getSwitchReasonAt(int turn){
        return inningsList.get(turn).switchReason;
    }
}
