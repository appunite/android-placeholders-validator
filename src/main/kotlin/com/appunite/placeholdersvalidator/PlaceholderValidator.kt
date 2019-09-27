package com.appunite.placeholdersvalidator

import groovy.util.Node

class PlaceholdersValidator {

    fun validatePlaceholders(mainFilePlaceholders: PlaceholdersForFile,
                             translatedFilesPlaceholders: List<PlaceholdersForFile>): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        translatedFilesPlaceholders.forEach { placeholdersForFile ->
            mainFilePlaceholders.placeholders.forEach { (stringKey, referencePlaceholders) ->
                if (placeholdersForFile.placeholders.containsKey(stringKey)) { // We don't want to validate not translated strings
                    val filePlaceholders: List<String> = placeholdersForFile.placeholders[stringKey].orEmpty()

                    if (referencePlaceholders != filePlaceholders) {
                        errors.add(
                            ValidationError(
                                placeholders = filePlaceholders,
                                shouldBePlaceholders = referencePlaceholders,
                                affectedFilePath = placeholdersForFile.filePath,
                                affectedStringKey = stringKey
                            )
                        )
                    }
                }
            }
        }

        return errors.toList()
    }

    fun extractPlaceholdersFromXml(parsedXml: Node): Map<String, List<String>> {
        val stringKeyToPlaceholders = mutableMapOf<String, List<String>>()

        parsedXml.children().filter { it is Node }.forEach {
            val node = it as Node
            val text: String = node.value().toString()
            val placeholders: List<String> = "(%[0-9]+\\$[sd])|(\\$[sd])".toRegex()
                .findAll(text)
                .toList()
                .map { result -> result.value }

            stringKeyToPlaceholders[it.attribute("name").toString()] = placeholders
        }

        return stringKeyToPlaceholders
    }
}

data class ValidationError(val placeholders: List<String>,
                           val shouldBePlaceholders: List<String>,
                           val affectedFilePath: String,
                           val affectedStringKey: String) {

    val message = "Affected file: $affectedFilePath Affected key: $affectedStringKey. " +
            "Should be: $shouldBePlaceholders but is: $placeholders \n"
}