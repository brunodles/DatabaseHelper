package nomads

import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.incremental.IncrementalTaskInputs

public class SqlNightTask extends SourceTask {

    public static final String OUTPUT_DIRECTORY = "sqlNight"
    public File buildDirectory
    public File outputDirectory

    @TaskAction
    def execute(IncrementalTaskInputs inputs) {
        Map<File, String> map = new LinkedHashMap<>();
        getInputs().files.forEach { file ->
            println "SqlNight file -> ${file.getAbsolutePath()}"
            def body = "import android.util.Log;\n" +
                    "import android.database.sqlite.SQLiteDatabase;\n\n" +
                    "public final class ${file.text.capitalize()} {\n" +
                    "\tpublic static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {\n" +
                    "\t\tLog.d(\"${file.text.capitalize()}\", \"onUpgrade: \");\n" +
                    "\t}\n" +
                    "}"
            map.put(file, body)
        }
        outputDirectory.mkdirs()

        inputs.outOfDate({ inputFileDetails ->
            def body = map[inputFileDetails.file]
            if (body == null) {
                inputFileDetails.file.delete()
                return
            }

            def filename = inputFileDetails.file.text
            def classPackage = toPackage(inputFileDetails.file)
            def outputDirectory = new File(this.outputDirectory, classPackage.replace(".", "/"))
            outputDirectory.mkdirs()
            def output = new File(outputDirectory, filename + ".java")
            output.createNewFile()
            println "MigrationClass -> ${output.getAbsolutePath()}"

            output.text = "package ${classPackage};\n$body"
            println output.text
            inputFileDetails.added
        })

        inputs.removed( { inputFileDetails ->
            def body = map[inputFileDetails.file]
            println "Removed file? ${inputFileDetails.file}"
            if (body == null) {
                println " yes"
                inputFileDetails.file.delete()
            }
        })
    }

    String toPackage(File file) {
        return relativePath(file).dropRight(1).join(".")
    }

    String[] relativePath(File file) {
        String[] parts = file.getAbsolutePath().split(File.separator)
        int srcIndex = -1;
        def strings = new ArrayList<String>()
        for (int i = 0; i < parts.size(); i++) {
            if (parts[i] == "src") {
                srcIndex = i
                continue
            }
            if (srcIndex < 0) continue
            if (i < srcIndex + 3) continue
            strings.add(parts[i])
        }
        return strings.toArray()
    }

    void setBuildDirectory(File buildDirectory) {
        this.buildDirectory = buildDirectory
//        this.outputDirectory = buildDirectory;
        this.outputDirectory = new File(buildDirectory, OUTPUT_DIRECTORY)
    }

}