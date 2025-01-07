package com.accounting.main;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dialogue {
    private int id;
    private String type;
    private String text;
    private String characterImagePath;
    private Object nextId;  // This is for handling cases where it could be an integer or a map
    private String[] options;
    private int nextIdCorrect;  // For Input type
    private int nextIdIncorrect; // For incorrect path
    private String correctAnswer;

    // Default constructor
    public Dialogue() {}

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCharacterImagePath() {
        return characterImagePath;
    }

    public void setCharacterImagePath(String characterImagePath) {
        this.characterImagePath = characterImagePath;
    }

    public Object getNextId() {
        return nextId;
    }

    public void setNextId(Object nextId) {
        this.nextId = nextId;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getNextIdCorrect() {
        return nextIdCorrect;
    }

    public void setNextIdCorrect(int nextIdCorrect) {
        this.nextIdCorrect = nextIdCorrect;
    }

    public int getNextIdIncorrect() {
        return nextIdIncorrect;
    }

    public void setNextIdIncorrect(int nextIdIncorrect) {
        this.nextIdIncorrect = nextIdIncorrect;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
