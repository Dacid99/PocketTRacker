package org.sbv.straightpoolcounter;

public class Player {
    private String name;
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

    public void addPoints(int points) {
        this.score += points;
    }

    public void deductPoints(int points) {
        this.score -= points;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setClub(String club) {
        this.club = club;
    }
}
