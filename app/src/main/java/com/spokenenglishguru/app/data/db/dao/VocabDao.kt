package com.spokenenglishguru.app.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.spokenenglishguru.app.data.db.entity.VocabEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabDao {
    @Query("SELECT * FROM vocab_items WHERE category = :category ORDER BY word ASC")
    fun getVocabByCategory(category: String): Flow<List<VocabEntity>>

    @Query("SELECT * FROM vocab_items WHERE word LIKE '%' || :query || '%' OR hindi_meaning LIKE '%' || :query || '%' ORDER BY word ASC")
    fun searchVocab(query: String): Flow<List<VocabEntity>>

    @Query("SELECT * FROM vocab_items WHERE is_favorite = 1 ORDER BY id DESC")
    fun getFavoriteVocab(): Flow<List<VocabEntity>>

    @Query("UPDATE vocab_items SET is_favorite = :isFav WHERE id = :id")
    suspend fun toggleVocabFavorite(id: Int, isFav: Int)

    @Query("SELECT COUNT(*) FROM vocab_items")
    fun getTotalVocabCount(): Flow<Int>
}
