package com.github.brunodles.databasehelper.processor;

import com.github.brunodles.classreader.Field;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.brunodles.primitiveutils.StringUtils.capitalFirst;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class Utils {

    private static final List<String> INTEGER_TYPES = asList("int", "integer", "long", "bigint", "boolean", "date");
    private static final List<String> REAL_TYPES = asList("float", "double");
    private static final List<String> TEXT_TYPES = asList("string", "list");

    public static final String CAMEL_CASE_REGEX = "(\\p{Upper}*)(\\p{Lower}+)";

    public static String classNameToTableName(String className) {
        Matcher matcher = Pattern.compile(CAMEL_CASE_REGEX).matcher(className);
        String name = "";
        while (matcher.find()) {
            String uppercase = matcher.group(1);
            String lowercase = matcher.group(2);
            if (uppercase != null && !uppercase.isEmpty()) {
                name += uppercase;
            }
            name += lowercase + "_";
        }
        return name.substring(0, name.length() - 1).toUpperCase();
    }


    public static String dbFieldType(Field field) {
        final String typeLowerCase = field.type.toLowerCase();
        if (INTEGER_TYPES.contains(typeLowerCase)) {
            return "INTEGER";
        } else if (REAL_TYPES.contains(typeLowerCase)) {
            return "REAL";
        } else if (TEXT_TYPES.contains(typeLowerCase)) {
            return "TEXT";
        }

        throw new RuntimeException("Unmapped field type for \"" + typeLowerCase + "\"");
    }

    public static String dbFieldKey(Field field) {
        return format("F_%s", field.name.toUpperCase());
    }

    public static String readFieldFromDb(Field field) {
        return format("Db.get%s(cursor, %s)", capitalFirst(field.type), dbFieldKey(field));
    }
}
