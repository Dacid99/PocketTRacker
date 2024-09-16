package org.sbv.pockettracker.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class ScoreSheetViewModel extends ViewModel {
    private final MutableLiveData<ScoreSheet> scoreSheetLiveData;

    public final LiveData<ScoreSheet> getScoreSheet(){
        return scoreSheetLiveData;
    }

    public ScoreSheetViewModel(){
        scoreSheetLiveData = new MutableLiveData<>(new ScoreSheet());
    }

    public void update(ScoreSheet.Inning newInning){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            scoreSheet.update(newInning);
            scoreSheetLiveData.setValue(scoreSheet);
        }
    }

    public ScoreSheet.Inning rollback(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            ScoreSheet.Inning inning = scoreSheet.rollback();
            scoreSheetLiveData.setValue(scoreSheet);
            return inning;
        }
        return null;
    }

    public ScoreSheet.Inning progress(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            ScoreSheet.Inning inning = scoreSheet.progress();
            scoreSheetLiveData.setValue(scoreSheet);
            return inning;
        }
        return null;
    }

    public ScoreSheet.Inning toStart(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            ScoreSheet.Inning inning = scoreSheet.toStart();
            scoreSheetLiveData.setValue(scoreSheet);
            return inning;
        }
        return null;
    }

    public ScoreSheet.Inning toLatest(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            ScoreSheet.Inning inning = scoreSheet.toLatest();
            scoreSheetLiveData.setValue(scoreSheet);
            return inning;
        }
        return null;
    }

    public boolean isStart(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            return scoreSheet.isStart();
        }else return false;
    }

    public boolean isLatest(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            return scoreSheet.isLatest();
        }else return false;
    }

    public int turnplayerNumber(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            return scoreSheet.turnplayerNumber();
        }else return 0;
    }
    public int currentTurn(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            return scoreSheet.currentTurn();
        }else return 0;
    }
    public int[] innings(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            return scoreSheet.innings();
        }else return new int[]{0,0};
    }
    public int length(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            return scoreSheet.length();
        }else return 1;
    }
    public void reset(){
        ScoreSheet scoreSheet = new ScoreSheet();
        scoreSheetLiveData.setValue(scoreSheet);
    }
}
