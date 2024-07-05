package org.sbv.pockettracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PoolTableViewModel extends ViewModel {
    private MutableLiveData<PoolTable> poolTableLiveData;

    public LiveData<PoolTable> getPoolTable(){
        return poolTableLiveData;
    }

    public void setPoolTable(PoolTable poolTable){
        poolTableLiveData.setValue(poolTable);
    }
}
