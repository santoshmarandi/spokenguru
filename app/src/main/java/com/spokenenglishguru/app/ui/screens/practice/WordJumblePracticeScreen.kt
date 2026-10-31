package com.spokenenglishguru.app.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordJumblePracticeScreen(
    lessonId: Int,
    navController: NavController,
    viewModel: PracticeViewModel,
    ttsManager: TextToSpeechManager
) {
    val state by viewModel.jumbleState.collectAsState()

    LaunchedEffect(lessonId) {
        viewModel.loadWordJumble(lessonId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Sentence Puzzle",
                subtitle = "Score: ${state.score} • Question ${(state.currentIndex + 1).coerceAtMost(state.items.size)}/${state.items.size}",
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (state.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading puzzle drill...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val currentItem = state.items[state.currentIndex]

                Column {
                    // Prompt Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "TRANSLATE INTO ENGLISH",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentItem.promptPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (!currentItem.promptSecondary.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentItem.promptSecondary,
                                    fontSize = 13.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // User sentence drop area
                    Text(
                        text = "Construct Sentence:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.5.dp,
                                color = if (state.isChecked) {
                                    if (state.isCorrect) SuccessGreen else ErrorRed
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(14.dp)
                    ) {
                        if (state.selectedChips.isEmpty()) {
                            Text(
                                text = "Tap the words below in the correct order...",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        } else {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.selectedChips.forEach { word ->
                                    WordChip(
                                        word = word,
                                        isPlaced = true,
                                        onClick = { viewModel.removeWordChip(word) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Available chips pool
                    Text(
                        text = "Available Words:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        state.availableChips.forEach { word ->
                            WordChip(
                                word = word,
                                isPlaced = false,
                                onClick = { viewModel.selectWordChip(word) }
                            )
                        }
                    }
                }

                // Feedback / Check Bottom Area
                Column {
                    if (state.isChecked) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (state.isCorrect) SuccessGreen.copy(alpha = 0.12f) else ErrorRed.copy(alpha = 0.12f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (state.isCorrect) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (state.isCorrect) SuccessGreen else ErrorRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (state.isCorrect) "Excellent! Sahi uttar hai." else "Incorrect!",
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.isCorrect) SuccessGreen else ErrorRed
                                    )
                                    if (!state.isCorrect) {
                                        Text(
                                            text = "Answer: ${currentItem.englishAnswer}",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                IconButton(onClick = { ttsManager.speak(currentItem.englishAnswer) }) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Pronounce", tint = TealAccent)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    Button(
                        onClick = {
                            if (!state.isChecked) {
                                viewModel.checkJumbleAnswer()
                            } else {
                                viewModel.nextJumbleQuestion()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isChecked) IndigoPrimary else AmberAccent
                        ),
                        enabled = state.selectedChips.isNotEmpty() || state.isChecked
                    ) {
                        Text(
                            text = if (!state.isChecked) "Check Answer" else "Next Sentence",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        if (state.isChecked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.NavigateNext, contentDescription = null)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun WordChip(
    word: String,
    isPlaced: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isPlaced) IndigoPrimary else MaterialTheme.colorScheme.surface
            )
            .border(
                width = 1.dp,
                color = if (isPlaced) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text = word,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = if (isPlaced) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}
