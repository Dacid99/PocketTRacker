package org.sbv.pockettracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.color.utilities.Score;

public class ScoreSheetViewModel extends ViewModel {
    private final MutableLiveData<ScoreSheet> scoreSheetLiveData = new MutableLiveData<>();

    public LiveData<ScoreSheet> getScoreSheet(){
        return scoreSheetLiveData;
    }

    public void setScoreSheetLiveData(ScoreSheet scoreSheet){
        scoreSheetLiveData.setValue(scoreSheet);
    }

}
