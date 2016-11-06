package com.github.brunodles.databasehelper.writer;

/**
 * Created by bruno on 06/11/16.
 */
public interface MethodBuilder {
    MethodBuilder addLine(String string);

    MethodBuilder addParam(String name, String type);

    MethodBuilder addParam(String name, Class type);
}
