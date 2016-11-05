package com.github.brunodles.classreader;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Created by bruno on 04/11/16.
 */

public interface ClassReader {

    String getClassName();

    List<Field> getFields();

    Field getField(int index);

    public static final class Factory {

        public static ClassReader read(String filePath) throws IOException {
            return new ClassreaderTextInterpretor(filePath);
        }

        public static ClassReader read(Class aClass) {
            return new ClassReaderReflection(aClass);
        }

        public static ClassReader read(ProcessingEnvironment processingEnv, TypeElement typeElement) {
            return new ClassReaderTypeElement(processingEnv, typeElement);
        }

        private Factory() {
        }
    }
}
