package com.spokenenglishguru.app.data.repository

import com.spokenenglishguru.app.data.db.dao.McqQuestionDao
import com.spokenenglishguru.app.data.db.entity.McqQuestionEntity
import kotlinx.coroutines.flow.Flow

class QuizRepository(
    private val mcqQuestionDao: McqQuestionDao
) {
    fun getQuestionsByQuiz(quizNum: Int): Flow<List<McqQuestionEntity>> =
        mcqQuestionDao.getQuestionsByQuiz(quizNum)

    fun getAllQuizNumbers(): Flow<List<Int>> =
        mcqQuestionDao.getAllQuizNumbers()

    fun getMistakeQuestions(): Flow<List<McqQuestionEntity>> =
        mcqQuestionDao.getMistakeQuestions()

    fun getBookmarkedQuestions(): Flow<List<McqQuestionEntity>> =
        mcqQuestionDao.getBookmarkedQuestions()

    fun getTotalMcqCount(): Flow<Int> =
        mcqQuestionDao.getTotalMcqCount()

    suspend fun recordMistake(id: Int) {
        mcqQuestionDao.recordMistake(id)
    }

    suspend fun clearMistake(id: Int) {
        mcqQuestionDao.clearMistake(id)
    }

    suspend fun toggleBookmark(id: Int, currentBookmarked: Int) {
        val nextVal = if (currentBookmarked == 1) 0 else 1
        mcqQuestionDao.toggleBookmark(id, nextVal)
    }
}
