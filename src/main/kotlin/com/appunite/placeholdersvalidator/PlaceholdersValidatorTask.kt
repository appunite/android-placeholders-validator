package com.appunite.placeholdersvalidator

import groovy.util.Node
import groovy.util.XmlParser
import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.file.FileTree
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class PlaceholdersValidatorTask : DefaultTask() {

    private val validator = PlaceholdersValidator()

    @get:Input
    abstract val resourcesDir: Property<FileTree>
    @get:Input
    abstract val ignorePlurals: Property<Boolean>

    init {
        description = "Validates placeholders from translated strings.xml files"
    }

    @TaskAction
    fun validateStringsPlaceholders() {
        val resourcesDir = resourcesDir.get()
        val ignorePlurals = ignorePlurals.get()

        val isStringXmlInValuesFolder = { path : String ->
            path.endsWith("/values/strings.xml") || path.endsWith("\\values\\strings.xml")
        }

        val mainStringsFile: File = resourcesDir.find {
            isStringXmlInValuesFolder(it.absolutePath)
        }!!

        val translatedStrings: List<File> = resourcesDir.filter {
            it.name == "strings.xml" && !isStringXmlInValuesFolder(it.absolutePath)
        }.files.toList()

        val mainFilePlaceholders: PlaceholdersForFile = createStringPlaceholdersMap(
            mainStringsFile, ignorePlurals
        )
        val translatedFilesPlaceholders: List<PlaceholdersForFile> = translatedStrings.map {
            createStringPlaceholdersMap(it, ignorePlurals)
        }

        val errors = validator.validatePlaceholders(
            mainFilePlaceholders,
            translatedFilesPlaceholders,
        )

        if (errors.isNotEmpty()) {
            val errorMessage = errors.fold("", { acc, error -> acc + error.message })
            throw GradleScriptException(errorMessage, Throwable(errorMessage))
        }
    }

    private fun createStringPlaceholdersMap(
        file: File,
        ignorePlurals: Boolean
    ): PlaceholdersForFile {
        val parsedFile: Node = XmlParser().parse(file)
        return PlaceholdersForFile(
            validator.extractPlaceholdersFromXml(parsedFile, ignorePlurals),
            file.absolutePath
        )
    }
}

data class PlaceholdersForFile(val placeholders: Map<String, List<String>>, val filePath: String)
