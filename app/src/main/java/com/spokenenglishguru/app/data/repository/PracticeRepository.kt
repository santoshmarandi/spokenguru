package com.spokenenglishguru.app.data.repository

import com.spokenenglishguru.app.data.db.dao.SentencePracticeDao
import com.spokenenglishguru.app.data.db.entity.SentencePracticeEntity
import kotlinx.coroutines.flow.Flow

class PracticeRepository(
    private val sentencePracticeDao: SentencePracticeDao
) {
    fun getPracticesForLesson(lessonId: Int): Flow<List<SentencePracticeEntity>> =
        sentencePracticeDao.getPracticesForLesson(lessonId)

    fun getFavoritePractices(): Flow<List<SentencePracticeEntity>> =
        sentencePracticeDao.getFavoritePractices()

    fun getRandomBatch(limit: Int = 20): Flow<List<SentencePracticeEntity>> =
        sentencePracticeDao.getRandomPracticeBatch(limit)

    fun getTotalSentenceCount(): Flow<Int> =
        sentencePracticeDao.getTotalSentenceCount()

    suspend fun toggleFavorite(id: Int, currentFav: Int) {
        val nextFav = if (currentFav == 1) 0 else 1
        sentencePracticeDao.updateFavorite(id, nextFav)
    }

    suspend fun updateMastery(id: Int, score: Int) {
        sentencePracticeDao.updateMastery(id, score)
    }
}
