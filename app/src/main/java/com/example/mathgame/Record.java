package com.example.mathgame;

public class Record {
    String date, time;
    int gameID, correctCount, wrongCount;
    double duration;

    // constructor
    public Record(int gameID, String date, String time, double duration, int correctCount, int wrongCount) {
        this.gameID = gameID;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.correctCount = correctCount;
        this.wrongCount = wrongCount;
    }
}
