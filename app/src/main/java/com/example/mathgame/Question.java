package com.example.mathgame;

public class Question {
    String question, correctness;
    int questionID, answer, yourAnswer;

    // constructor
    public Question(int questionID, String question, int answer, int yourAnswer) {
        this.questionID = questionID;
        this.question = question;
        this.answer = answer;
        this.yourAnswer = yourAnswer;
        if (answer == yourAnswer)
            correctness = "Correct";
        else
            correctness = "Incorrect";
    }
}
