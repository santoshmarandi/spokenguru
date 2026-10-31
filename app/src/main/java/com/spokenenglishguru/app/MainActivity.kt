package com.spokenenglishguru.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spokenenglishguru.app.ui.components.BottomNavBar
import com.spokenenglishguru.app.ui.navigation.Screen
import com.spokenenglishguru.app.ui.screens.home.HomeScreen
import com.spokenenglishguru.app.ui.screens.home.HomeViewModel
import com.spokenenglishguru.app.ui.screens.lessons.LessonDetailScreen
import com.spokenenglishguru.app.ui.screens.lessons.LessonsScreen
import com.spokenenglishguru.app.ui.screens.lessons.LessonsViewModel
import com.spokenenglishguru.app.ui.screens.practice.FlashcardPracticeScreen
import com.spokenenglishguru.app.ui.screens.practice.PracticeScreen
import com.spokenenglishguru.app.ui.screens.practice.PracticeViewModel
import com.spokenenglishguru.app.ui.screens.practice.WordJumblePracticeScreen
import com.spokenenglishguru.app.ui.screens.quiz.MistakeBankScreen
import com.spokenenglishguru.app.ui.screens.quiz.QuizPlayScreen
import com.spokenenglishguru.app.ui.screens.quiz.QuizViewModel
import com.spokenenglishguru.app.ui.screens.quiz.QuizzesScreen
import com.spokenenglishguru.app.ui.screens.vocab.VocabScreen
import com.spokenenglishguru.app.ui.screens.vocab.VocabViewModel
import com.spokenenglishguru.app.ui.theme.SpokenEnglishTheme
import com.spokenenglishguru.app.util.TextToSpeechManager

class MainActivity : ComponentActivity() {

    private lateinit var ttsManager: TextToSpeechManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ttsManager = TextToSpeechManager(this)

        val app = application as SpokenEnglishApp

        setContent {
            SpokenEnglishTheme {
                MainAppContent(
                    app = app,
                    ttsManager = ttsManager
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::ttsManager.isInitialized) {
            ttsManager.shutdown()
        }
    }
}

@Composable
fun MainAppContent(
    app: SpokenEnglishApp,
    ttsManager: TextToSpeechManager
) {
    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Show bottom bar only on the 5 primary tabs
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Lessons.route,
        Screen.Practice.route,
        Screen.Quizzes.route,
        Screen.Vocab.route
    )

    // Shared ViewModels
    val homeViewModel = remember {
        HomeViewModel(
            app.lessonRepository,
            app.practiceRepository,
            app.quizRepository,
            app.vocabRepository
        )
    }

    val lessonsViewModel = remember {
        LessonsViewModel(
            app.lessonRepository,
            app.practiceRepository
        )
    }

    val practiceViewModel = remember {
        PracticeViewModel(
            app.practiceRepository
        )
    }

    val quizViewModel = remember {
        QuizViewModel(
            app.quizRepository
        )
    }

    val vocabViewModel = remember {
        VocabViewModel(
            app.vocabRepository
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Tab 1: Home
            composable(Screen.Home.route) {
                HomeScreen(
                    navController = navController,
                    viewModel = homeViewModel
                )
            }

            // Tab 2: Lessons
            composable(Screen.Lessons.route) {
                LessonsScreen(
                    navController = navController,
                    viewModel = lessonsViewModel
                )
            }

            // Tab 3: Practice Lab
            composable(Screen.Practice.route) {
                PracticeScreen(
                    navController = navController,
                    viewModel = practiceViewModel
                )
            }

            // Tab 4: 1500+ MCQs & Quizzes
            composable(Screen.Quizzes.route) {
                QuizzesScreen(
                    navController = navController,
                    viewModel = quizViewModel
                )
            }

            // Tab 5: Vocab Vault
            composable(Screen.Vocab.route) {
                VocabScreen(
                    navController = navController,
                    viewModel = vocabViewModel,
                    ttsManager = ttsManager
                )
            }

            // Detail: Lesson Detail
            composable(
                route = Screen.LessonDetail.route,
                arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 1
                LessonDetailScreen(
                    lessonId = lessonId,
                    navController = navController,
                    viewModel = lessonsViewModel,
                    ttsManager = ttsManager
                )
            }

            // Practice: Flashcards
            composable(
                route = Screen.FlashcardPractice.route,
                arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 0
                FlashcardPracticeScreen(
                    lessonId = lessonId,
                    navController = navController,
                    viewModel = practiceViewModel,
                    ttsManager = ttsManager
                )
            }

            // Practice: Word Jumble
            composable(
                route = Screen.WordJumblePractice.route,
                arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 0
                WordJumblePracticeScreen(
                    lessonId = lessonId,
                    navController = navController,
                    viewModel = practiceViewModel,
                    ttsManager = ttsManager
                )
            }

            // Quiz: Play Quiz
            composable(
                route = Screen.QuizPlay.route,
                arguments = listOf(navArgument("quizNum") { type = NavType.IntType })
            ) { backStackEntry ->
                val quizNum = backStackEntry.arguments?.getInt("quizNum") ?: 1
                QuizPlayScreen(
                    quizNum = quizNum,
                    navController = navController,
                    viewModel = quizViewModel,
                    ttsManager = ttsManager
                )
            }

            // Quiz: Mistake Bank
            composable(Screen.MistakeBank.route) {
                MistakeBankScreen(
                    navController = navController,
                    viewModel = quizViewModel
                )
            }
        }
    }
}
