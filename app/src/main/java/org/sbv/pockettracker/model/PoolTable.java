package org.sbv.pockettracker.model;

public class PoolTable {
    public static final int MAXBALLNUMBER = 15;
    private int numberOfBalls ;
    private int oldNumberOfBalls ;


    public PoolTable(){
        rerack();
    }
    public final int getNumberOfBalls(){
        return this.numberOfBalls;
    }

    public final int getOldNumberOfBalls(){
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

    public void rerack(){
        this.numberOfBalls = MAXBALLNUMBER;
        this.oldNumberOfBalls = MAXBALLNUMBER;
    }

    public final boolean isValidBallNumber(int newNumberOfBalls){
        return newNumberOfBalls <= this.oldNumberOfBalls && newNumberOfBalls >= 1;
    }

    public void setNumberOfBalls(int newNumberOfBalls){
        if (isValidBallNumber(newNumberOfBalls)) {
            this.numberOfBalls = newNumberOfBalls;
        }
    }

    public void setOldNumberOfBalls(int newOldNumberOfBalls) {
        this.oldNumberOfBalls = newOldNumberOfBalls;
    }
}
