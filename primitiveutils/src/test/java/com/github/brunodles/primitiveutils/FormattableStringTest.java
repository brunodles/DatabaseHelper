package com.github.brunodles.primitiveutils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

/**
 * Created by bruno on 06/11/16.
 */
@RunWith(JUnit4.class)
public class FormattableStringTest {

    private FormattableString str;

    @Before
    public void setup() {
        str = new FormattableString("bruno");
    }

    @Test
    public void whenPrecisionIsOne_shouldUpperCaseFirstLetter() {
        assertEquals("Bruno", format("%.1s", str));
    }

    @Test
    public void whenPrecisionIsTwo_shouldUpperCaseTwoFirstLetters() {
        assertEquals("BRuno", format("%.2s", str));
    }

    @Test
    public void whenNoParameter_shouldUseDefaultStringFormatter() {
        assertEquals("bruno", format("%s", str));
        assertEquals("BRUNO", format("%S", str));
    }
}