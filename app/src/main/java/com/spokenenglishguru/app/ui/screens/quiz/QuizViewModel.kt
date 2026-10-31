package com.spokenenglishguru.app.ui.screens.quiz

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spokenenglishguru.app.data.db.entity.McqQuestionEntity
import com.spokenenglishguru.app.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuizHubState(
    val quizNumbers: List<Int> = emptyList(),
    val totalMcqs: Int = 1234,
    val mistakeCount: Int = 0,
    val bookmarkCount: Int = 0
)

data class QuizPlayState(
    val quizNum: Int = 1,
    val questions: List<McqQuestionEntity> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: String? = null,
    val isSubmitted: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val isFinished: Boolean = false
)

class QuizViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _hubState = MutableStateFlow(QuizHubState())
    val hubState: StateFlow<QuizHubState> = _hubState.asStateFlow()

    private val _playState = MutableStateFlow(QuizPlayState())
    val playState: StateFlow<QuizPlayState> = _playState.asStateFlow()

    val mistakeQuestions: StateFlow<List<McqQuestionEntity>> = quizRepository.getMistakeQuestions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            quizRepository.getAllQuizNumbers()
                .catch { e -> Log.e("QuizViewModel", "Error loading quiz numbers", e) }
                .collect { numbers ->
                    _hubState.value = _hubState.value.copy(quizNumbers = numbers)
                }
        }
        viewModelScope.launch {
            quizRepository.getMistakeQuestions()
                .catch { e -> Log.e("QuizViewModel", "Error loading mistakes", e) }
                .collect { mistakes ->
                    _hubState.value = _hubState.value.copy(mistakeCount = mistakes.size)
                }
        }
        viewModelScope.launch {
            quizRepository.getBookmarkedQuestions()
                .catch { e -> Log.e("QuizViewModel", "Error loading bookmarks", e) }
                .collect { bms ->
                    _hubState.value = _hubState.value.copy(bookmarkCount = bms.size)
                }
        }
        viewModelScope.launch {
            quizRepository.getTotalMcqCount()
                .catch { e -> Log.e("QuizViewModel", "Error loading mcq count", e) }
                .collect { count ->
                    _hubState.value = _hubState.value.copy(totalMcqs = count)
                }
        }
    }

    fun startQuiz(quizNum: Int) {
        viewModelScope.launch {
            quizRepository.getQuestionsByQuiz(quizNum).collect { list ->
                _playState.value = QuizPlayState(
                    quizNum = quizNum,
                    questions = list,
                    currentIndex = 0,
                    selectedOption = null,
                    isSubmitted = false,
                    isCorrect = false,
                    score = 0,
                    isFinished = false
                )
            }
        }
    }

    fun startMistakeQuiz() {
        val mistakes = mistakeQuestions.value
        _playState.value = QuizPlayState(
            quizNum = 0,
            questions = mistakes,
            currentIndex = 0,
            selectedOption = null,
            isSubmitted = false,
            isCorrect = false,
            score = 0,
            isFinished = false
        )
    }

    fun selectOption(optionKey: String) {
        val curr = _playState.value
        if (curr.isSubmitted) return

        val currentQ = curr.questions[curr.currentIndex]
        val isCorrect = optionKey.equals(currentQ.correctOption, ignoreCase = true)

        viewModelScope.launch {
            if (!isCorrect) {
                quizRepository.recordMistake(currentQ.id)
            } else if (currentQ.timesWrong > 0) {
                quizRepository.clearMistake(currentQ.id)
            }
        }

        _playState.value = curr.copy(
            selectedOption = optionKey,
            isSubmitted = true,
            isCorrect = isCorrect,
            score = if (isCorrect) curr.score + 1 else curr.score
        )
    }

    fun nextQuestion() {
        val curr = _playState.value
        val nextIdx = curr.currentIndex + 1
        if (nextIdx < curr.questions.size) {
            _playState.value = curr.copy(
                currentIndex = nextIdx,
                selectedOption = null,
                isSubmitted = false,
                isCorrect = false
            )
        } else {
            _playState.value = curr.copy(isFinished = true)
        }
    }

    fun toggleBookmark(questionId: Int, currentBookmarked: Int) {
        viewModelScope.launch {
            quizRepository.toggleBookmark(questionId, currentBookmarked)
        }
    }
}
