package com.github.brunodles.classreader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Created by bruno on 04/11/16.
 */

class ClassReaderTypeElement implements ClassReader {

    private final ProcessingEnvironment processingEnv;
    private final TypeElement typeElement;
    private final List<Field> fields;

    public ClassReaderTypeElement(ProcessingEnvironment processingEnv, TypeElement typeElement) {
        this.processingEnv = processingEnv;
        this.typeElement = typeElement;
        fields = findFields(processingEnv, typeElement);
    }

    private static List<Field> findFields(ProcessingEnvironment processingEnv, TypeElement typeElement) {
        ArrayList<Field> fields = new ArrayList<>();
        for (Element element : typeElement.getEnclosedElements()) {
            if (!element.getKind().isField()) continue;
            String type = element.asType().toString();
            Field field = new Field(element.getSimpleName().toString(), type);
            fields.add(field);
        }
        TypeElement superclass = asTypeElement(processingEnv, typeElement.getSuperclass());
        if (!superclass.getQualifiedName().toString().equals("java.lang.Object"))
            fields.addAll(findFields(processingEnv, superclass));
        return fields;
    }

    private static TypeElement asTypeElement(ProcessingEnvironment processingEnv, TypeMirror typeMirror) {
        Types TypeUtils = processingEnv.getTypeUtils();
        return (TypeElement) TypeUtils.asElement(typeMirror);
    }

    @Override
    public String getClassName() {
        return typeElement.getSimpleName().toString();
    }

    @Override
    public List<Field> getFields() {
        return Collections.unmodifiableList(fields);
    }

    @Override
    public Field getField(int index) {
        return fields.get(index);
    }
}
