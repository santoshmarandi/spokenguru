package com.spokenguru.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spokenguru.app.data.UserPrefs
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.NavTab
import com.spokenguru.app.ui.screens.ExerciseEbookScreen
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
        UserPrefs.init(applicationContext)

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
                    val targetLesson = remember { intent?.getIntExtra("lessonNumber", -1) ?: -1 }
                    val startDest = if (targetLesson > 0) "quiz/$targetLesson" else "home"

                    fun navigateToTab(tab: NavTab, activeTab: NavTab) {
                        if (tab == activeTab) return
                        when (tab) {
                            NavTab.PRACTICE -> {
                                val resumeLesson = UserPrefs.lastOpenedLesson.coerceAtLeast(1)
                                navController.navigate("quiz/$resumeLesson")
                            }
                            NavTab.HOME -> {
                                navController.navigate("home") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            NavTab.EBOOK -> {
                                navController.navigate("ebook") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            NavTab.PROFILE -> {
                                navController.navigate("profile") {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = startDest,
                        enterTransition = { fadeIn(animationSpec = tween(220)) },
                        exitTransition = { fadeOut(animationSpec = tween(180)) },
                        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
                        popExitTransition = { fadeOut(animationSpec = tween(180)) }
                    ) {
                        composable("home") {
                            LessonListScreen(
                                repository = repository,
                                currentTab = NavTab.HOME,
                                onTabSelected = { tab -> navigateToTab(tab, NavTab.HOME) },
                                onSelectLesson = { lessonNumber ->
                                    UserPrefs.setLastOpenedLesson(applicationContext, lessonNumber)
                                    navController.navigate("quiz/$lessonNumber")
                                },
                                onOpenMistakes = {
                                    navController.navigate("mistakes")
                                }
                            )
                        }

                        composable("ebook") {
                            ExerciseEbookScreen(
                                repository = repository,
                                currentTab = NavTab.EBOOK,
                                onTabSelected = { tab -> navigateToTab(tab, NavTab.EBOOK) },
                                onStartPractice = { lessonNumber ->
                                    UserPrefs.setLastOpenedLesson(applicationContext, lessonNumber)
                                    navController.navigate("quiz/$lessonNumber")
                                }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                repository = repository,
                                currentTab = NavTab.PROFILE,
                                onTabSelected = { tab -> navigateToTab(tab, NavTab.PROFILE) },
                                onOpenMistakes = {
                                    navController.navigate("mistakes")
                                }
                            )
                        }

                        composable(
                            route = "quiz/{lessonNumber}",
                            arguments = listOf(
                                navArgument("lessonNumber") { type = NavType.IntType }
                            ),
                            enterTransition = {
                                slideInHorizontally(tween(260)) { it / 3 } + fadeIn(tween(260))
                            },
                            exitTransition = { fadeOut(tween(180)) },
                            popEnterTransition = { fadeIn(tween(200)) },
                            popExitTransition = {
                                slideOutHorizontally(tween(240)) { it / 3 } + fadeOut(tween(200))
                            }
                        ) { backStackEntry ->
                            val lessonNumber = backStackEntry.arguments?.getInt("lessonNumber") ?: 1
                            WordPickScreen(
                                lessonNumber = lessonNumber,
                                repository = repository,
                                onNavigateBack = {
                                    if (!navController.popBackStack()) {
                                        navController.navigate("home") {
                                            popUpTo(0)
                                        }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "mistakes",
                            enterTransition = {
                                slideInHorizontally(tween(260)) { it / 3 } + fadeIn(tween(260))
                            },
                            popExitTransition = {
                                slideOutHorizontally(tween(240)) { it / 3 } + fadeOut(tween(200))
                            }
                        ) {
                            MistakeReviewScreen(
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
