package com.spokenenglishguru.app.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.spokenenglishguru.app.data.db.entity.McqQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface McqQuestionDao {
    @Query("SELECT * FROM mcq_questions WHERE quiz_num = :quizNum ORDER BY question_num ASC")
    fun getQuestionsByQuiz(quizNum: Int): Flow<List<McqQuestionEntity>>

    @Query("SELECT DISTINCT quiz_num FROM mcq_questions ORDER BY quiz_num ASC")
    fun getAllQuizNumbers(): Flow<List<Int>>

    @Query("SELECT * FROM mcq_questions WHERE times_wrong > 0 ORDER BY times_wrong DESC")
    fun getMistakeQuestions(): Flow<List<McqQuestionEntity>>

    @Query("SELECT * FROM mcq_questions WHERE is_bookmarked = 1 ORDER BY id DESC")
    fun getBookmarkedQuestions(): Flow<List<McqQuestionEntity>>

    @Query("UPDATE mcq_questions SET times_wrong = times_wrong + 1 WHERE id = :id")
    suspend fun recordMistake(id: Int)

    @Query("UPDATE mcq_questions SET times_wrong = 0 WHERE id = :id")
    suspend fun clearMistake(id: Int)

    @Query("UPDATE mcq_questions SET is_bookmarked = :isBookmarked WHERE id = :id")
    suspend fun toggleBookmark(id: Int, isBookmarked: Int)

    @Query("SELECT COUNT(*) FROM mcq_questions")
    fun getTotalMcqCount(): Flow<Int>
}
