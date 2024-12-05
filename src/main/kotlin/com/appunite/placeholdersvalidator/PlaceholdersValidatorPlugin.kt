package com.appunite.placeholdersvalidator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

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
        }
    }

}

abstract class PlaceholdersValidatorExtension(objects: ObjectFactory) {
    val resourcesDir: Property<FileTree> = objects.property(FileTree::class.java)
    val ignorePlurals: Property<Boolean> = objects.property(Boolean::class.java)
}
