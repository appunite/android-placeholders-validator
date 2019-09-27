package com.appunite.placeholdersvalidator

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.file.CompositeFileTree

class PlaceholdersValidatorPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.configurations.create("placeholdersValidator").apply {
            isVisible = false
            description = "Dependencies required by the PlaceholdersValidatorPlugin"
        }
        val extension = project.extensions.create("placeholdersValidator", PlaceholdersValidatorExtension::class.java)

        val validatorTask = project.tasks.create("placeholdersValidatorTask", PlaceholdersValidatorTask::class.java)
        validatorTask.doFirst { validatorTask.resourcesDir = extension.resourcesDir }

        // Run validation before all other tasks
        project.tasks.matching { it != validatorTask }.all { it.dependsOn(validatorTask) }
    }

}

open class PlaceholdersValidatorExtension {
    lateinit var resourcesDir: CompositeFileTree
}