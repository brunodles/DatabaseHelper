package com.github.brunodles.primitiveutils;

import java.util.Formattable;
import java.util.Formatter;

import static java.util.FormattableFlags.ALTERNATE;
import static java.util.FormattableFlags.UPPERCASE;

/**
 * Created by bruno on 06/11/16.
 */

public class FormattableString implements Formattable {
    private final String string;

    public FormattableString(String string) {
        this.string = string;
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        boolean uppercase = (flags & UPPERCASE) == UPPERCASE;
        String result = string;
        if (uppercase)
            if (precision > 0)
                result = string.substring(0, precision).toLowerCase()
                        + string.substring(precision).toUpperCase();
            else
                result = string.toUpperCase();
        else if (precision > 0)
            result = string.substring(0, precision).toUpperCase()
                    + string.substring(precision);

        formatter.format(result);
    }

    public static FormattableString formattable(String string){
        return new FormattableString(string);
    }
}
