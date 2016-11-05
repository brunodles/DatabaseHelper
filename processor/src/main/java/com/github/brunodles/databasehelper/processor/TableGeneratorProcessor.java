package com.github.brunodles.databasehelper.processor;


import com.github.brunodles.annotationprocessorhelper.AbstractProcessorBase;
import com.github.brunodles.annotationprocessorhelper.SupportedAnnotations;
import com.github.brunodles.classreader.ClassReader;
import com.github.brunodles.classreader.Field;
import com.github.brunodles.databasehelper.annotation.CreateTable;
import com.github.brunodles.sqlhelper.CreateBuilder;
import com.github.brunodles.sqlhelper.SqlHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
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

import static java.lang.String.format;

//@com.google.auto.service.AutoService(Processor.class)
@SupportedAnnotations({CreateTable.class})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TableGeneratorProcessor extends AbstractProcessorBase {

    private static final String TAG = "[ TableGeneratorProcessor ]";
    public static final String CONTENT_VALUES_DECLARATION = "@NonNull private static ContentValues updateValues(%s object) {\nContentValues values = new ContentValues();\n";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                CreateTable a = e.getAnnotation(CreateTable.class);
                TypeElement value = asTypeElement(getMyValue1(a));

                ClassReader reader = ClassReader.Factory.read(this.processingEnv, value);

                String className = value.getSimpleName() + "SqlHelper";
                String packageName = e.toString().replace("." + e.getSimpleName(), "");
                String tableName = Utils.classNameToTableName(value.getSimpleName().toString());

                CreateBuilder createBuilder = SqlHelper.create(tableName);
                StringBuilder constantsFields = new StringBuilder()
                        .append("public static final String TABLE_NAME = \"")
                        .append(tableName).append("\";\n\n");
                StringBuilder contentValuesBuilder = new StringBuilder()
                        .append(format(CONTENT_VALUES_DECLARATION, reader.getClassName()));

                for (Field field : reader.getFields()) {
                    createBuilder.add(field.name, Utils.dbFieldType(field));
                    constantsFields.append("public static final String F_")
                            .append(field.name.toUpperCase())
                            .append(" = \"")
                            .append(field.name)
                            .append("\";\n");
                    contentValuesBuilder.append(format("values.put(F_%s, object.%s);\n", field.name.toUpperCase(), field.name));
                }

                contentValuesBuilder.append("return values;\n}");

                StringBuilder imports = new StringBuilder()
                        .append("import android.content.ContentValues;\n")
                        .append("import android.support.annotation.NonNull;\n")
                        .append("import ").append(value.getQualifiedName()).append(";\n");

                StringBuilder body = new StringBuilder()
                        .append("package ")
                        .append(packageName)
                        .append(";\n\n")
                        .append(imports)
                        .append("\n\n")
                        .append("public final class ")
                        .append(className)
                        .append(" {\n\n")
                        .append(constantsFields)
                        .append("\n")
                        .append("public static final String CREATE_TABLE = ")
                        .append("\"")
                        .append(createBuilder.build())
                        .append("\"")
                        .append(";\n\n")
                        .append(contentValuesBuilder)
                        .append("\n\n")
                        .append("private ").append(className).append("(){}")
                        .append("\n}");

                System.out.printf("write class -> %s.%s\n", packageName, className);
                writeClass(className, packageName, body.toString());
            }
        }
        return true;
    }

    private static TypeMirror getMyValue1(CreateTable annotation) {
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
        AnnotationMirror am = getAnnotationMirror(foo, CreateTable.class);
        if (am == null) {
            return null;
        }
        AnnotationValue av = getAnnotationValue(am, "myValue");
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
            fatalError(ex.getMessage());
        } finally {
            try {
                if (osw != null) {
                    osw.flush();
                    osw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                fatalError(ex.getMessage());
            }
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

}
