package org.sbv.pockettracker.model;

public class ScoreSheetWriter {
    PoolTableViewModel trackedPoolTableViewModel;
    ScoreBoardViewModel trackedScoreBoardViewModel;
    ScoreSheetViewModel scoreSheetViewModel;

    public ScoreSheetWriter(ScoreSheetViewModel scoreSheetViewModel, PoolTableViewModel poolTableViewModel, ScoreBoardViewModel scoreBoardViewModel){
        this.scoreSheetViewModel = scoreSheetViewModel;
        this.trackedPoolTableViewModel = poolTableViewModel;
        this.trackedScoreBoardViewModel = scoreBoardViewModel;
    }

    private void updateViewModels(ScoreSheet.Inning inning){
        if (inning != null) {
            trackedScoreBoardViewModel.updateScores(inning.playerScores);
            trackedPoolTableViewModel.updateOldNumberOfBalls( inning.ballsOnTable );
            trackedPoolTableViewModel.updateNumberOfBalls( inning.ballsOnTable );
        }
    }

    public void update(String reason){
        ScoreSheet.Inning newInning = new ScoreSheet.Inning();
        newInning.switchReason = reason;
        newInning.playerScores[0] = trackedScoreBoardViewModel.getScores()[0];
        newInning.playerScores[1] = trackedScoreBoardViewModel.getScores()[1];
        newInning.ballsOnTable = trackedPoolTableViewModel.getNumberOfBalls();
    }

    public void rollback(){
        ScoreSheet.Inning inning = scoreSheetViewModel.rollback();
        updateViewModels(inning);
    }

    public void toStart(){
        ScoreSheet.Inning inning = scoreSheetViewModel.toStart();
        updateViewModels(inning);
    }

    public void progress(){
        ScoreSheet.Inning inning = scoreSheetViewModel.progress();
        updateViewModels(inning);
    }

    public void toLatest(){
        ScoreSheet.Inning inning = scoreSheetViewModel.toLatest();
        updateViewModels(inning);
    }
}
