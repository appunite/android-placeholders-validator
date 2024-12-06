package com.appunite.placeholdersvalidator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty

const val EXTENSION_NAME = "placeholdersValidator"
const val TASK_NAME = "placeholdersValidatorTask"

abstract class PlaceholdersValidatorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configurations.create("placeholdersValidator").apply {
            isVisible = false
            description = "Dependencies required by the PlaceholdersValidatorPlugin"
        }

        val extension = project.extensions
            .create(EXTENSION_NAME, PlaceholdersValidatorExtension::class.java, project.objects)

        project.tasks.register(TASK_NAME, PlaceholdersValidatorTask::class.java) { task ->
            task.resourcesDir.set(extension.resourcesDir)
            task.ignorePlurals.set(extension.ignorePlurals)
            task.ignoredOrderLanguages.set(extension.ignoredOrderLanguages)
        }
    }

}

abstract class PlaceholdersValidatorExtension(objects: ObjectFactory) {
    lateinit var resourcesDir: FileTree
    var ignorePlurals: Boolean = false
    var ignoredOrderLanguages: SetProperty<String> = objects.setProperty(String::class.java).apply {
        set(emptySet<String>())
    }
}
