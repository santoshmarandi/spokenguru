package com.spokenenglishguru.app.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.spokenenglishguru.app.ui.navigation.Screen
import com.spokenenglishguru.app.ui.theme.AmberAccent
import com.spokenenglishguru.app.ui.theme.IndigoPrimary

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Lessons,
        Screen.Practice,
        Screen.Quizzes,
        Screen.Vocab
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                icon = {
                    screen.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = screen.title
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AmberAccent,
                    selectedTextColor = AmberAccent,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
