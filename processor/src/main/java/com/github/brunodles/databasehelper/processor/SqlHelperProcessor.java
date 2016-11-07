package com.github.brunodles.databasehelper.processor;


import com.github.brunodles.annotationprocessorhelper.AbstractProcessorBase;
import com.github.brunodles.annotationprocessorhelper.SupportedAnnotations;
import com.github.brunodles.classreader.ClassReader;
import com.github.brunodles.classreader.Field;
import com.github.brunodles.databasehelper.annotation.SqlHelper;
import com.github.brunodles.databasehelper.annotation.SqlHelpers;
import com.github.brunodles.databasehelper.writer.MethodBuilder;
import com.github.brunodles.databasehelper.writer.OutputClass;
import com.github.brunodles.sqlhelper.CreateBuilder;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import static com.github.brunodles.databasehelper.processor.Utils.readFieldFromDb;
import static com.github.brunodles.primitiveutils.FormattableString.formattable;
import static com.github.brunodles.primitiveutils.StringUtils.quote;
import static java.lang.String.format;

@SupportedAnnotations({SqlHelper.class, SqlHelpers.class})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SqlHelperProcessor extends AbstractProcessorBase {

    private static final String TAG = "[ SqlHelperProcessor ]";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement typeElement : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                List<OutputClass> outputClasses = checkSqlHelpers(element);
                OutputClass outputClass = checkSqlHelper(element);
                if (outputClass != null) outputClasses.add(outputClass);
                createHelper(element, outputClasses);
            }
        }
        return true;
    }

    private void createHelper(Element element, List<OutputClass> sqlHelperClasses) {
        String className = element.getSimpleName() + "_Helper";
        String packageName = element.toString().replace("." + element.getSimpleName(), "");

        OutputClass outputClass = new OutputClass(packageName, className)
                .addImport("android.database.sqlite.SQLiteDatabase");
        MethodBuilder onCreate = outputClass.addMethod("public", "onCreate", "void", "static")
                .addParam("db", "SQLiteDatabase");
        for (OutputClass aClass : sqlHelperClasses) {
            outputClass.addImport(aClass.packageName() + "." + aClass.className());
            onCreate.addLine("db.execSQL(" + aClass.className() + ".CREATE_TABLE);");
        }

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("class.mustache");
        StringWriter writer = new StringWriter();
        try {
            log(TAG, String.format("build helper class -> %s.%s", packageName, className));
            mustache.execute(writer, outputClass).flush();
            log(TAG, writer.toString());
            writeClass(className, packageName, writer.toString());
        } catch (IOException e1) {
            fatalError(TAG, e1.getMessage());
        }
    }

    private List<OutputClass> checkSqlHelpers(Element element) {
        SqlHelpers annotation = element.getAnnotation(SqlHelpers.class);
        ArrayList<OutputClass> outputClasses = new ArrayList<>();
        if (annotation == null) return outputClasses;
        SqlHelper[] value = annotation.value();
        for (SqlHelper sqlHelper : value)
            outputClasses.add(buildHelperClass(element, sqlHelper));
        return outputClasses;
    }

    private OutputClass checkSqlHelper(Element e) {
        SqlHelper a = e.getAnnotation(SqlHelper.class);
        if (a == null) return null;
        return buildHelperClass(e, a);
    }

    private OutputClass buildHelperClass(Element element, SqlHelper annotation) {
        String fieldReadMethod = annotation.fieldGetter().method;
        String fieldWriteMethod = annotation.fieldSetter().method;
        TypeElement value = asTypeElement(getMyValue1(annotation));

        ClassReader reader = ClassReader.Factory.read(this.processingEnv, value);

        String className = value.getSimpleName() + "SqlHelper";
        String packageName = element.toString().replace("." + element.getSimpleName(), "");
        String tableName = Utils.classNameToTableName(value.getSimpleName().toString());

        OutputClass outputClass = new OutputClass(packageName, className);
        outputClass.addImport("android.content.ContentValues")
                .addImport("android.support.annotation.NonNull")
                .addImport("android.database.Cursor")
                .addImport(value.getQualifiedName().toString());

        outputClass.addPublicConstant("TABLE_NAME", String.class).value(quote(tableName));
        CreateBuilder createBuilder = com.github.brunodles.sqlhelper.SqlHelper.create(tableName);
        MethodBuilder contentValues = outputClass
                .addMethod("public", "createValues", "ContentValues", "static", "final")
                .addParam("object", reader.getClassName())
                .addLine("ContentValues values = new ContentValues();");
        MethodBuilder readCursor = outputClass
                .addMethod("public", "readCursor", reader.getClassName(), "static", "final")
                .addParam("cursor", "Cursor")
                .addLine(String.format("%1$s object = new %1$s();", reader.getClassName()));

        for (Field field : reader.getFields()) {
            outputClass.addImport(field.typeCanonical);

            String fieldKey = Utils.dbFieldKey(field);
            outputClass.addPublicConstant(fieldKey, String.class)
                    .value(quote(field.name));
            createBuilder.add(field.name, Utils.dbFieldType(field));
            contentValues.addLine(format("values.put(%s, object." + fieldReadMethod + ");",
                    fieldKey, field.name));

            readCursor.addLine(format("object." + fieldWriteMethod + ";", formattable(field.name),
                    readFieldFromDb(field)));
        }
        readCursor.addLine("return object;");
        contentValues.addLine("return values;");

        outputClass.addPublicConstant("CREATE_TABLE", String.class)
                .value(quote(createBuilder.build()));

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("class.mustache");
        StringWriter writer = new StringWriter();
        try {
            log(TAG, String.format("build helper class -> %s.%s", packageName, className));
            mustache.execute(writer, outputClass).flush();
            log(TAG, writer.toString());
            writeClass(className, packageName, writer.toString());
        } catch (IOException e1) {
            fatalError(TAG, e1.getMessage());
        }
        return outputClass;
    }

    private static TypeMirror getMyValue1(SqlHelper annotation) {
        try {
            annotation.value(); // this should throw
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null; // can this ever happen ??
    }

    private static AnnotationMirror getAnnotationMirror(TypeElement typeElement, Class<?> clazz) {
        String clazzName = clazz.getName();
        for (AnnotationMirror m : typeElement.getAnnotationMirrors()) {
            if (m.getAnnotationType().toString().equals(clazzName)) {
                return m;
            }
        }
        return null;
    }

    private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }


    public TypeMirror getMyValue2(TypeElement foo) {
        AnnotationMirror am = getAnnotationMirror(foo, SqlHelper.class);
        if (am == null) {
            return null;
        }
        AnnotationValue av = getAnnotationValue(am, "value");
        if (av == null) {
            return null;
        } else {
            return (TypeMirror) av.getValue();
        }
    }

    private TypeElement asTypeElement(TypeMirror typeMirror) {
        Types TypeUtils = this.processingEnv.getTypeUtils();
        return (TypeElement) TypeUtils.asElement(typeMirror);
    }

    private void writeClass(String className, String packageName, String content) {
        OutputStreamWriter osw = null;
        try {
            // create a model file
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(packageName + "." + className,
                    elementUtils.getTypeElement(packageName));
            OutputStream os = fileObject.openOutputStream();
            osw = new OutputStreamWriter(os, Charset.forName("UTF-8"));
            osw.write(content, 0, content.length());

        } catch (IOException ex) {
            ex.printStackTrace();
            fatalError(TAG, ex.getMessage());
        } finally {
            try {
                if (osw != null) {
                    osw.flush();
                    osw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                fatalError(TAG, ex.getMessage());
            }
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

}
