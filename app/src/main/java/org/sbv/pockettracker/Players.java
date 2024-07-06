package org.sbv.pockettracker;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public class Players implements Parcelable {
    public static final String[] defaultPlayerNames = {"",""};
    public static final String[] defaultPlayerClubs = {"",""};
    public static boolean haveClubs = true;
    private final String[] names;
    private final String[] clubs;

    public Players() {
        this.names = defaultPlayerNames;
        this.clubs = defaultPlayerClubs;
    }

    public String[] getNames() {
        return names;
    }

    public String[] getClubs() {
        return clubs;
    }


    public void setName(int playerNumber, String name) {
        if (playerNumber == 0 || playerNumber == 1)
            names[playerNumber] = name;
    }

    public void setClub(int playerNumber, String club) {
        if (playerNumber == 0 || playerNumber == 1)
            this.clubs[playerNumber] = club;
    }


    public void swap() {
        String nameBackup = this.names[0];
        String clubBackup = this.clubs[0];

        this.names[0] = this.names[1];
        this.clubs[0] = this.clubs[1];

        this.names[1] = nameBackup;
        this.clubs[1] = clubBackup;
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
