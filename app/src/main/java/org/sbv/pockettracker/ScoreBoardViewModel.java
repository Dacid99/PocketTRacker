package org.sbv.pockettracker;

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
    public int[] getScores(){
        ScoreBoard scoreBoard = scoreBoardLiveData.getValue();
        if (scoreBoard != null){
            return scoreBoard.getPlayerScores();
        }else return new int[]{0,0};
    }
    public void addPoints(int playerNumber, int points){
        ScoreBoard scoreBoard = scoreBoardLiveData.getValue();
        if (scoreBoard != null){
            scoreBoard.addPoints(playerNumber, points);
            scoreBoardLiveData.setValue(scoreBoard);
        }
    }
    public void updateWinnerPoints(int winnerPoints){
        ScoreBoard scoreBoard = scoreBoardLiveData.getValue();
        if (scoreBoard != null){
            scoreBoard.setWinnerPoints(winnerPoints);
            scoreBoardLiveData.setValue(scoreBoard);
        }
    }

    public void updateScores(int[] scores){
        ScoreBoard scoreBoard = scoreBoardLiveData.getValue();
        if (scoreBoard != null){
            scoreBoard.setPlayer1Score(scores[0]);
            scoreBoard.setPlayer2Score(scores[1]);
            scoreBoardLiveData.setValue(scoreBoard);
        }
    }
    public void reset(){
        ScoreBoard scoreBoard = new ScoreBoard();
        scoreBoardLiveData.setValue(scoreBoard);
    }
}
