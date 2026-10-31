package com.spokenenglishguru.app.ui.screens.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.spokenenglishguru.app.ui.navigation.Screen
import com.spokenenglishguru.app.ui.theme.AmberAccent
import com.spokenenglishguru.app.ui.theme.IndigoDark
import com.spokenenglishguru.app.ui.theme.IndigoLight
import com.spokenenglishguru.app.ui.theme.IndigoPrimary
import com.spokenenglishguru.app.ui.theme.StarYellow
import com.spokenenglishguru.app.ui.theme.TealAccent

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Hero Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(IndigoPrimary, IndigoLight)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Spoken English Guru",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "English Bolna Sikhein — 40 Lessons",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp
                                )
                            }
                            // Streak badge
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(AmberAccent.copy(alpha = 0.25f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = StarYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${state.streakDays} Days",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Overall Progress
                        val progressFraction = (state.completedLessons.toFloat() / state.totalLessons.toFloat()).coerceIn(0f, 1f)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Course Completion",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${state.completedLessons} / ${state.totalLessons} Lessons",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AmberAccent,
                            trackColor = Color.White.copy(alpha = 0.3f),
                        )
                    }
                }
            }
        }

        // Quick Stats Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Sentences",
                    count = "${state.totalSentences}",
                    color = TealAccent
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "MCQs",
                    count = "${state.totalMcqs}",
                    color = AmberAccent
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Vocab",
                    count = "${state.totalVocab}",
                    color = IndigoLight
                )
            }
        }

        // Section Title: 4 Learning Pillars
        item {
            Text(
                text = "Practice Hub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PillarCard(
                    title = "1. Grammar Lessons & Notes",
                    subtitle = "40 structured chapters with bilingual rules and notes",
                    icon = Icons.Default.Book,
                    badge = "40 Lessons",
                    color = IndigoPrimary,
                    onClick = { navController.navigate(Screen.Lessons.route) }
                )
                PillarCard(
                    title = "2. Sentence Translation Lab",
                    subtitle = "Hindi to English Flashcards with TTS speech audio",
                    icon = Icons.Default.Translate,
                    badge = "5,500+ Sentences",
                    color = TealAccent,
                    onClick = { navController.navigate(Screen.Practice.route) }
                )
                PillarCard(
                    title = "3. 1500+ MCQs & Mock Exams",
                    subtitle = "Quizzes with instant answers, timers & Mistake Bank",
                    icon = Icons.Default.Quiz,
                    badge = "67 Quizzes",
                    color = AmberAccent,
                    onClick = { navController.navigate(Screen.Quizzes.route) }
                )
                PillarCard(
                    title = "4. Vocab Vault",
                    subtitle = "Verbs (V1, V2, V3), Antonyms, Synonyms & Degrees",
                    icon = Icons.Default.AutoAwesome,
                    badge = "740+ Words",
                    color = IndigoLight,
                    onClick = { navController.navigate(Screen.Vocab.route) }
                )
            }
        }

        // Quick Daily Challenge CTA
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(AmberAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Quick Drill",
                            tint = AmberAccent
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily 2-Minute Drill",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Practice random sentences from all lessons",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { navController.navigate(Screen.FlashcardPractice.createRoute(1)) },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start", fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PillarCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
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
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = badge,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            }
        }
    }
}
