package org.sbv.pockettracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScoreSheetViewModel extends ViewModel {
    private final MutableLiveData<ScoreSheet> scoreSheetLiveData;

    public LiveData<ScoreSheet> getScoreSheet(){
        return scoreSheetLiveData;
    }

    public ScoreSheetViewModel(PoolTableViewModel poolTableViewModel, ScoreBoardViewModel scoreBoardViewModel){
        scoreSheetLiveData = new MutableLiveData<>(new ScoreSheet(poolTableViewModel, scoreBoardViewModel));
    }

    public void update(String reason){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            scoreSheet.append(reason);
            scoreSheetLiveData.setValue(scoreSheet);
        }
    }

    public void rollback(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            scoreSheet.rollback();
            scoreSheetLiveData.setValue(scoreSheet);
        }
    }

    public void progress(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            scoreSheet.progress();
            scoreSheetLiveData.setValue(scoreSheet);
        }
    }

    public void toStart(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            scoreSheet.toStart();
            scoreSheetLiveData.setValue(scoreSheet);
        }
    }
    public void toLatest(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            scoreSheet.toLatest();
            scoreSheetLiveData.setValue(scoreSheet);
        }
    }
    public void append(ScoreSheet.Inning inning){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            scoreSheet.append(inning);
            scoreSheetLiveData.setValue(scoreSheet);
        }
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
    public int length(){
        ScoreSheet scoreSheet = scoreSheetLiveData.getValue();
        if (scoreSheet != null){
            return scoreSheet.length();
        }else return 1;
    }
    public void reset(PoolTableViewModel poolTableViewModel, ScoreBoardViewModel scoreBoardViewModel){
        ScoreSheet scoreSheet = new ScoreSheet(poolTableViewModel,scoreBoardViewModel);
        scoreSheetLiveData.setValue(scoreSheet);
    }
}
