package org.sbv.straightpoolcounter;

import java.util.Enumeration;

public class PoolTable {
    private int numberOfBalls ;

    public PoolTable(){
        this.numberOfBalls = 15;
    }
    public int getNumberOfBalls(){
        return this.numberOfBalls;
    }

    public int setNewNumberOfBallsAndGiveDifference(int newNumberOfBalls){
        if (!isValidBallNumber(newNumberOfBalls)) {
            return -1 ;
        }
        int ballsRemoved = this.numberOfBalls - newNumberOfBalls;
        this.numberOfBalls = newNumberOfBalls;
        if (this.numberOfBalls == 1){
            rerack();
        }
        return ballsRemoved;
    }

    public void removeBall(){
        this.numberOfBalls -= 1;
        if (this.numberOfBalls == 1){
            rerack();
        }
    }

    public void rerack(){
        this.numberOfBalls = 15;
    }

    public boolean isValidBallNumber(int newNumberOfBalls){
        return newNumberOfBalls < this.numberOfBalls && newNumberOfBalls >= 1;
    }

    public void setNumberOfBalls(int numberOfBalls){
        this.numberOfBalls = numberOfBalls;
    }
}
