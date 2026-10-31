package com.spokenenglishguru.app.ui.screens.lessons

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spokenenglishguru.app.data.db.entity.LessonEntity
import com.spokenenglishguru.app.data.db.entity.SentencePracticeEntity
import com.spokenenglishguru.app.data.repository.LessonRepository
import com.spokenenglishguru.app.data.repository.PracticeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LessonsUiState(
    val lessons: List<LessonEntity> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class LessonsViewModel(
    private val lessonRepository: LessonRepository,
    private val practiceRepository: PracticeRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<LessonsUiState> = combine(
        lessonRepository.getAllLessons().catch { e -> Log.e("LessonsViewModel", "Error fetching lessons", e) },
        _selectedCategory,
        _searchQuery
    ) { allLessons, category, query ->
        val filtered = allLessons.filter { lesson ->
            val matchesCategory = if (category == "All") true else lesson.category == category
            val matchesQuery = if (query.isBlank()) true else lesson.title.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
        LessonsUiState(
            lessons = filtered,
            selectedCategory = category,
            searchQuery = query
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LessonsUiState())

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getLessonById(id: Int) = lessonRepository.getLessonById(id)

    fun getPracticesForLesson(lessonId: Int) = practiceRepository.getPracticesForLesson(lessonId)

    fun toggleFavorite(id: Int, currentFav: Int) {
        viewModelScope.launch {
            practiceRepository.toggleFavorite(id, currentFav)
        }
    }

    fun toggleLessonCompleted(lessonId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            lessonRepository.markLessonCompleted(lessonId, isCompleted)
        }
    }
}
