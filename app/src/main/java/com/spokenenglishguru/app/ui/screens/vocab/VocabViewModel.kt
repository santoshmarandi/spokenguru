package com.spokenenglishguru.app.ui.screens.vocab

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spokenenglishguru.app.data.db.entity.VocabEntity
import com.spokenenglishguru.app.data.repository.VocabRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class VocabViewModel(
    private val vocabRepository: VocabRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("VERB")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _itemsFlow = MutableStateFlow<List<VocabEntity>>(emptyList())
    val itemsFlow: StateFlow<List<VocabEntity>> = _itemsFlow.asStateFlow()

    init {
        loadCategory("VERB")
    }

    fun loadCategory(category: String) {
        _selectedCategory.value = category
        viewModelScope.launch {
            vocabRepository.getVocabByCategory(category)
                .catch { e -> Log.e("VocabViewModel", "Error loading category", e) }
                .collect { list ->
                    _itemsFlow.value = list
                }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                vocabRepository.getVocabByCategory(_selectedCategory.value)
                    .catch { e -> Log.e("VocabViewModel", "Error searching", e) }
                    .collect { list ->
                        _itemsFlow.value = list
                    }
            } else {
                vocabRepository.searchVocab(query)
                    .catch { e -> Log.e("VocabViewModel", "Error searching", e) }
                    .collect { list ->
                        _itemsFlow.value = list
                    }
            }
        }
    }

    fun toggleFavorite(id: Int, currentFav: Int) {
        viewModelScope.launch {
            vocabRepository.toggleFavorite(id, currentFav)
        }
    }
}
