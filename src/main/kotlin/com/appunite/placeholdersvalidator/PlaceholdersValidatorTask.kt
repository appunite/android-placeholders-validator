package com.appunite.placeholdersvalidator

import groovy.util.Node
import groovy.util.XmlParser
import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.internal.file.CompositeFileTree
import org.gradle.api.tasks.TaskAction
import java.io.File

open class PlaceholdersValidatorTask : DefaultTask() {

    lateinit var resourcesDir: CompositeFileTree
    private val validator = PlaceholdersValidator()

    @TaskAction
    fun validateStringsPlaceholders() {
        description = "Validates placeholders from translated strings.xml files"

        val mainStringsFile: File = resourcesDir.find {
            it.absolutePath.endsWith("/values/strings.xml")
        }!!

        val translatedStrings: List<File> = resourcesDir.filter {
            it.name == "strings.xml" && !it.absolutePath.endsWith("/values/strings.xml")
        }.files.toList()

        val mainFilePlaceholders: PlaceholdersForFile = createStringPlaceholdersMap(mainStringsFile)
        val translatedFilesPlaceholders: List<PlaceholdersForFile> = translatedStrings.map {
            createStringPlaceholdersMap(it)
        }

        val errors = validator.validatePlaceholders(mainFilePlaceholders, translatedFilesPlaceholders)
        if (errors.isNotEmpty()) {
            val errorMessage = errors.fold("", { acc, error -> acc + error.message })
            throw GradleScriptException(errorMessage, Throwable(errorMessage))
        }
    }

    private fun createStringPlaceholdersMap(file: File): PlaceholdersForFile {
        val parsedFile: Node = XmlParser().parse(file)
        return PlaceholdersForFile(
            validator.extractPlaceholdersFromXml(parsedFile),
            file.absolutePath
        )
    }
}

data class PlaceholdersForFile(val placeholders: Map<String, List<String>>, val filePath: String)