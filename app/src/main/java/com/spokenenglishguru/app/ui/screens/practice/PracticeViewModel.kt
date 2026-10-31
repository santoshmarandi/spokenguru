package com.spokenenglishguru.app.ui.screens.practice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spokenenglishguru.app.data.db.entity.SentencePracticeEntity
import com.spokenenglishguru.app.data.repository.PracticeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class PracticeHubState(
    val favoriteSentences: List<SentencePracticeEntity> = emptyList(),
    val totalSentenceCount: Int = 0
)

data class FlashcardState(
    val items: List<SentencePracticeEntity> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val masteredCount: Int = 0,
    val reviewCount: Int = 0,
    val isCompleted: Boolean = false
)

data class WordJumbleState(
    val items: List<SentencePracticeEntity> = emptyList(),
    val currentIndex: Int = 0,
    val targetWords: List<String> = emptyList(),
    val availableChips: List<String> = emptyList(),
    val selectedChips: List<String> = emptyList(),
    val isChecked: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0
)

class PracticeViewModel(
    private val practiceRepository: PracticeRepository
) : ViewModel() {

    private val _hubState = MutableStateFlow(PracticeHubState())
    val hubState: StateFlow<PracticeHubState> = _hubState.asStateFlow()

    private val _flashcardState = MutableStateFlow(FlashcardState())
    val flashcardState: StateFlow<FlashcardState> = _flashcardState.asStateFlow()

    private val _jumbleState = MutableStateFlow(WordJumbleState())
    val jumbleState: StateFlow<WordJumbleState> = _jumbleState.asStateFlow()

    init {
        viewModelScope.launch {
            practiceRepository.getFavoritePractices()
                .catch { e -> Log.e("PracticeViewModel", "Error loading favorites", e) }
                .collect { favs ->
                    _hubState.value = _hubState.value.copy(favoriteSentences = favs)
                }
        }
        viewModelScope.launch {
            practiceRepository.getTotalSentenceCount()
                .catch { e -> Log.e("PracticeViewModel", "Error loading count", e) }
                .collect { count ->
                    _hubState.value = _hubState.value.copy(totalSentenceCount = count)
                }
        }
    }

    // Flashcard Logic
    fun loadFlashcards(lessonId: Int) {
        viewModelScope.launch {
            val flow = if (lessonId <= 0) {
                practiceRepository.getRandomBatch(25)
            } else {
                practiceRepository.getPracticesForLesson(lessonId)
            }
            flow.collect { list ->
                _flashcardState.value = FlashcardState(
                    items = list,
                    currentIndex = 0,
                    isFlipped = false,
                    masteredCount = 0,
                    reviewCount = 0,
                    isCompleted = false
                )
            }
        }
    }

    fun flipCard() {
        _flashcardState.value = _flashcardState.value.copy(isFlipped = !_flashcardState.value.isFlipped)
    }

    fun recordCardAnswer(mastered: Boolean) {
        val curr = _flashcardState.value
        val nextIdx = curr.currentIndex + 1
        val isFinished = nextIdx >= curr.items.size

        if (curr.items.isNotEmpty() && curr.currentIndex < curr.items.size) {
            val item = curr.items[curr.currentIndex]
            viewModelScope.launch {
                practiceRepository.updateMastery(item.id, if (mastered) 1 else 0)
            }
        }

        _flashcardState.value = curr.copy(
            currentIndex = nextIdx,
            isFlipped = false,
            masteredCount = if (mastered) curr.masteredCount + 1 else curr.masteredCount,
            reviewCount = if (!mastered) curr.reviewCount + 1 else curr.reviewCount,
            isCompleted = isFinished
        )
    }

    // Word Jumble Logic
    fun loadWordJumble(lessonId: Int) {
        viewModelScope.launch {
            val flow = if (lessonId <= 0) {
                practiceRepository.getRandomBatch(20)
            } else {
                practiceRepository.getPracticesForLesson(lessonId)
            }
            flow.collect { list ->
                val cleanList = list.filter { it.englishAnswer.split("\\s+".toRegex()).size in 3..12 }
                _jumbleState.value = WordJumbleState(
                    items = cleanList,
                    currentIndex = 0,
                    score = 0
                )
                setupCurrentJumbleQuestion(0, cleanList)
            }
        }
    }

    private fun setupCurrentJumbleQuestion(index: Int, list: List<SentencePracticeEntity>) {
        if (list.isEmpty() || index >= list.size) return
        val current = list[index]
        // Clean words: remove punctuation from tokens for chip selection
        val words = current.englishAnswer
            .replace(Regex("[.,!?;:\"()\\-]"), "")
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

        _jumbleState.value = _jumbleState.value.copy(
            currentIndex = index,
            targetWords = words,
            availableChips = words.shuffled(),
            selectedChips = emptyList(),
            isChecked = false,
            isCorrect = false
        )
    }

    fun selectWordChip(word: String) {
        val curr = _jumbleState.value
        if (curr.isChecked) return
        val newAvailable = curr.availableChips.toMutableList()
        newAvailable.remove(word)
        val newSelected = curr.selectedChips + word
        _jumbleState.value = curr.copy(
            availableChips = newAvailable,
            selectedChips = newSelected
        )
    }

    fun removeWordChip(word: String) {
        val curr = _jumbleState.value
        if (curr.isChecked) return
        val newSelected = curr.selectedChips.toMutableList()
        newSelected.remove(word)
        val newAvailable = curr.availableChips + word
        _jumbleState.value = curr.copy(
            availableChips = newAvailable,
            selectedChips = newSelected
        )
    }

    fun checkJumbleAnswer() {
        val curr = _jumbleState.value
        val userSentence = curr.selectedChips.joinToString(" ").lowercase()
        val targetSentence = curr.targetWords.joinToString(" ").lowercase()
        val correct = userSentence == targetSentence

        _jumbleState.value = curr.copy(
            isChecked = true,
            isCorrect = correct,
            score = if (correct) curr.score + 1 else curr.score
        )
    }

    fun nextJumbleQuestion() {
        val curr = _jumbleState.value
        val nextIdx = curr.currentIndex + 1
        setupCurrentJumbleQuestion(nextIdx, curr.items)
    }
}
