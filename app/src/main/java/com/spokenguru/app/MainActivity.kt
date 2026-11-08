package com.spokenguru.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.NavTab
import com.spokenguru.app.ui.screens.ExerciseEbookScreen
import com.spokenguru.app.ui.screens.ImportLessonScreen
import com.spokenguru.app.ui.screens.LessonListScreen
import com.spokenguru.app.ui.screens.MistakeReviewScreen
import com.spokenguru.app.ui.screens.ProfileScreen
import com.spokenguru.app.ui.screens.WordPickScreen
import com.spokenguru.app.ui.theme.SpokenGuruTheme
import com.spokenguru.app.ui.theme.ThemeState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: LessonRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeState.init(applicationContext)

        repository = LessonRepository(applicationContext)

        lifecycleScope.launch {
            repository.ensureDataSeeded()
        }

        setContent {
            SpokenGuruTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var currentTab by remember { mutableStateOf(NavTab.HOME) }
                    val targetLesson = remember { intent?.getIntExtra("lessonNumber", -1) ?: -1 }
                    val startDest = if (targetLesson > 0) "quiz/$targetLesson" else "home"

                    NavHost(
                        navController = navController,
                        startDestination = startDest
                    ) {
                        composable("home") {
                            currentTab = NavTab.HOME
                            LessonListScreen(
                                repository = repository,
                                currentTab = currentTab,
                                onTabSelected = { tab ->
                                    currentTab = tab
                                    when (tab) {
                                        NavTab.HOME -> {}
                                        NavTab.EBOOK -> navController.navigate("ebook")
                                        NavTab.PRACTICE -> navController.navigate("quiz/1")
                                        NavTab.PROFILE -> navController.navigate("profile")
                                    }
                                },
                                onSelectLesson = { lessonNumber ->
                                    navController.navigate("quiz/$lessonNumber")
                                }
                            )
                        }

                        composable("ebook") {
                            currentTab = NavTab.EBOOK
                            ExerciseEbookScreen(
                                repository = repository,
                                currentTab = currentTab,
                                onTabSelected = { tab ->
                                    currentTab = tab
                                    when (tab) {
                                        NavTab.HOME -> navController.navigate("home")
                                        NavTab.EBOOK -> {}
                                        NavTab.PRACTICE -> navController.navigate("quiz/1")
                                        NavTab.PROFILE -> navController.navigate("profile")
                                    }
                                },
                                onStartPractice = { lessonNumber ->
                                    navController.navigate("quiz/$lessonNumber")
                                }
                            )
                        }

                        composable("profile") {
                            currentTab = NavTab.PROFILE
                            ProfileScreen(
                                repository = repository,
                                currentTab = currentTab,
                                onTabSelected = { tab ->
                                    currentTab = tab
                                    when (tab) {
                                        NavTab.HOME -> navController.navigate("home")
                                        NavTab.EBOOK -> navController.navigate("ebook")
                                        NavTab.PRACTICE -> navController.navigate("quiz/1")
                                        NavTab.PROFILE -> {}
                                    }
                                },
                                onOpenMistakes = {
                                    navController.navigate("mistakes")
                                },
                                onOpenImport = {
                                    navController.navigate("import")
                                }
                            )
                        }

                        composable(
                            route = "quiz/{lessonNumber}",
                            arguments = listOf(
                                navArgument("lessonNumber") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val lessonNumber = backStackEntry.arguments?.getInt("lessonNumber") ?: 1
                            WordPickScreen(
                                lessonNumber = lessonNumber,
                                repository = repository,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("mistakes") {
                            MistakeReviewScreen(
                                repository = repository,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("import") {
                            ImportLessonScreen(
                                repository = repository,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
