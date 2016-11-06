package com.github.brunodles.databasehelper.writer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by bruno on 06/11/16.
 */
public class MethodImpl extends Element implements MethodBuilder {
    final List<String> block = new LinkedList<>();
    final Map<String, String> params = new LinkedHashMap<>();

    MethodImpl(String visibility, String type, String name, String... modifiers) {
        super(visibility, type, name, modifiers);
    }

    public String params() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet())
            result.append(entry.getValue())
                    .append(" ")
                    .append(entry.getKey())
                    .append(", ");
        int length = result.length();
        if (params.size() > 0)
            result.delete(length - 2, length);
        return result.toString();
    }

    public List<String> blockLines() {
        return Collections.unmodifiableList(block);
    }

    @Override
    public MethodBuilder addLine(String string) {
        block.add(string);
        return this;
    }

    @Override
    public MethodBuilder addParam(String name, String type) {
        params.put(name, type);
        return this;
    }

    @Override
    public MethodBuilder addParam(String name, Class type) {
        params.put(name, type.getSimpleName());
        return this;
    }
}
