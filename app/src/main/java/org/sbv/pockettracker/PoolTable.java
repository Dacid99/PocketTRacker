package org.sbv.pockettracker;

public class PoolTable {
    private int numberOfBalls ;
    private int oldNumberOfBalls ;


    public PoolTable(){
        rerack();
    }
    public int getNumberOfBalls(){
        return this.numberOfBalls;
    }

    public int getOldNumberOfBalls(){
        return this.oldNumberOfBalls;
    }

    public int evaluate(){
        int ballsRemoved = this.oldNumberOfBalls - numberOfBalls;
        if (this.numberOfBalls == 1){
            rerack();
        }
        oldNumberOfBalls = numberOfBalls;

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
        this.oldNumberOfBalls = 15;
    }

    public boolean isValidBallNumber(int newNumberOfBalls){
        return newNumberOfBalls <= this.oldNumberOfBalls && newNumberOfBalls >= 1;
    }

    public void setNumberOfBalls(int newNumberOfBalls){
        if (isValidBallNumber(numberOfBalls)) {
            this.numberOfBalls = newNumberOfBalls;
        }
    }
}
