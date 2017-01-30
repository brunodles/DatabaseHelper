package nomads

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies

class SqlNight implements Plugin<Project> {

    public static final String VERSION = "0.1";
    public static final String FILE_EXTENSION = "migration";

    void apply(Project project) {
        project.plugins.all { it ->
            if (it instanceof AppPlugin)
                configure(project, project.extensions.findByName("android").applicationVariants)
//                configure(project, project.extensions.getByType(AppExtension.class).applicationVariants)
            else if (it instanceof LibraryPlugin)
                configure(project, project.extensions.getByType(LibraryExtension.class).libraryVariants)
        }
    }

    private void configure(Project project, DomainObjectSet<? extends BaseVariant> variants) {
        def generateSqlDelight = project.task("generateSqlNightClasses")
        generateSqlDelight.group = "sqlNight"

//        val compileDeps = project.configurations.getByName("compile").dependencies
//        project.gradle.addListener(new DependencyResolutionListener() {
//            @Override
//            void beforeResolve(ResolvableDependencies resolvableDependencies) {
//                if (System.getProperty("sqldelight.skip.runtime") != "true") {
//                    compileDeps.add(project.dependencies.create("com.squareup.sqldelight:runtime:$VERSION"))
//                }
//                compileDeps.add(
//                        project.dependencies.create("com.android.support:support-annotations:23.1.1"))
//
//                project.gradle.removeListener(this)
//            }
//
//            @Override
//            void afterResolve(ResolvableDependencies resolvableDependencies) {
//
//            }
//        })

        variants.all {
            def taskName = "generate${it.name.capitalize()}SqlNightClasses"
            def task = project.tasks.create(taskName, SqlNightTask.class)
            task.group = "sqlNight"
            task.buildDirectory = project.buildDir
            task.description = "Generate Android interfaces for working with ${it.name} database tables"
            task.source("src")
            task.include("**${File.separatorChar}*.$FILE_EXTENSION")

            generateSqlDelight.dependsOn(task)

            it.registerJavaGeneratingTask(task, task.outputDirectory)
        }
    }

}
