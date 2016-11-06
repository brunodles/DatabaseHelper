package com.github.brunodles.databasehelper.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bruno on 05/11/16.
 */

public class OutputClass {

    private final String packageName;
    private final String className;
    private final Collection<String> imports = new HashSet<>();
    private final Map<String, FieldImpl> fields = new LinkedHashMap<>();
    private final List<MethodImpl> methods = new ArrayList<>();

    public OutputClass(String className) {
        this.className = className;
        this.packageName = null;
    }

    public OutputClass(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public OutputClass addImport(Class aClass) {
        imports.add(aClass.getCanonicalName());
        return this;
    }

    public OutputClass addImport(String canonicalClassName) {
        if (canonicalClassName != null && !canonicalClassName.isEmpty() )
            imports.add(canonicalClassName);
        return this;
    }

    public FieldBuilder addPublicConstant(String name, Class type) {
        addImport(type);
        return addPublicConstant(name, type.getSimpleName());
    }

    public FieldBuilder addPublicConstant(String name, String type) {
        return addField("public", name, type, "static", "final");
    }

    public FieldBuilder addField(String visibility, String name, Class type, String... modifiers) {
        addImport(type);
        return addField(visibility, name, type.getSimpleName(), modifiers);
    }

    public FieldBuilder addField(String visibility, String name, String type, String... modifiers) {
        FieldImpl field = new FieldImpl(visibility, name, type, modifiers);
        fields.put(name, field);
        return field;
    }

    public MethodBuilder addMethod(String visibility, String name, String type, String... modifiers) {
        MethodImpl method = new MethodImpl(visibility, name, type, modifiers);
        methods.add(method);
        return method;
    }

    public String packageName() {
        return packageName;
    }

    public List<String> importList() {
        return Collections.unmodifiableList(new ArrayList<>(imports));
    }

    public String className() {
        return className;
    }

    public List<FieldImpl> fields() {
        return Collections.unmodifiableList(new ArrayList<>(fields.values()));
    }

    public List<MethodImpl> methods() {
        return Collections.unmodifiableList(methods);
    }

}
