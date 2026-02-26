package com.example.arcaderacing.scores;

public class RacingScoreItem {
    public String username;
    public int timeSeconds;   // tiempo guardado en segundos
    public String date;

    public RacingScoreItem(String username, int timeSeconds, String date) {
        this.username = username;
        this.timeSeconds = timeSeconds;
        this.date = date;
    }
}
