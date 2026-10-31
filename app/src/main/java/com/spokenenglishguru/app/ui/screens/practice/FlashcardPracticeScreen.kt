package com.spokenenglishguru.app.ui.screens.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.spokenenglishguru.app.ui.components.AppTopBar
import com.spokenenglishguru.app.ui.theme.AmberAccent
import com.spokenenglishguru.app.ui.theme.ErrorRed
import com.spokenenglishguru.app.ui.theme.IndigoPrimary
import com.spokenenglishguru.app.ui.theme.SuccessGreen
import com.spokenenglishguru.app.ui.theme.TealAccent
import com.spokenenglishguru.app.util.TextToSpeechManager

@Composable
fun FlashcardPracticeScreen(
    lessonId: Int,
    navController: NavController,
    viewModel: PracticeViewModel,
    ttsManager: TextToSpeechManager
) {
    val state by viewModel.flashcardState.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadFlashcards(lessonId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (lessonId > 0) "Lesson $lessonId Flashcards" else "Practice Flashcards",
                subtitle = if (state.items.isNotEmpty()) "${(state.currentIndex + 1).coerceAtMost(state.items.size)} of ${state.items.size}" else null,
                canNavigateBack = true,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading flashcards...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (state.isCompleted) {
                // Completed Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Session Complete!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mastered: ${state.masteredCount}  •  Review Again: ${state.reviewCount}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.loadFlashcards(lessonId) },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Practice Again")
                        }
                    }
                }
            } else {
                val currentItem = state.items[state.currentIndex]
                val progressFraction = ((state.currentIndex + 1).toFloat() / state.items.size.toFloat()).coerceIn(0f, 1f)

                // Progress Bar
                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AmberAccent,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Flashcard Box with Flip State
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            viewModel.flipCard()
                            if (!state.isFlipped) {
                                ttsManager.speak(currentItem.englishAnswer)
                            }
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.isFlipped) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (state.isFlipped) "ENGLISH ANSWER" else "HINDI PROMPT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.isFlipped) AmberAccent else IndigoPrimary
                            )
                            if (state.isFlipped) {
                                IconButton(
                                    onClick = { ttsManager.speak(currentItem.englishAnswer) }
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Pronounce", tint = TealAccent)
                                }
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 20.dp)
                        ) {
                            if (!state.isFlipped) {
                                Text(
                                    text = currentItem.promptPrimary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 32.sp
                                )
                                if (!currentItem.promptSecondary.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = currentItem.promptSecondary,
                                        fontSize = 15.sp,
                                        fontStyle = FontStyle.Italic,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                Text(
                                    text = currentItem.englishAnswer,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 30.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (state.isFlipped) "Tap to see prompt" else "Tap card to reveal answer",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Button(
                        onClick = { viewModel.recordCardAnswer(mastered = false) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed.copy(alpha = 0.12f), contentColor = ErrorRed)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Need Practice", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.recordCardAnswer(mastered = true) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mastered", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
