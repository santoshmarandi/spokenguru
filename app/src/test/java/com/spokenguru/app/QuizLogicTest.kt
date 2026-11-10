package com.spokenguru.app

import com.google.gson.Gson
import com.spokenguru.app.ui.screens.MistakeAnalyzer
import com.spokenguru.app.ui.screens.checkAnswer
import com.spokenguru.app.ui.screens.extractSpeakableEnglish
import com.spokenguru.app.ui.screens.selectSmartDistractors
import com.spokenguru.app.ui.screens.shuffleAvoidOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizLogicTest {

    private val gson = Gson()

    @Test
    fun checkAnswer_matchesExactAndContractions() {
        assertTrue(checkAnswer("I am coming from Delhi.", "I am coming from Delhi", "[]", gson))
        assertTrue(checkAnswer("i'm coming from delhi", "I am coming from Delhi.", "[]", gson))
        assertTrue(checkAnswer("This isn't a boy", "This is not a boy.", "[]", gson))
        assertTrue(checkAnswer("This is not a boy!", "This isn't a boy", "[]", gson))
        assertTrue(checkAnswer("We cannot go", "We can't go.", "[]", gson))
        assertFalse(checkAnswer("I am coming to Delhi", "I am coming from Delhi.", "[]", gson))
    }

    @Test
    fun checkAnswer_matchesAlternates() {
        val alts = gson.toJson(listOf("This time is not good.", "It is not a good time."))
        assertTrue(checkAnswer("this time is not good", "This isn't a good time.", alts, gson))
    }

    @Test
    fun extractSpeakableEnglish_stripsHindiAndArrows() {
        val raw = "Admit (स्वीकार करना) ↔ Admitted, Aid (सहायता देना) ↔ Aided"
        val cleaned = extractSpeakableEnglish(raw)
        assertEquals("Admit , Admitted, Aid , Aided", cleaned)
    }

    @Test
    fun mistakeAnalyzer_detectsWordMatchPairMismatch() {
        val primaryPairs = "Admit (स्वीकार करना) ↔ Admitted, Aid (सहायता देना) ↔ Aided, Agree ↔ Agreed"
        val analysis = MistakeAnalyzer.analyze("Admit (स्वीकार करना) ↔ Agreed", primaryPairs, "")
        assertTrue(analysis.explanation.contains("Admitted"))
        assertTrue(analysis.explanation.contains("Agreed"))
        assertEquals("Admitted", analysis.expectedWord)
        assertEquals("Agreed", analysis.userMistakeWord)
    }

    @Test
    fun mistakeAnalyzer_detectsSingleWordAndSubstitutionErrors() {
        val singleWord = MistakeAnalyzer.analyze("under", "from", "")
        assertEquals("from", singleWord.expectedWord)
        assertEquals("under", singleWord.userMistakeWord)

        val demonstratives = MistakeAnalyzer.analyze("These is a girl", "This is a girl", "")
        assertTrue(demonstratives.explanation.contains("this"))

        val wordOrder = MistakeAnalyzer.analyze("I coming am Delhi from", "I am coming from Delhi", "")
        assertTrue(wordOrder.explanation.contains("क्रम"))
    }

    @Test
    fun selectSmartDistractors_excludesAnswerAndAlternateWords_andProvidesFallback() {
        val alts = gson.toJson(listOf("That isn't a girl."))
        val rawDistractors = gson.toJson(listOf("isn't", "boy", "That", "are", "these", "girls"))
        val picked = selectSmartDistractors(
            primaryAnswer = "This is not a girl.",
            alternateAnswersJson = alts,
            distractorsJson = rawDistractors,
            targetCount = 3,
            gson = gson
        )
        assertEquals(3, picked.size)
        // "isn't" and "That" appear in alternates, so they must not leak into distractors
        assertFalse(picked.any { it.equals("isn't", ignoreCase = true) })
        assertFalse(picked.any { it.equals("That", ignoreCase = true) })
        assertTrue(picked.containsAll(listOf("boy", "are", "these")))

        // Fallback when distractorsJson is empty for custom/imported items
        val fallbackPicked = selectSmartDistractors(
            primaryAnswer = "Who is this boy?",
            alternateAnswersJson = "[]",
            distractorsJson = "[]",
            targetCount = 3,
            gson = gson
        )
        assertEquals(3, fallbackPicked.size)
        assertFalse(fallbackPicked.any { it.equals("is", ignoreCase = true) || it.equals("this", ignoreCase = true) })
    }

    @Test
    fun shuffleAvoidOrder_breaksExactMatchForShortLists() {
        val original = listOf("I", "go", "home")
        val shuffled = shuffleAvoidOrder(original) { it == original }
        assertEquals(original.toSet(), shuffled.toSet())
        assertFalse(shuffled == original)
    }
}
