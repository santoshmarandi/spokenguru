package com.spokenguru.app.ui.screens

data class MistakeAnalysis(
    val explanation: String,
    val userMistakeWord: String? = null,
    val expectedWord: String? = null
)

object MistakeAnalyzer {

    fun analyze(
        userAnswer: String,
        primaryAnswer: String,
        grammarRule: String
    ): MistakeAnalysis {
        val cleanUser = cleanSentence(userAnswer)
        val cleanPrimary = cleanSentence(primaryAnswer)

        if (cleanUser.isBlank()) {
            return MistakeAnalysis(
                explanation = "उत्तर खाली है। कृपया नीचे दिए गए शब्दों से वाक्य बनाएं।"
            )
        }

        val userTokens = cleanUser.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val solutionTokens = cleanPrimary.split("\\s+".toRegex()).filter { it.isNotBlank() }

        // 1. High-frequency grammatical substitution rules
        val substitutionRules = listOf(
            Pair("these", "this") to "'these' (बहुवचन) के बजाय 'this' (एकवचन) होना चाहिए।",
            Pair("this", "these") to "'this' (एकवचन) के बजाय 'these' (बहुवचन) होना चाहिए।",
            Pair("those", "that") to "'those' (बहुवचन) के बजाय 'that' (एकवचन) होना चाहिए।",
            Pair("that", "those") to "'that' (एकवचन) के बजाय 'those' (बहुवचन) होना चाहिए।",
            Pair("are", "is") to "एकवचन (Singular) कर्ता के साथ 'is' आएगा, 'are' नहीं।",
            Pair("is", "are") to "बहुवचन (Plural) कर्ता के साथ 'are' आएगा, 'is' नहीं।",
            Pair("was", "were") to "बहुवचन (Plural) कर्ता के साथ 'were' आएगा, 'was' नहीं।",
            Pair("were", "was") to "एकवचन (Singular) कर्ता के साथ 'was' आएगा, 'were' नहीं।",
            Pair("has", "have") to "यहाँ 'have' का प्रयोग होना चाहिए था, 'has' नहीं।",
            Pair("have", "has") to "He/She/Singular कर्ता के साथ 'has' का प्रयोग होता है, 'have' नहीं।",
            Pair("do", "does") to "He/She/Singular के साथ 'does' का प्रयोग होता है, 'do' नहीं।",
            Pair("does", "do") to "I/You/Plural कर्ता के साथ 'do' का प्रयोग होता है, 'does' नहीं।",
            Pair("to", "past") to "बीत चुके मिनटों (बजकर मिनट) के लिए 'past' का प्रयोग करें, 'to' नहीं।",
            Pair("past", "to") to "बजने में बाकी मिनटों के लिए 'to' का प्रयोग करें, 'past' नहीं।"
        )

        for ((pair, msg) in substitutionRules) {
            val (userWord, rightWord) = pair
            if (userTokens.contains(userWord) && solutionTokens.contains(rightWord) && !solutionTokens.contains(userWord)) {
                return MistakeAnalysis(
                    explanation = "गलती: $msg",
                    userMistakeWord = userWord,
                    expectedWord = rightWord
                )
            }
        }

        // 2. Identify word differences (words in user answer not in solution)
        val extraWords = userTokens.filter { !solutionTokens.contains(it) }
        val missingWords = solutionTokens.filter { !userTokens.contains(it) }

        if (extraWords.isNotEmpty() && missingWords.isNotEmpty()) {
            val firstWrong = extraWords.first()
            val firstRight = missingWords.first()
            return MistakeAnalysis(
                explanation = "गलती: आपने '$firstWrong' चुना, जबकि यहाँ '$firstRight' होना चाहिए।",
                userMistakeWord = firstWrong,
                expectedWord = firstRight
            )
        }

        if (missingWords.isNotEmpty()) {
            val missingJoined = missingWords.joinToString(", ") { "'$it'" }
            return MistakeAnalysis(
                explanation = "छूटा हुआ शब्द: आपके उत्तर में $missingJoined छूट गया है।"
            )
        }

        if (extraWords.isNotEmpty()) {
            val extraJoined = extraWords.joinToString(", ") { "'$it'" }
            return MistakeAnalysis(
                explanation = "अतिरिक्त शब्द: $extraJoined इस वाक्य में नहीं होना चाहिए।"
            )
        }

        // 3. Word order error (all words are present, but sequence is wrong)
        if (userTokens.size == solutionTokens.size && userTokens.sorted() == solutionTokens.sorted()) {
            return MistakeAnalysis(
                explanation = "शब्दों का क्रम गलत है: सभी शब्द सही चुने हैं, लेकिन वाक्य में शब्दों का सही क्रम (Word Order) देखें।"
            )
        }

        return MistakeAnalysis(
            explanation = "दिए गए शब्दों का क्रम या चुनाव सही नहीं है। सही वाक्य और व्याकरण नियम को देखें।"
        )
    }
}
