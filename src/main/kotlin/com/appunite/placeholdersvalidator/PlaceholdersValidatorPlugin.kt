package com.appunite.placeholdersvalidator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.CompositeFileTree

const val EXTENSION_NAME = "placeholdersValidator"
const val TASK_NAME = "placeholdersValidatorTask"

abstract class PlaceholdersValidatorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configurations.create("placeholdersValidator").apply {
            isVisible = false
            description = "Dependencies required by the PlaceholdersValidatorPlugin"
        }

        val extension = project.extensions
            .create(EXTENSION_NAME, PlaceholdersValidatorExtension::class.java)

        project.tasks.register(TASK_NAME, PlaceholdersValidatorTask::class.java) { task ->
            task.resourcesDir.set(extension.resourcesDir)
        }
    }

}

open class PlaceholdersValidatorExtension {
    lateinit var resourcesDir: CompositeFileTree
}
