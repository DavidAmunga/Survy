package com.buttercell.survy.model;

/**
 * Created by amush on 09-Mar-18.
 */

public class Answer {
    private String answer, username;


    public Answer(String answer, String username) {
        this.answer = answer;
        this.username = username;

    }

    public Answer() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
