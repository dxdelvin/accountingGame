package com.accounting.main;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dialogue {

    private int id;
    private String type;
    private String text;
    private String characterImagePath;
    private Object nextId;
    private String[] options;

    // Default constructor
    public Dialogue() {
    }

    // Optional constructor for manual initialization
    public Dialogue(int id, String type, String text, String characterImagePath, Object nextId, String[] options) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.characterImagePath = characterImagePath;
        this.nextId = nextId;
        this.options = options;
    }

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

}
