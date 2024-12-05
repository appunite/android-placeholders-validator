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
        expected["4"] = listOf("%s")
        expected["5"] = listOf("%d")
        expected["6"] = listOf("%s")
        expected["7"] = listOf("%d")
        expected["8"] = listOf("%d", "%s", "%1${d}s", "%2${d}d")
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
        errors[0].assertPlaceholderError(
            key = "1",
            placeholders = listOf("%s"),
            shouldBePlaceholders = listOf("%1${d}s"),
            file = "it"
        )
        errors[1].assertPlaceholderError(
            key = "4",
            placeholders = listOf(),
            shouldBePlaceholders = listOf("%s"),
            file = "it"
        )
        errors[2].assertPlaceholderError(
            key = "8",
            placeholders = listOf("%s"),
            shouldBePlaceholders = listOf("%d", "%s", "%1${d}s", "%2${d}d"),
            "de"
        )
    }

    private fun ValidationError.assertPlaceholderError(
        key: String,
        placeholders: List<String>,
        shouldBePlaceholders: List<String>,
        file: String
    ) {
        assertEquals(key, this.affectedStringKey, )
        assertEquals(placeholders, this.placeholders)
        assertEquals(shouldBePlaceholders, this.shouldBePlaceholders)
        assertEquals(file, this.affectedFilePath)
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
        <string name="4">"String: %s"</string>
        <string name="5">"Decimal: %d"</string>
        <string name="6">"String that could be decimal: %sd"</string>
        <string name="7">"Decimal that could be string: %ds"</string>
        <string name="8">"All together: %d %s %1${d}s %2${d}d"</string>
        <string name="9">"All Wrong: %a %1s %2d %1$d %$ %1${d}${d}"</string>
        <string name="10">"Text"</string>
    </resources>
    """

    private val xmlWithWrongPlaceholders = """<?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="1">"Percent string ${d}%s"</string>
        <string name="4">"String: %"</string>
        <string name="5">"Decimal: %d"</string>
    </resources>
    """

    private val xml2WithWrongPlaceholders = """<?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="8">"All together: d s 1%s %${d}d"</string>
    </resources>
    """
}
