package com.spokenguru.app.ui.screens

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class AnswerState {
    IDLE,
    CORRECT,
    INCORRECT
}

private val CONTRACTIONS = mapOf(
    "i'm" to "i am",
    "you're" to "you are",
    "we're" to "we are",
    "they're" to "they are",
    "he's" to "he is",
    "she's" to "she is",
    "it's" to "it is",
    "that's" to "that is",
    "what's" to "what is",
    "who's" to "who is",
    "where's" to "where is",
    "isn't" to "is not",
    "aren't" to "are not",
    "wasn't" to "was not",
    "weren't" to "were not",
    "don't" to "do not",
    "doesn't" to "does not",
    "didn't" to "did not",
    "haven't" to "have not",
    "hasn't" to "has not",
    "hadn't" to "had not",
    "won't" to "will not",
    "wouldn't" to "would not",
    "can't" to "cannot",
    "couldn't" to "could not",
    "shouldn't" to "should not",
    "mustn't" to "must not",
    "i've" to "i have",
    "you've" to "you have",
    "we've" to "we have",
    "they've" to "they have",
    "i'll" to "i will",
    "you'll" to "you will",
    "he'll" to "he will",
    "she'll" to "she will",
    "we'll" to "we will",
    "they'll" to "they will",
    "let's" to "let us"
)

fun expandContractions(text: String): String {
    return text.split("\\s+".toRegex()).joinToString(" ") { token ->
        CONTRACTIONS[token] ?: token
    }
}

fun cleanSentence(str: String): String {
    return str.lowercase()
        .replace("’", "'")
        .replace(Regex("[^a-z0-9' ]"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
}

fun cleanWordPunctuation(word: String): String {
    return word.replace(Regex("^[.,!?;:\"()]+|[.,!?;:\"()]+$"), "")
}

/**
 * Checks user translation against primary and alternate answers, supporting punctuation differences
 * and equivalent English contractions (e.g. "I'm" == "I am", "isn't" == "is not").
 */
fun checkAnswer(
    userSentence: String,
    primary: String,
    alternatesJson: String,
    gson: Gson
): Boolean {
    val cleanUser = cleanSentence(userSentence)
    val expandedUser = expandContractions(cleanUser)
    val cleanPrimary = cleanSentence(primary)
    val expandedPrimary = expandContractions(cleanPrimary)

    if (cleanUser == cleanPrimary || expandedUser == expandedPrimary) return true

    val listType = object : TypeToken<List<String>>() {}.type
    val alts: List<String> = try {
        gson.fromJson(alternatesJson, listType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    return alts.any { alt ->
        val cleanAlt = cleanSentence(alt)
        cleanAlt == cleanUser || expandContractions(cleanAlt) == expandedUser
    }
}

/**
 * Extracts clean English text for TTS, stripping Hindi script and Word Match formatting.
 */
fun extractSpeakableEnglish(text: String): String {
    return text
        .replace("↔", ", ")
        .replace(Regex("\\([^)]*[\\u0900-\\u097F][^)]*\\)"), "")
        .replace(Regex("[\\u0900-\\u097F]+"), "")
        .replace(Regex("\\s+"), " ")
        .trim()
}

private val KOTLIN_CONFUSABLES = mapOf(
    "is" to listOf("are", "am", "was"),
    "are" to listOf("is", "were", "am"),
    "am" to listOf("is", "are", "was"),
    "was" to listOf("were", "is", "had"),
    "were" to listOf("was", "are", "had"),
    "do" to listOf("does", "did"),
    "does" to listOf("do", "did"),
    "did" to listOf("do", "does", "was"),
    "has" to listOf("have", "had"),
    "have" to listOf("has", "had"),
    "had" to listOf("have", "has", "did"),
    "will" to listOf("would", "shall"),
    "would" to listOf("will", "could"),
    "can" to listOf("could", "may"),
    "could" to listOf("can", "would"),
    "this" to listOf("that", "these"),
    "that" to listOf("this", "those"),
    "these" to listOf("those", "this"),
    "those" to listOf("these", "that"),
    "in" to listOf("into", "on", "at"),
    "on" to listOf("at", "in"),
    "from" to listOf("by", "since", "to"),
    "for" to listOf("since", "from"),
    "since" to listOf("for", "from"),
    "by" to listOf("with", "from"),
    "he" to listOf("him", "his"),
    "him" to listOf("he", "his"),
    "she" to listOf("her", "hers"),
    "go" to listOf("went", "goes"),
    "went" to listOf("go", "gone")
)

/**
 * Selects up to [targetCount] smart, non-duplicate distractors from [distractorsJson]
 * (with contextual grammar fallbacks if missing), ensuring none match answer/alternate words.
 */
fun selectSmartDistractors(
    primaryAnswer: String,
    alternateAnswersJson: String,
    distractorsJson: String,
    targetCount: Int,
    gson: Gson
): List<String> {
    if (targetCount <= 0) return emptyList()

    val listType = object : TypeToken<List<String>>() {}.type
    val rawDistractors: List<String> = try {
        gson.fromJson(distractorsJson, listType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    val alts: List<String> = try {
        gson.fromJson(alternateAnswersJson, listType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    val rawTokens = primaryAnswer.split("\\s+".toRegex())
        .map { cleanWordPunctuation(it) }
        .filter { it.isNotBlank() }

    val forbidden = mutableSetOf<String>()
    rawTokens.forEach { forbidden.add(cleanSentence(it)) }
    alts.forEach { alt ->
        alt.split("\\s+".toRegex()).forEach { t ->
            val key = cleanSentence(t)
            if (key.isNotBlank()) forbidden.add(key)
        }
    }

    val validSentences = mutableSetOf(expandContractions(cleanSentence(primaryAnswer)))
    alts.forEach { validSentences.add(expandContractions(cleanSentence(it))) }

    fun isSafeDistractor(candidate: String): Boolean {
        val ck = cleanSentence(candidate)
        if (ck.isBlank() || ck in forbidden) return false
        for (idx in rawTokens.indices) {
            val testTokens = rawTokens.toMutableList().also { it[idx] = candidate }
            val testNorm = expandContractions(cleanSentence(testTokens.joinToString(" ")))
            if (testNorm in validSentences) return false
        }
        return true
    }

    val selected = mutableListOf<String>()
    val seenKeys = mutableSetOf<String>()

    for (cand in rawDistractors) {
        val ck = cleanSentence(cand)
        if (ck !in seenKeys && isSafeDistractor(cand)) {
            selected.add(cand)
            seenKeys.add(ck)
        }
        if (selected.size >= targetCount) return selected
    }

    // On-device grammar confusion fallback if custom/imported lesson has empty distractors
    for ((idx, tok) in rawTokens.withIndex()) {
        val key = cleanSentence(tok)
        val cands = KOTLIN_CONFUSABLES[key].orEmpty()
        for (cand in cands) {
            val surface = if (idx == 0 && tok.firstOrNull()?.isUpperCase() == true) {
                cand.replaceFirstChar { it.uppercaseChar() }
            } else {
                cand
            }
            val ck = cleanSentence(surface)
            if (ck !in seenKeys && isSafeDistractor(surface)) {
                selected.add(surface)
                seenKeys.add(ck)
            }
            if (selected.size >= targetCount) return selected
        }
    }

    return selected
}

/**
 * Shuffles bank items while avoiding keeping the correct words in exact answer sequence.
 */
fun <T> shuffleAvoidOrder(items: List<T>, isOriginalOrder: (List<T>) -> Boolean): List<T> {
    if (items.size <= 1) return items
    var attempt = items.shuffled()
    var tries = 0
    while (isOriginalOrder(attempt) && tries < 6) {
        attempt = items.shuffled()
        tries++
    }
    return attempt
}
