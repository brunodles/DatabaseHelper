package com.github.brunodles.classreader;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Created by bruno de lima on 20/04/14.
 */

class ClassReaderReflection implements ClassReader {

    public static final List<String> INVALID_MODIFICATORS = unmodifiableList(asList(
            "static", "final", "abstract"));
    private Class aClass;
    private List<Field> fields;

    public ClassReaderReflection(Class aClass) {
        super();
        this.aClass = aClass;
        fields = findFields(aClass);
    }

    private static List<Field> findFields(Class aClass) {
        List<Field> fields = new ArrayList<>();
        for (java.lang.reflect.Field field : aClass.getDeclaredFields()) {
            Field newField = buildField(field);
            fields.add(newField);
        }
        Class superclass = aClass.getSuperclass();
        if (superclass != Object.class)
            fields.addAll(findFields(superclass));
        return fields;
    }

    private static Field buildField(java.lang.reflect.Field field) {
        Field newField = new Field(field.getName(), field.getType().getCanonicalName());
        Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
        for (Annotation declaredAnnotation : declaredAnnotations) {
            if (declaredAnnotation.getClass().getName().contains("NonNull"))
                newField.addModifier("NonNull");
        }
        int modifiers = field.getModifiers();
        // TODO verify if is a valid modifiers
        return newField;
    }

    public List<String> getFieldsNames() {
        ArrayList<String> result = new ArrayList<String>();
        for (Field field : fields) {
            result.add(field.name);
        }
        return result;
    }

    public String getClassName() {
        return aClass.getSimpleName();
    }

    public List<Field> getFields() {
        return unmodifiableList(fields);
    }

    public Field getField(int index) {
        return fields.get(index);
    }

    public String typeClassName() {
        return aClass.getSimpleName();
    }

}
