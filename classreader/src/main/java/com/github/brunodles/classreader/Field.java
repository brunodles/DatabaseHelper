package com.github.brunodles.classreader;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Field {

    public final String name;
    public final String type;
    public final String typeCanonical;
    private final List<String> modifiers = new ArrayList<>();

    public Field(String name, String type) {
        this.name = name;
        int indexOf = type.lastIndexOf(".");
        if (indexOf == -1) {
            this.typeCanonical = null;
            this.type = type;
        } else {
            this.typeCanonical = type;
            this.type = type.substring(indexOf + 1);
        }
    }

    public List<String> getModifiers() {
        return unmodifiableList(modifiers);
    }

    void addModifier(String name) {
        modifiers.add(name);
    }
}
