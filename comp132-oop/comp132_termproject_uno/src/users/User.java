package users;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password; 
    private int totalScore;
    private int gamesPlayed;
    private int wins;
    private int losses;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.totalScore = 0;
        this.gamesPlayed = 0;
        this.wins = 0;
        this.losses = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void addScore(int score) {
        this.totalScore += score;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        this.wins++;
    }

    public int getLosses() {
        return losses;
    }

    public void incrementLosses() {
        this.losses++;
    }

    public double getWinLossRatio() {
        return gamesPlayed == 0 ? 0 : (double) wins / gamesPlayed;
    }

    public double getAverageScorePerGame() {
        return gamesPlayed == 0 ? 0 : (double) totalScore / gamesPlayed;
    }
}
