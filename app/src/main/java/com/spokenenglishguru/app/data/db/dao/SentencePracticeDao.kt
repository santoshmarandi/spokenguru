package com.spokenenglishguru.app.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.spokenenglishguru.app.data.db.entity.SentencePracticeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SentencePracticeDao {
    @Query("SELECT * FROM sentence_practices WHERE lesson_id = :lessonId ORDER BY id ASC")
    fun getPracticesForLesson(lessonId: Int): Flow<List<SentencePracticeEntity>>

    @Query("SELECT * FROM sentence_practices WHERE is_favorite = 1 ORDER BY id DESC")
    fun getFavoritePractices(): Flow<List<SentencePracticeEntity>>

    @Query("UPDATE sentence_practices SET is_favorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFav: Int)

    @Query("UPDATE sentence_practices SET mastery_score = :score WHERE id = :id")
    suspend fun updateMastery(id: Int, score: Int)

    @Query("SELECT * FROM sentence_practices ORDER BY RANDOM() LIMIT :limit")
    fun getRandomPracticeBatch(limit: Int = 20): Flow<List<SentencePracticeEntity>>

    @Query("SELECT COUNT(*) FROM sentence_practices")
    fun getTotalSentenceCount(): Flow<Int>
}
