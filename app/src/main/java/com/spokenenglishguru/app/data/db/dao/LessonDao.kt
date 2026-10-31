package com.spokenenglishguru.app.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.spokenenglishguru.app.data.db.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY id ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE category = :category ORDER BY id ASC")
    fun getLessonsByCategory(category: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
    fun getLessonById(id: Int): Flow<LessonEntity?>

    @Query("SELECT * FROM lessons WHERE title LIKE '%' || :query || '%' ORDER BY id ASC")
    fun searchLessons(query: String): Flow<List<LessonEntity>>

    @Query("SELECT COUNT(*) FROM lessons")
    fun getLessonCount(): Flow<Int>
}
