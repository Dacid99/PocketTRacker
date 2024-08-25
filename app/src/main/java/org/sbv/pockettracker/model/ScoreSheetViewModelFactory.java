package org.sbv.pockettracker.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


public class ScoreSheetViewModelFactory implements ViewModelProvider.Factory {
    private final ScoreBoardViewModel scoreBoardViewModel;
    private final PoolTableViewModel poolTableViewModel;

    public ScoreSheetViewModelFactory(PoolTableViewModel poolTableViewModel, ScoreBoardViewModel scoreBoardViewModel){
        this.scoreBoardViewModel = scoreBoardViewModel;
        this.poolTableViewModel = poolTableViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass){
        if (modelClass.isAssignableFrom(ScoreSheetViewModel.class)) {
            @SuppressWarnings("unchecked")
            T viewModel = (T) new ScoreSheetViewModel(poolTableViewModel, scoreBoardViewModel);
            return viewModel;
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
