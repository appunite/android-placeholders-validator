package com.appunite.placeholdersvalidator

import groovy.util.Node
import groovy.util.XmlParser
import org.junit.Test
import java.io.StringReader
import kotlin.test.assertEquals

class PlaceholdersValidatorTest {

    private val d = "$"

    private lateinit var validator: PlaceholdersValidator

    private fun create() {
        validator = PlaceholdersValidator()
    }

    @Test
    fun `when given xml, then placeholders correctly extracted`() {
        create()
        val parsedXml: Node = XmlParser().parse(StringReader(xmlWithPlaceholders))

        val result: Map<String, List<String>> = validator.extractPlaceholdersFromXml(parsedXml)

        val expected = mutableMapOf<String, List<String>>()
        expected["1"] = listOf("%1${d}s")
        expected["2"] = listOf("%1${d}s", "%1${d}d")
        expected["3"] = listOf("%1${d}d")
        expected["4"] = listOf("${d}s")
        expected["5"] = listOf("${d}d")
        expected["6"] = listOf("${d}s")
        expected["7"] = listOf("${d}d")
        expected["8"] = listOf("${d}d", "${d}s", "%1${d}s", "%2${d}d")
        expected["9"] = listOf()
        expected["10"] = listOf()

        assertEquals(expected, result)
    }

    @Test
    fun `when placeholders are different than main file placeholders, then show error`() {
        create()
        val mainFilePlaceholders = extractPlaceholdersFrom(xmlWithPlaceholders)
        val wrongPlaceholders = extractPlaceholdersFrom(xmlWithWrongPlaceholders)
        val secondWrongPlaceholders = extractPlaceholdersFrom(xml2WithWrongPlaceholders)

        val errors: List<ValidationError> = validator.validatePlaceholders(
            mainFilePlaceholders = PlaceholdersForFile(mainFilePlaceholders, "en"),
            translatedFilesPlaceholders = listOf(
                PlaceholdersForFile(wrongPlaceholders, "it"),
                PlaceholdersForFile(secondWrongPlaceholders, "de")
            )
        )

        assertEquals(3, errors.size)
        errors[0].assertPlaceholderError("1", listOf("${d}s"), listOf("%1${d}s"), "it")
        errors[1].assertPlaceholderError("4", listOf(), listOf("${d}s"), "it")
        errors[2].assertPlaceholderError(
            "8",
            listOf("${d}s", "${d}d"),
            listOf("${d}d", "${d}s", "%1${d}s", "%2${d}d"),
            "de"
        )
    }

    private fun ValidationError.assertPlaceholderError(
        key: String,
        placeholders: List<String>,
        shouldBePlaceholders: List<String>,
        file: String
    ) {
        assertEquals(this.affectedStringKey, key)
        assertEquals(this.placeholders, placeholders)
        assertEquals(this.shouldBePlaceholders, shouldBePlaceholders)
        assertEquals(this.affectedFilePath, file)
    }

    private fun extractPlaceholdersFrom(xml: String): Map<String, List<String>> {
        val parsedXml = XmlParser().parse(StringReader(xml))
        return validator.extractPlaceholdersFromXml(parsedXml)
    }

    private val xmlWithPlaceholders = """<?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="1">"Percent string %1${d}s"</string>
        <string name="2">"Percent String and Percent decimal %1${d}s %1${d}ds"</string>
        <string name="3">"Decimal and redundant percent: %1%1${d}d"</string>
        <string name="4">"String: ${d}s"</string>
        <string name="5">"Decimal: ${d}d"</string>
        <string name="6">"String that could be decimal: ${d}sd"</string>
        <string name="7">"Decimal that could be string: ${d}ds"</string>
        <string name="8">"All together: ${d}d ${d}s %1${d}s %2${d}d"</string>
        <string name="9">"All Wrong: ${d}a %1s %2d %1$d %$ %1${d}${d}"</string>
        <string name="10">"Text"</string>
    </resources>
    """

    private val xmlWithWrongPlaceholders = """<?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="1">"Percent string %${d}s"</string>
        <string name="4">"String: ${d}"</string>
        <string name="5">"Decimal: ${d}d"</string>
    </resources>
    """

    private val xml2WithWrongPlaceholders = """<?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="8">"All together: d s 1${d}s %${d}d"</string>
    </resources>
    """
}