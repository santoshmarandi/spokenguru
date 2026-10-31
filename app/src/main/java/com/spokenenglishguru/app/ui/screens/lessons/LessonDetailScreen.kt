package com.spokenenglishguru.app.ui.screens.lessons

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.spokenenglishguru.app.data.db.entity.SentencePracticeEntity
import com.spokenenglishguru.app.ui.components.AppTopBar
import com.spokenenglishguru.app.ui.navigation.Screen
import com.spokenenglishguru.app.ui.theme.AmberAccent
import com.spokenenglishguru.app.ui.theme.IndigoPrimary
import com.spokenenglishguru.app.ui.theme.StarYellow
import com.spokenenglishguru.app.ui.theme.TealAccent
import com.spokenenglishguru.app.util.TextToSpeechManager

@Composable
fun LessonDetailScreen(
    lessonId: Int,
    navController: NavController,
    viewModel: LessonsViewModel,
    ttsManager: TextToSpeechManager
) {
    val lesson by viewModel.getLessonById(lessonId).collectAsState(initial = null)
    val practices by viewModel.getPracticesForLesson(lessonId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            AppTopBar(
                title = lesson?.title ?: "Lesson $lessonId",
                subtitle = "Lesson $lessonId • ${practices.size} Exercises",
                canNavigateBack = true,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Action Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = lesson?.title ?: "",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "PDF Pages: ${lesson?.pdfPages ?: "N/A"} (Printed: ${lesson?.printedPages ?: "N/A"})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    navController.navigate(Screen.FlashcardPractice.createRoute(lessonId))
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                            ) {
                                Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Flashcards", fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    navController.navigate(Screen.WordJumblePractice.createRoute(lessonId))
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Extension, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Puzzle Drill", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Bilingual Practice Exercises (${practices.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            itemsIndexed(practices, key = { _, item -> item.id }) { index, item ->
                SentenceExerciseCard(
                    index = index + 1,
                    item = item,
                    onSpeak = { ttsManager.speak(item.englishAnswer) },
                    onToggleFavorite = { viewModel.toggleFavorite(item.id, item.isFavorite) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SentenceExerciseCard(
    index: Int,
    item: SentencePracticeEntity,
    onSpeak: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Number badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(IndigoPrimary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "#$index",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSpeak, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak",
                            tint = TealAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (item.isFavorite == 1) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite == 1) StarYellow else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Hindi prompt
            Text(
                text = item.promptPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
            )

            // Romanized hint
            if (!item.promptSecondary.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.promptSecondary,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // English Answer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                Text(
                    text = item.englishAnswer,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = AmberAccent,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
