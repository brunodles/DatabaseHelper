package com.github.brunodles.databasehelper.writer;

/**
 * Created by bruno on 06/11/16.
 */
public class Element {
    private String visibility;
    private String name;
    private String type;
    private String[] modifiers;

    Element(String visibility, String name, String type, String... modifiers) {
        this.visibility = visibility;
        this.type = type;
        this.name = name;
        this.modifiers = modifiers;
    }

    String name() {
        return name;
    }

    public String visibility() {
        return visibility;
    }

    public String[] modifiers() {
        return modifiers;
    }

    public String type() {
        return type;
    }
}
