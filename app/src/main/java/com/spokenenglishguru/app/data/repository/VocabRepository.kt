package com.spokenenglishguru.app.data.repository

import com.spokenenglishguru.app.data.db.dao.VocabDao
import com.spokenenglishguru.app.data.db.entity.VocabEntity
import kotlinx.coroutines.flow.Flow

class VocabRepository(
    private val vocabDao: VocabDao
) {
    fun getVocabByCategory(category: String): Flow<List<VocabEntity>> =
        vocabDao.getVocabByCategory(category)

    fun searchVocab(query: String): Flow<List<VocabEntity>> =
        vocabDao.searchVocab(query)

    fun getFavoriteVocab(): Flow<List<VocabEntity>> =
        vocabDao.getFavoriteVocab()

    fun getTotalVocabCount(): Flow<Int> =
        vocabDao.getTotalVocabCount()

    suspend fun toggleFavorite(id: Int, currentFav: Int) {
        val nextFav = if (currentFav == 1) 0 else 1
        vocabDao.toggleVocabFavorite(id, nextFav)
    }
}
