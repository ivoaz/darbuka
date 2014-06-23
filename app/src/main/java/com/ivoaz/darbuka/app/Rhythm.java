package com.ivoaz.darbuka.app;

public class Rhythm {
    private String name;
    private String string;

    public Rhythm(String name, String string) {
        this.name = name;
        this.string = string;
    }

    public String toString() {
        return name;
    }

    public String getString() {
        return string;
    }

    public String getName() {
        return name;
    }
}
