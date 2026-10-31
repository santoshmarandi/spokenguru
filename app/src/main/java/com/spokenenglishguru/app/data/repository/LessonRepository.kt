package com.spokenenglishguru.app.data.repository

import com.spokenenglishguru.app.data.db.dao.LessonDao
import com.spokenenglishguru.app.data.db.dao.ProgressDao
import com.spokenenglishguru.app.data.db.entity.LessonEntity
import com.spokenenglishguru.app.data.db.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

class LessonRepository(
    private val lessonDao: LessonDao,
    private val progressDao: ProgressDao
) {
    fun getAllLessons(): Flow<List<LessonEntity>> = lessonDao.getAllLessons()

    fun getLessonsByCategory(category: String): Flow<List<LessonEntity>> =
        lessonDao.getLessonsByCategory(category)

    fun getLessonById(id: Int): Flow<LessonEntity?> = lessonDao.getLessonById(id)

    fun searchLessons(query: String): Flow<List<LessonEntity>> = lessonDao.searchLessons(query)

    fun getLessonCount(): Flow<Int> = lessonDao.getLessonCount()

    fun getCompletedCount(): Flow<Int?> = progressDao.getCompletedLessonCount()

    suspend fun markLessonCompleted(lessonId: Int, isCompleted: Boolean) {
        progressDao.saveProgress(
            UserProgressEntity(
                key = "lesson_completed_$lessonId",
                valueInt = if (isCompleted) 1 else 0
            )
        )
    }

    fun isLessonCompleted(lessonId: Int): Flow<UserProgressEntity?> =
        progressDao.getProgress("lesson_completed_$lessonId")
}
