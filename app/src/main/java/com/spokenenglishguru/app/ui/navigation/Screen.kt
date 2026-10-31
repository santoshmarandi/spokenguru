package com.spokenenglishguru.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Bottom Nav Tabs
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Lessons : Screen("lessons", "Lessons", Icons.Default.Book)
    object Practice : Screen("practice", "Practice", Icons.Default.Translate)
    object Quizzes : Screen("quizzes", "Quizzes", Icons.Default.Quiz)
    object Vocab : Screen("vocab", "Vocab", Icons.Default.AutoAwesome)

    // Sub-screens
    object LessonDetail : Screen("lesson_detail/{lessonId}", "Lesson Detail") {
        fun createRoute(lessonId: Int) = "lesson_detail/$lessonId"
    }

    object FlashcardPractice : Screen("practice_flashcards/{lessonId}", "Flashcards") {
        fun createRoute(lessonId: Int) = "practice_flashcards/$lessonId"
    }

    object WordJumblePractice : Screen("practice_jumble/{lessonId}", "Sentence Puzzle") {
        fun createRoute(lessonId: Int) = "practice_jumble/$lessonId"
    }

    object QuizPlay : Screen("quiz_play/{quizNum}", "Quiz") {
        fun createRoute(quizNum: Int) = "quiz_play/$quizNum"
    }

    object MistakeBank : Screen("mistake_bank", "Mistake Bank")
}
