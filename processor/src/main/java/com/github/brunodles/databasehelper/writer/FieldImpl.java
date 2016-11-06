package com.github.brunodles.databasehelper.writer;

/**
 * Created by bruno on 06/11/16.
 */
public class FieldImpl extends Element implements FieldBuilder {
    String value;

    FieldImpl(String visibility, String name, String type, String... modifiers) {
        super(visibility, name, type, modifiers);
    }

    public String value() {
        return value;
    }

    @Override
    public void value(String value) {
        this.value = value;
    }
}
