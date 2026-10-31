package com.spokenenglishguru.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.spokenenglishguru.app.data.db.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_progress WHERE `key` = :key LIMIT 1")
    fun getProgress(key: String): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserProgressEntity)

    @Query("SELECT SUM(value_int) FROM user_progress WHERE `key` LIKE 'lesson_completed_%'")
    fun getCompletedLessonCount(): Flow<Int?>
}
