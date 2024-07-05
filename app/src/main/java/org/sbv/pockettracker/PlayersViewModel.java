package org.sbv.pockettracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlayersViewModel extends ViewModel {
    private MutableLiveData<Players> playersLiveData = new MutableLiveData<>();

    public LiveData<Players> getPlayers() {
        return playersLiveData;
    }

    public void setPlayersLiveData(Players players) {
        playersLiveData.setValue(players);
    }

    public void updatePlayerName(int playerNumber, String name) {
        Players players = playersLiveData.getValue();
        if (players != null) {
            players.setName(playerNumber, name);
            playersLiveData.setValue(players);
        }
    }

    public void updateClubName(int playerNumber, String club) {
        Players players = playersLiveData.getValue();
        if (players != null) {
            players.setClub(playerNumber, club);
            playersLiveData.setValue(players);
        }
    }
    public void swap(){
        Players players = playersLiveData.getValue();
        if (players != null){
            players.swap();
            playersLiveData.setValue(players);
        }
    }

    public void reset(){
        Players players = new Players();
        playersLiveData.setValue(players);
    }
}



