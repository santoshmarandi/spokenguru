package com.spokenenglishguru.app.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.spokenenglishguru.app.ui.navigation.Screen
import com.spokenenglishguru.app.ui.theme.AmberAccent
import com.spokenenglishguru.app.ui.theme.IndigoLight
import com.spokenenglishguru.app.ui.theme.IndigoPrimary
import com.spokenenglishguru.app.ui.theme.StarYellow
import com.spokenenglishguru.app.ui.theme.TealAccent

@Composable
fun PracticeScreen(
    navController: NavController,
    viewModel: PracticeViewModel
) {
    val hubState by viewModel.hubState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sentence Practice Lab",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Master ${hubState.totalSentenceCount} real conversational sentences",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            PracticeModeCard(
                title = "Smart Flashcards",
                description = "Flip Hindi sentences to reveal English translations with real-time TTS pronunciation",
                badge = "Multi-Mode Review",
                icon = Icons.Default.Style,
                color = IndigoPrimary,
                onClick = {
                    navController.navigate(Screen.FlashcardPractice.createRoute(0)) // 0 = random batch
                }
            )
        }

        item {
            PracticeModeCard(
                title = "Sentence Puzzle (Word Jumble)",
                description = "Arrange scattered words in the correct grammatical order to construct perfect sentences",
                badge = "Grammar Drill",
                icon = Icons.Default.Extension,
                color = TealAccent,
                onClick = {
                    navController.navigate(Screen.WordJumblePractice.createRoute(0)) // 0 = random batch
                }
            )
        }

        item {
            PracticeModeCard(
                title = "Rapid Fire Practice",
                description = "Fast-paced 25 sentence challenge drawn across all 40 lessons",
                badge = "25 Questions",
                icon = Icons.Default.Casino,
                color = AmberAccent,
                onClick = {
                    navController.navigate(Screen.FlashcardPractice.createRoute(0))
                }
            )
        }

        if (hubState.favoriteSentences.isNotEmpty()) {
            item {
                PracticeModeCard(
                    title = "Starred Favorites",
                    description = "Review sentences you have bookmarked during lessons",
                    badge = "${hubState.favoriteSentences.size} Starred",
                    icon = Icons.Default.Star,
                    color = StarYellow,
                    onClick = {
                        navController.navigate(Screen.FlashcardPractice.createRoute(-1)) // -1 = favorites
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PracticeModeCard(
    title: String,
    description: String,
    badge: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}
