package org.sbv.pockettracker;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScoreBoardViewModel extends ViewModel {
    private MutableLiveData<ScoreBoard> scoreBoardLiveData;

    public LiveData<ScoreBoard> getScoreBoard(){
        return scoreBoardLiveData;
    }
    public void setScoreBoardLiveData(MutableLiveData<ScoreBoard> scoreBoardLiveData) {
        this.scoreBoardLiveData = scoreBoardLiveData;
    }
}
