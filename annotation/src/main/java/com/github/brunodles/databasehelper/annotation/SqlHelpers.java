package com.github.brunodles.databasehelper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by bruno on 07/11/16.
 */
@Target({TYPE})
@Retention(CLASS)
public @interface SqlHelpers {
    SqlHelper[] value();
}
