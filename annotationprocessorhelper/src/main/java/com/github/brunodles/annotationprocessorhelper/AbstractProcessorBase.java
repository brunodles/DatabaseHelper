package com.github.brunodles.annotationprocessorhelper;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

public abstract class AbstractProcessorBase extends AbstractProcessor {

    protected Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> strings = new HashSet<>();

        SupportedAnnotationTypes sat = this.getClass().getAnnotation(SupportedAnnotationTypes.class);
        if (sat != null) {
            String[] value = sat.value();
            Collections.addAll(strings, value);
        }

        SupportedAnnotations sa = this.getClass().getAnnotation(SupportedAnnotations.class);
        if (sa != null) {
            Class<? extends Annotation>[] value = sa.value();
            for (Class<? extends Annotation> aClass : value) {
                String canonicalName = aClass.getCanonicalName();
                strings.add(canonicalName);
            }
        }

        if (strings.isEmpty() && isInitialized())
            warnAnnotationTypeNotFound();

        return Collections.unmodifiableSet(strings);
    }

    private void warnAnnotationTypeNotFound() {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                "No SupportedAnnotationTypes annotation " +
                        "found on " + this.getClass().getName() +
                        ", returning an empty set.");
    }

    protected final void log(String tag, String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                String.format("%s - %s\n", tag, msg));
    }

    protected final void fatalError(String tag, String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                String.format("%s FATAL ERROR - %s\n", tag, msg));
    }
}
