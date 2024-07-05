package org.sbv.pockettracker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.material.color.utilities.Score;

import org.jetbrains.annotations.Contract;

public class ScoreBoard implements Parcelable {
    public static int defaultWinnerPoints = 40;

    private int winnerPoints;
    private final int[] playerScores;

    public ScoreBoard(){
        playerScores = new int[]{0, 0};
        winnerPoints = defaultWinnerPoints;
    }

    public int[] getPlayerScores() {
        return playerScores;
    }

    public int getWinnerPoints() {
        return winnerPoints;
    }

    public void setPlayer1Score(int player1Score) {
        this.playerScores[0] = player1Score;
    }
    public void setPlayer2Score(int player2Score) {
        this.playerScores[1] = player2Score;
    }
    public void addPoints(int playerNumber, int points){
        playerScores[playerNumber] += points;
    }

    public void setWinnerPoints(int winnerPoints) {
        this.winnerPoints = winnerPoints;
    }

    public boolean existsWinner(){
        boolean existsWinner = false;
        for (int score : playerScores){
            existsWinner |= (score >= winnerPoints);
        }
        return existsWinner;
    }

    public int getWinner(){
        for (int index = 0; index < playerScores.length; index++){
            if (playerScores[index] >= winnerPoints){
                return index;
            }
        }
        return -1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(winnerPoints);
        dest.writeIntArray(playerScores);
    }

    public ScoreBoard(Parcel in){
        winnerPoints = in.readInt();
        playerScores = in.createIntArray();
    }

    public static final Creator<ScoreBoard> CREATOR = new Creator<ScoreBoard>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public ScoreBoard createFromParcel(Parcel in) {
            return new ScoreBoard(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public ScoreBoard[] newArray(int size) {
            return new ScoreBoard[size];
        }
    };
}
