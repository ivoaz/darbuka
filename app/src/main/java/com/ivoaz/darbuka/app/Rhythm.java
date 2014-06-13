package com.ivoaz.darbuka.app;

public class Rhythm {
    protected String name;
    protected String string;

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
