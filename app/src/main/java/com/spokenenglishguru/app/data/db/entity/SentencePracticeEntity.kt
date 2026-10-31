package com.spokenenglishguru.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sentence_practices")
data class SentencePracticeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "lesson_id")
    val lessonId: Int,
    val type: String, // TRANSLATION, ACTIVE_PASSIVE, DIRECT_INDIRECT, CORRECTION, HOMONYM
    @ColumnInfo(name = "prompt_primary")
    val promptPrimary: String, // Hindi sentence or original sentence
    @ColumnInfo(name = "prompt_secondary")
    val promptSecondary: String?, // Romanized or instruction
    @ColumnInfo(name = "english_answer")
    val englishAnswer: String,
    val notes: String? = null,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Int = 0,
    @ColumnInfo(name = "mastery_score")
    val masteryScore: Int = 0
)
