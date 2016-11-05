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
}
