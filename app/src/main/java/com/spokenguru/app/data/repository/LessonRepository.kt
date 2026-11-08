package com.spokenguru.app.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spokenguru.app.data.AppDatabase
import com.spokenguru.app.data.model.LessonEntity
import com.spokenguru.app.data.model.QuestionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class SeedData(
    val version: Int,
    val lessons: List<SeedLesson>,
    val questions: List<SeedQuestion>? = null,
    val lesson1Questions: List<SeedQuestion>? = null
)

data class SeedLesson(
    val lessonNumber: Int,
    val title: String,
    val pageNumber: Int,
    val totalQuestions: Int,
    val isAvailable: Boolean
)

data class SeedQuestion(
    val questionNumber: Int,
    val lessonNumber: Int,
    val questionType: String? = null,
    val hindiText: String,
    val romanText: String,
    val primaryAnswer: String,
    val alternateAnswers: List<String>?,
    val distractors: List<String>?,
    val options: List<String>? = null,
    val matchPairs: Map<String, String>? = null,
    val hint: String?,
    val ruleTitle: String?,
    val grammarRule: String?,
    val tagKeyword: String?,
    val categoryTag: String?,
    val wordGlosses: Map<String, String>?
)

class LessonRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getInstance(context)
) {
    private val lessonDao = database.lessonDao()
    private val questionDao = database.questionDao()
    private val gson = Gson()

    val allLessons: Flow<List<LessonEntity>> = lessonDao.getAllLessons()
    val allMistakes: Flow<List<QuestionEntity>> = questionDao.getAllMistakes()
    val mistakesCount: Flow<Int> = questionDao.getMistakesCount()

    suspend fun ensureDataSeeded() = withContext(Dispatchers.IO) {
        val count = lessonDao.getLessonCount()
        val questionsCount = questionDao.getTotalQuestionsCount()
        if (count < 40 || questionsCount < 8200) {
            seedFromAssets()
        }
    }

    private suspend fun seedFromAssets() {
        try {
            val jsonString = context.assets.open("lessons_seed.json").bufferedReader().use { it.readText() }
            val seedData = gson.fromJson(jsonString, SeedData::class.java)

            val lessonsToInsert = seedData.lessons.map {
                LessonEntity(
                    lessonNumber = it.lessonNumber,
                    title = it.title,
                    pageNumber = it.pageNumber,
                    totalQuestions = it.totalQuestions,
                    completedQuestions = 0,
                    isAvailable = it.isAvailable
                )
            }
            lessonDao.insertLessons(lessonsToInsert)

            val allQuestions = seedData.questions ?: seedData.lesson1Questions ?: emptyList()
            val questionsToInsert = allQuestions.map {
                QuestionEntity(
                    lessonNumber = it.lessonNumber,
                    questionNumber = it.questionNumber,
                    questionType = it.questionType ?: "SENTENCE_BUILDER",
                    hindiText = it.hindiText,
                    romanText = it.romanText,
                    primaryAnswer = it.primaryAnswer,
                    alternateAnswersJson = gson.toJson(it.alternateAnswers ?: emptyList<String>()),
                    distractorsJson = gson.toJson(it.distractors ?: emptyList<String>()),
                    optionsJson = gson.toJson(it.options ?: it.distractors ?: emptyList<String>()),
                    matchPairsJson = gson.toJson(it.matchPairs ?: emptyMap<String, String>()),
                    hint = it.hint ?: "",
                    ruleTitle = it.ruleTitle ?: "Guru Grammar Rule",
                    grammarRule = it.grammarRule ?: "",
                    tagKeyword = it.tagKeyword ?: "Lesson ${it.lessonNumber}",
                    categoryTag = it.categoryTag ?: "Practice Drill",
                    wordGlossesJson = gson.toJson(it.wordGlosses ?: emptyMap<String, String>()),
                    isCompleted = false,
                    isMistake = false
                )
            }
            // Chunked insertion to safely stay within SQLite parameter limits
            questionsToInsert.chunked(300).forEach { chunk ->
                questionDao.insertQuestions(chunk)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getQuestionsForLesson(lessonNumber: Int): Flow<List<QuestionEntity>> {
        return questionDao.getQuestionsForLesson(lessonNumber)
    }

    suspend fun getQuestionsForLessonSync(lessonNumber: Int): List<QuestionEntity> = withContext(Dispatchers.IO) {
        questionDao.getQuestionsForLessonSync(lessonNumber)
    }

    suspend fun markQuestionCompleted(id: Long, lessonNumber: Int) = withContext(Dispatchers.IO) {
        questionDao.updateCompletedStatus(id, true)
        val completedCount = questionDao.getCompletedCount(lessonNumber)
        lessonDao.updateCompletedQuestions(lessonNumber, completedCount)
    }

    suspend fun markMistake(id: Long, isMistake: Boolean) = withContext(Dispatchers.IO) {
        questionDao.updateMistakeStatus(id, isMistake)
    }

    suspend fun resetLesson(lessonNumber: Int) = withContext(Dispatchers.IO) {
        questionDao.resetLessonProgress(lessonNumber)
        lessonDao.updateCompletedQuestions(lessonNumber, 0)
    }

    suspend fun importQuestionsJson(lessonNumber: Int, jsonContent: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val listType = object : TypeToken<List<SeedQuestion>>() {}.type
            val imported: List<SeedQuestion> = gson.fromJson(jsonContent, listType)
            if (imported.isEmpty()) {
                return@withContext Result.failure(Exception("JSON list is empty"))
            }

            questionDao.deleteQuestionsForLesson(lessonNumber)

            val entities = imported.mapIndexed { index, q ->
                QuestionEntity(
                    lessonNumber = lessonNumber,
                    questionNumber = if (q.questionNumber > 0) q.questionNumber else (index + 1),
                    hindiText = q.hindiText,
                    romanText = q.romanText,
                    primaryAnswer = q.primaryAnswer,
                    alternateAnswersJson = gson.toJson(q.alternateAnswers ?: emptyList<String>()),
                    distractorsJson = gson.toJson(q.distractors ?: emptyList<String>()),
                    hint = q.hint ?: "",
                    ruleTitle = q.ruleTitle ?: "Guru Grammar Rule",
                    grammarRule = q.grammarRule ?: "",
                    tagKeyword = q.tagKeyword ?: "",
                    categoryTag = q.categoryTag ?: "",
                    wordGlossesJson = gson.toJson(q.wordGlosses ?: emptyMap<String, String>()),
                    isCompleted = false,
                    isMistake = false
                )
            }
            questionDao.insertQuestions(entities)

            val existing = lessonDao.getLesson(lessonNumber)
            if (existing != null) {
                lessonDao.updateLesson(
                    existing.copy(
                        totalQuestions = entities.size,
                        completedQuestions = 0,
                        isAvailable = true
                    )
                )
            }

            Result.success(entities.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDbStats(): String = withContext(Dispatchers.IO) {
        val totalLessons = lessonDao.getLessonCount()
        val totalQuestions = questionDao.getTotalQuestionsCount()
        "Total Lessons: $totalLessons\nTotal Active Questions: $totalQuestions"
    }
}
