package org.sbv.pockettracker;

public class Player {
    static int winningPoints = 40; //default value
    private String name = "";
    private String club = "";
    private int score;

    public Player(String name, String club) {
        this.name = name;
        this.club = club;
        this.score = 0;
    }

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    public Player() {
        this.score = 0;
    }

    public void addPoints(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public String getClub() {
        return club;
    }

    public boolean isWinner(){
        return score >= winningPoints;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void swapNameAndClubWith(Player otherPlayer){
        String nameBackup = otherPlayer.getName();
        String clubBackup = otherPlayer.getClub();

        otherPlayer.setName(this.name);
        otherPlayer.setClub(this.club);

        this.name = nameBackup;
        this.club = clubBackup;

        System.out.println(this.name + this.club + otherPlayer.getName() + otherPlayer.getClub());
    }

}
