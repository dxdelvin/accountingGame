package com.accounting.main;

import java.util.Map;

public class Dialogue {
    private int id;
    private String type;
    private String text;
    private Object nextId; // This will be an Integer or a Map depending on the nextId type
    private String[] options;

    // Getters and Setters
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
