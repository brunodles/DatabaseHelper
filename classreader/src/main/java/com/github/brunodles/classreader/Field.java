package com.github.brunodles.classreader;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Field {

    public final String name;
    public final String type;
    private final List<String> modifiers = new ArrayList<>();

    public Field(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public List<String> getModifiers() {
        return unmodifiableList(modifiers);
    }

    void addModifier(String name) {
        modifiers.add(name);
    }
}
