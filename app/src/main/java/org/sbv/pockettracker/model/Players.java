package org.sbv.pockettracker.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;

public class Players implements Parcelable {
    public static final String[] defaultPlayerNames = {"",""};
    public static final String[] defaultPlayerClubs = {"",""};
    public static final int PLAYER_1_NUMBER = 0;
    public static final int PLAYER_2_NUMBER = 1;
    public static boolean haveClubs = true;
    private final String[] names;
    private final String[] clubs;

    public Players() {
        this.names = Arrays.copyOf(defaultPlayerNames,2);
        this.clubs = Arrays.copyOf(defaultPlayerClubs, 2);
    }

    public String[] getNames() {
        return names;
    }

    public String[] getClubs() {
        return clubs;
    }


    public void setName(int playerNumber, String name) {
        if (playerNumber == PLAYER_1_NUMBER|| playerNumber == PLAYER_2_NUMBER)
            names[playerNumber] = name;
    }

    public void setClub(int playerNumber, String club) {
        if (playerNumber == PLAYER_1_NUMBER || playerNumber == PLAYER_2_NUMBER)
            this.clubs[playerNumber] = club;
    }


    public void swap() {
        String nameBackup = this.names[PLAYER_1_NUMBER];
        String clubBackup = this.clubs[PLAYER_1_NUMBER];

        this.names[PLAYER_1_NUMBER] = this.names[PLAYER_2_NUMBER];
        this.clubs[PLAYER_1_NUMBER] = this.clubs[PLAYER_2_NUMBER];

        this.names[PLAYER_2_NUMBER] = nameBackup;
        this.clubs[PLAYER_2_NUMBER] = clubBackup;
    }

    //parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeStringArray(this.names);
        dest.writeStringArray(this.clubs);
    }

    protected Players(@NonNull Parcel in) {
        this.names = in.createStringArray();
        this.clubs = in.createStringArray();
    }

    public static final Creator<Players> CREATOR = new Creator<Players>() {
        @NonNull
        @Contract("_ -> new")
        @Override
        public Players createFromParcel(Parcel in) {
            return new Players(in);
        }

        @NonNull
        @Contract(value = "_ -> new", pure = true)
        @Override
        public Players[] newArray(int size) {
            return new Players[size];
        }
    };
}
