package com.ivoaz.darbuka.app;

public class Bpm {
    protected int value;

    public Bpm(int value) {
        this.value = value;
    }

    public String toString() {
        return (Integer.toString(value) + "bpm");
    }

    public int getValue() {
        return value;
    }
}
