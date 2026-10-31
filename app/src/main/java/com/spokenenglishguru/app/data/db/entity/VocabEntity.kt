package com.spokenenglishguru.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocab_items")
data class VocabEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "lesson_id")
    val lessonId: Int,
    val category: String, // VERB, ADJECTIVE, ADVERB, ANTONYM, SYNONYM, HOMONYM
    val word: String,
    @ColumnInfo(name = "hindi_meaning")
    val hindiMeaning: String?,
    val romanized: String?,
    val form2: String?, // V2 or Comparative or Opposite
    val form3: String?, // V3 or Superlative or Synonym
    @ColumnInfo(name = "extra_info")
    val extraInfo: String?,
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Int = 0
)
