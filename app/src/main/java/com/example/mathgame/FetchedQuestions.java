package com.example.mathgame;

public class FetchedQuestions {
    private String question, answer;

    // constructor
    FetchedQuestions(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    // answer getter
    public String getAnswer() {
        return answer;
    }

    // question getter
    public String getQuestion() {
        return question;
    }
}
