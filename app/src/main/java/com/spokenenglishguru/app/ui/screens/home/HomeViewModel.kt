package com.spokenenglishguru.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spokenenglishguru.app.data.repository.LessonRepository
import com.spokenenglishguru.app.data.repository.PracticeRepository
import com.spokenenglishguru.app.data.repository.QuizRepository
import com.spokenenglishguru.app.data.repository.VocabRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeUiState(
    val totalLessons: Int = 40,
    val completedLessons: Int = 0,
    val totalSentences: Int = 5580,
    val totalMcqs: Int = 1234,
    val totalVocab: Int = 746,
    val streakDays: Int = 3
)

class HomeViewModel(
    private val lessonRepository: LessonRepository,
    private val practiceRepository: PracticeRepository,
    private val quizRepository: QuizRepository,
    private val vocabRepository: VocabRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                combine(
                    lessonRepository.getCompletedCount(),
                    practiceRepository.getTotalSentenceCount(),
                    quizRepository.getTotalMcqCount(),
                    vocabRepository.getTotalVocabCount()
                ) { completed, sentences, mcqs, vocab ->
                    HomeUiState(
                        totalLessons = 40,
                        completedLessons = completed ?: 0,
                        totalSentences = sentences,
                        totalMcqs = mcqs,
                        totalVocab = vocab,
                        streakDays = (completed ?: 0) + 1
                    )
                }.catch { e ->
                    Log.e("HomeViewModel", "Error combining home metrics", e)
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to initialize HomeViewModel", e)
            }
        }
    }
}
