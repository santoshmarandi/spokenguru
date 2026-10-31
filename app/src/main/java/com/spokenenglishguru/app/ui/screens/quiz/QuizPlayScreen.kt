package com.spokenenglishguru.app.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Refresh
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

@Composable
fun QuizPlayScreen(
    quizNum: Int,
    navController: NavController,
    viewModel: QuizViewModel,
    ttsManager: TextToSpeechManager
) {
    val state by viewModel.playState.collectAsState()

    LaunchedEffect(quizNum) {
        if (quizNum > 0) {
            viewModel.startQuiz(quizNum)
        } else {
            viewModel.startMistakeQuiz()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (quizNum > 0) "Quiz #$quizNum" else "Mistake Bank Quiz",
                subtitle = if (state.questions.isNotEmpty()) "Question ${(state.currentIndex + 1).coerceAtMost(state.questions.size)} of ${state.questions.size} • Score: ${state.score}" else null,
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
            if (state.questions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading quiz questions...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (state.isFinished) {
                // Finished Scorecard
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val percentage = (state.score.toFloat() / state.questions.size.toFloat() * 100).toInt()
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(if (percentage >= 60) SuccessGreen.copy(alpha = 0.15f) else AmberAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$percentage%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = if (percentage >= 60) SuccessGreen else AmberAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = if (percentage >= 75) "Outstanding Work!" else if (percentage >= 50) "Good Effort!" else "Keep Practicing!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You scored ${state.score} out of ${state.questions.size} questions correct.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (quizNum > 0) viewModel.startQuiz(quizNum) else viewModel.startMistakeQuiz()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry Quiz")
                        }
                    }
                }
            } else {
                val currentQ = state.questions[state.currentIndex]
                val progressFraction = ((state.currentIndex + 1).toFloat() / state.questions.size.toFloat()).coerceIn(0f, 1f)

                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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

                    Spacer(modifier = Modifier.height(18.dp))

                    // Question Prompt Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "QUESTION ${state.currentIndex + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary
                                )
                                Row {
                                    IconButton(onClick = { ttsManager.speak(currentQ.prompt) }, modifier = Modifier.size(32.dp)) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Pronounce", tint = TealAccent, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.toggleBookmark(currentQ.id, currentQ.isBookmarked) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (currentQ.isBookmarked == 1) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = AmberAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = currentQ.prompt,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options List
                    val options = listOfNotNull(
                        "a" to currentQ.optionA,
                        "b" to currentQ.optionB,
                        currentQ.optionC?.let { "c" to it },
                        currentQ.optionD?.let { "d" to it }
                    )

                    options.forEach { (key, optText) ->
                        OptionCard(
                            optionKey = key.uppercase(),
                            text = optText,
                            isSelected = state.selectedOption.equals(key, ignoreCase = true),
                            isCorrect = key.equals(currentQ.correctOption, ignoreCase = true),
                            isSubmitted = state.isSubmitted,
                            onClick = { viewModel.selectOption(key) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // Next Button
                if (state.isSubmitted) {
                    Button(
                        onClick = { viewModel.nextQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Text(
                            text = if (state.currentIndex + 1 < state.questions.size) "Next Question" else "See Results",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.NavigateNext, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun OptionCard(
    optionKey: String,
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isSubmitted: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSubmitted && isCorrect -> SuccessGreen.copy(alpha = 0.15f)
        isSubmitted && isSelected && !isCorrect -> ErrorRed.copy(alpha = 0.15f)
        isSelected -> IndigoPrimary.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isSubmitted && isCorrect -> SuccessGreen
        isSubmitted && isSelected && !isCorrect -> ErrorRed
        isSelected -> IndigoPrimary
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isSubmitted, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isSubmitted && isCorrect -> SuccessGreen
                            isSubmitted && isSelected && !isCorrect -> ErrorRed
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSubmitted && isCorrect) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else if (isSubmitted && isSelected && !isCorrect) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        text = optionKey,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
