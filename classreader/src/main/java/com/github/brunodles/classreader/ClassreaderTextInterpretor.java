package com.github.brunodles.classreader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ClassreaderTextInterpretor implements ClassReader {

    public static final String CLASS_NAME_REGEX = "class +(\\w+) *\\{";
    public static final String FIELD_REGEX = "(\\w* +)?(\\w++) +(\\w+)(:?.*);";
    public static final List<String> INVALID_MODIFICATORS = Arrays.asList("static", "final", "abstract");

    private final String className;
    private final ArrayList<Field> fields;
    private final String filePath;
    private final String fileBody;

    public ClassreaderTextInterpretor(String filePath) throws IOException {
        super();
        this.filePath = filePath;
        this.fileBody = loadFile(filePath);
        this.className = findClassName(fileBody);
        this.fields = findFields(fileBody);
    }

    private static String loadFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileReader reader = new FileReader(file);
        char[] buffer = new char[512];
        int read;
        StringBuilder builder = new StringBuilder();
        while ((read = reader.read(buffer)) > -1) {
            builder.append(buffer, 0, read);
        }
        reader.close();
        return builder.toString();
    }

    private static String findClassName(String fileBody) {
        Matcher matcher = Pattern.compile(CLASS_NAME_REGEX).matcher(fileBody);
        if (matcher.find())
            return matcher.group(1);
        return "";
    }

    private static ArrayList<Field> findFields(String fileBody) {
        ArrayList<Field> fields = new ArrayList<>();
        Pattern pattern = Pattern.compile(FIELD_REGEX);
        Matcher matcher = pattern.matcher(fileStrClassBody(fileBody));
        while (matcher.find()) {
            Field field = createFieldWithMatcher(matcher);
            if (field == null) continue;
            fields.add(field);
        }
        return fields;
    }

    private static String fileStrClassBody(String fileBody) {
        int firstBracer = fileBody.indexOf("{");
        int lastBracer = fileBody.lastIndexOf("}");
        return fileBody.substring(firstBracer, lastBracer);
    }

    private static Field createFieldWithMatcher(Matcher matcher) {
        if (haveInvalidModificators(matcher.group(1)))
            return null;
        return new Field(matcher.group(3), matcher.group(2));
    }

    private static boolean haveInvalidModificators(String modificatorsStr) {
        if (modificatorsStr == null || modificatorsStr.isEmpty()) {
            return false;
        }
        String[] modificators = modificatorsStr.split(" ");
        for (String modificator : modificators) {
            if (INVALID_MODIFICATORS.contains(modificator)) {
                return true;
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see bruno.android.helper.ClassReader#getFieldsNames()
	 */
    public List<String> getFieldsNames() {
        ArrayList<String> list = new ArrayList<>();
        for (Field field : fields) {
            list.add(field.name);
        }
        return list;
    }

    /* (non-Javadoc)
     * @see bruno.android.helper.ClassReader#getTableName()
	 */
    @Override
    public String getClassName() {
        return className;
    }

    /* (non-Javadoc)
     * @see bruno.android.helper.ClassReader#getFields()
	 */
    @Override
    public List<Field> getFields() {
        return Collections.unmodifiableList(fields);
    }

    /* (non-Javadoc)
     * @see bruno.android.helper.ClassReader#getField(int)
	 */
    @Override
    public Field getField(int index) {
        return fields.get(index);
    }
}