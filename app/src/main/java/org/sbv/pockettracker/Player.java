package org.sbv.pockettracker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class Player implements Parcelable {
    static int winningPoints = 40; //default value
    private String name = "";
    private String club = "";
    private int score;

    public Player() {
        this.score = 0;
    }

    public void addPoints(int points) {
        this.score += points;
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
        return score >= winningPoints;
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

        System.out.println(this.name + this.club + otherPlayer.getName() + otherPlayer.getClub());
    }

    //parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.club);
        dest.writeInt(this.score);
    }

    public Player(Parcel in) {
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
