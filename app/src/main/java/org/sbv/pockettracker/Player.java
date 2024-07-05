package org.sbv.pockettracker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class Player implements Parcelable {
    public static final String[] defaultPlayerNames = {"",""};
    public static final String[] defaultPlayerClubs = {"",""};
    public static boolean hasClub = true;

    private final int playerNumber;
    private String name;
    private String club;

    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
        this.name = defaultPlayerNames[playerNumber-1];
        this.club = defaultPlayerClubs[playerNumber-1];
    }


    public int getPlayerNumber(){
        return this.playerNumber;
    }


    public String getName() {
        return name;
    }

    public String getClub() {
        return club;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setClub(String club) {
        this.club = club;
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
    }

    public Player(Parcel in) {
        this.playerNumber = in.readInt();
        this.name = in.readString();
        this.club = in.readString();
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
