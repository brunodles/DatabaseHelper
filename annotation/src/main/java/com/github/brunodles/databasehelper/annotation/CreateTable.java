package com.github.brunodles.databasehelper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by bruno on 04/11/16.
 */

@Documented
@Target(TYPE)
@Retention(CLASS)
public @interface CreateTable {
    Class value();

    FieldGetter fieldGetter() default FieldGetter.FIELD;

    FieldSetter fieldSetter() default FieldSetter.FIELD;

    public static enum FieldGetter {
        FIELD("%s"),
        METHOD("%s()"),
        GET_METHOD("get%s()");
        public final String method;

        FieldGetter(String method) {
            this.method = method;
        }
    }

    public static enum FieldSetter {
        FIELD("%s = %s"),
        METHOD("%s(%s)"),
        SET_METHOD("set%.1s(%s)");
        public final String method;

        FieldSetter(String method) {
            this.method = method;
        }
    }
}
