package org.sbv.pockettracker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class Player implements Parcelable {
    public static int winnerPoints = 40;
    public static final String[] defaultPlayerNames = {"",""};
    public static final String[] defaultPlayerClubs = {"",""};
    public static boolean hasClub = true;

    private final int playerNumber;
    private String name;
    private String club;
    private int score;

    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
        this.name = defaultPlayerNames[playerNumber-1];
        this.club = defaultPlayerClubs[playerNumber-1];
        this.score = 0;
    }

    public void addPoints(int points) {
        this.score += points;
    }

    public int getPlayerNumber(){
        return this.playerNumber;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public String getClub() {
        return club;
    }

    public boolean isWinner() {
        return score >= winnerPoints;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void swapNameAndClubWith(Player otherPlayer) {
        String nameBackup = otherPlayer.getName();
        String clubBackup = otherPlayer.getClub();

        otherPlayer.setName(this.name);
        otherPlayer.setClub(this.club);

        this.name = nameBackup;
        this.club = clubBackup;
    }

    //parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.playerNumber);
        dest.writeString(this.name);
        dest.writeString(this.club);
        dest.writeInt(this.score);
    }

    public Player(Parcel in) {
        this.playerNumber = in.readInt();
        this.name = in.readString();
        this.club = in.readString();
        this.score = in.readInt();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}
