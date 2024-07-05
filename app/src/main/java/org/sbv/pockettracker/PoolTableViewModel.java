package org.sbv.pockettracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PoolTableViewModel extends ViewModel {
    private final MutableLiveData<PoolTable> poolTableLiveData = new MutableLiveData<>();

    public LiveData<PoolTable> getPoolTable(){
        return poolTableLiveData;
    }

    public void setPoolTable(PoolTable poolTable){
        poolTableLiveData.setValue(poolTable);
    }

    public void updateNumberOfBalls(int numberOfBalls){
        PoolTable poolTable = poolTableLiveData.getValue();
        if (poolTable != null){
            if (numberOfBalls != poolTable.getNumberOfBalls()) {
                poolTable.setNumberOfBalls(numberOfBalls);
                poolTableLiveData.setValue(poolTable);
            }
        }
    }
    public void updateOldNumberOfBalls(int oldNumberOfBalls){
        PoolTable poolTable = poolTableLiveData.getValue();
        if (poolTable != null){
            if (oldNumberOfBalls != poolTable.getOldNumberOfBalls()) {
                poolTable.setOldNumberOfBalls(oldNumberOfBalls);
                poolTableLiveData.setValue(poolTable);
            }
        }
    }

    public int evaluate(){
        PoolTable poolTable = poolTableLiveData.getValue();
        if (poolTable != null){
            int ballDifference = poolTable.evaluate();
            poolTableLiveData.setValue(poolTable);
            return ballDifference;
        }else return 0;
    }

    public int getNumberOfBalls(){
        PoolTable poolTable = poolTableLiveData.getValue();
        if (poolTable != null){
            return poolTable.getNumberOfBalls();
        }else return 15;
    }

    public int getOldNumberOfBalls(){
        PoolTable poolTable = poolTableLiveData.getValue();
        if (poolTable != null){
            return poolTable.getOldNumberOfBalls();
        }else return 15;
    }

    public void reset(){
        PoolTable poolTable = new PoolTable();
        poolTableLiveData.setValue(poolTable);
    }
}
