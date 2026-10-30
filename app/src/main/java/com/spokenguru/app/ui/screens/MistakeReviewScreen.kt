package com.spokenguru.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.spokenguru.app.ui.theme.isAppDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spokenguru.app.audio.SoundEffectManager
import com.spokenguru.app.audio.TtsManager
import com.spokenguru.app.data.model.QuestionEntity
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.BankWordTile
import com.spokenguru.app.ui.components.GuruButton
import com.spokenguru.app.ui.components.GuruButtonVariant
import com.spokenguru.app.ui.components.SlotWordChip
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkBg
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruDarkTextMuted
import com.spokenguru.app.ui.theme.GuruMint
import com.spokenguru.app.ui.theme.GuruMintDark
import com.spokenguru.app.ui.theme.GuruMintLight
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruRedDark
import com.spokenguru.app.ui.theme.GuruRedLight
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MistakeReviewScreen(
    repository: LessonRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }

    val ttsManager = remember { TtsManager(context) }
    DisposableEffect(Unit) {
        onDispose { ttsManager.shutdown() }
    }

    val mistakes by repository.allMistakes.collectAsState(initial = emptyList())
    var currentIndex by remember { mutableIntStateOf(0) }
    var answerState by remember { mutableStateOf(AnswerState.IDLE) }

    val bankWords = remember { mutableStateListOf<BankWordItem>() }
    val selectedWords = remember { mutableStateListOf<BankWordItem>() }

    LaunchedEffect(currentIndex, mistakes) {
        if (mistakes.isNotEmpty() && currentIndex < mistakes.size) {
            val q = mistakes[currentIndex]
            selectedWords.clear()
            answerState = AnswerState.IDLE

            val correctWords = q.primaryAnswer.split("\\s+".toRegex()).map { cleanWordPunctuation(it) }.filter { it.isNotBlank() }
            val distractorType = object : TypeToken<List<String>>() {}.type
            val distractors: List<String> = try {
                gson.fromJson(q.distractorsJson, distractorType) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            val list = mutableListOf<BankWordItem>()
            correctWords.forEachIndexed { idx, w ->
                list.add(BankWordItem(id = idx, word = w, isDistractor = false))
            }
            distractors.take(2).forEachIndexed { idx, w ->
                list.add(BankWordItem(id = 100 + idx, word = w, isDistractor = true))
            }

            bankWords.clear()
            bankWords.addAll(list.shuffled())
        }
    }

    if (mistakes.isEmpty()) {
        Scaffold(containerColor = if (isDark) GuruDarkBg else GuruBg) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(GuruMintLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = GuruMint,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "All Mistakes Cleared!",
                        fontFamily = FontFamily.Default,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Great job! All your flagged mistakes have been reviewed.",
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp,
                        color = GuruTextMuted
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    GuruButton(
                        text = "Back to Home",
                        onClick = onNavigateBack,
                        height = 46.dp,
                        modifier = Modifier.width(180.dp)
                    )
                }
            }
        }
        return
    }

    val currentQuestion = if (currentIndex < mistakes.size) mistakes[currentIndex] else mistakes.first()

    Scaffold(
        containerColor = if (isDark) GuruDarkBg else GuruBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = if (isDark) GuruDarkText else GuruTextDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / mistakes.size.toFloat() },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = GuruRed,
                        trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE5E7EB)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${currentIndex + 1}/${mistakes.size}",
                        fontFamily = FontFamily.Default,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                    )
                }
            }
        },
        bottomBar = {
            if (answerState == AnswerState.IDLE) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDark) GuruDarkSurface else Color.White)
                ) {
                    HorizontalDivider(
                        thickness = 0.8.dp,
                        color = (if (isDark) GuruDarkCardBorder else GuruCardBorder).copy(alpha = 0.6f)
                    )
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                        GuruButton(
                            text = "Check Correction",
                            icon = Icons.Default.Check,
                            enabled = selectedWords.isNotEmpty(),
                            onClick = {
                                val userSentence = selectedWords.joinToString(" ") { it.word }
                                val isCorrect = checkAnswer(
                                    userSentence = userSentence,
                                    primary = currentQuestion.primaryAnswer,
                                    alternatesJson = currentQuestion.alternateAnswersJson,
                                    gson = gson
                                )

                                if (isCorrect) {
                                    answerState = AnswerState.CORRECT
                                    SoundEffectManager.playSuccess()
                                    ttsManager.speak(currentQuestion.primaryAnswer)
                                    scope.launch {
                                        repository.markMistake(currentQuestion.id, false)
                                    }
                                } else {
                                    answerState = AnswerState.INCORRECT
                                    SoundEffectManager.playError()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            height = 48.dp
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "REVISE MISTAKE",
                    fontFamily = FontFamily.Default,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GuruRed,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentQuestion.hindiText,
                    fontFamily = FontFamily.Default,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) GuruDarkText else GuruTextDark,
                    lineHeight = 32.sp
                )

                if (currentQuestion.romanText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQuestion.romanText,
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp,
                        color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Answer Slot Area
                val slotShape = RoundedCornerShape(16.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 100.dp)
                        .clip(slotShape)
                        .background(if (isDark) GuruDarkSurface else Color.White)
                        .border(
                            width = 1.dp,
                            color = if (isDark) GuruDarkCardBorder else GuruCardBorder,
                            shape = slotShape
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (selectedWords.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tap words below in correct order",
                                fontFamily = FontFamily.Default,
                                fontSize = 14.sp,
                                color = GuruTextSubtle
                            )
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        if (selectedWords.isNotEmpty()) {
                                            selectedWords.removeAt(selectedWords.size - 1)
                                        }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Undo,
                                        contentDescription = "Undo",
                                        tint = GuruTextSubtle,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { selectedWords.clear() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = GuruTextSubtle,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                selectedWords.forEach { item ->
                                    SlotWordChip(
                                        text = item.word,
                                        onRemove = {
                                            if (answerState == AnswerState.IDLE) {
                                                SoundEffectManager.playClick()
                                                selectedWords.remove(item)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Word Bank
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    bankWords.forEach { item ->
                        val isSelected = selectedWords.any { it.id == item.id }
                        BankWordTile(
                            word = item.word,
                            isSelected = isSelected,
                            onClick = {
                                if (answerState == AnswerState.IDLE && !isSelected) {
                                    SoundEffectManager.playClick()
                                    selectedWords.add(item)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }

            // Feedback Bottom Sheet
            AnimatedVisibility(
                visible = answerState != AnswerState.IDLE,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, shape)
                        .clip(shape)
                        .background(
                            if (answerState == AnswerState.CORRECT) {
                                if (isDark) Color(0xFF10281D) else GuruMintLight
                            } else {
                                if (isDark) Color(0xFF2C1318) else GuruRedLight
                            }
                        )
                        .border(
                            1.dp,
                            if (answerState == AnswerState.CORRECT) GuruMint.copy(alpha = 0.3f) else GuruRed.copy(alpha = 0.3f),
                            shape
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (answerState == AnswerState.CORRECT) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = null,
                                tint = if (answerState == AnswerState.CORRECT) GuruMintDark else GuruRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (answerState == AnswerState.CORRECT) "Correct! Mistake cleared 🎉" else "Not quite right",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = if (answerState == AnswerState.CORRECT) GuruMintDark else GuruRedDark
                            )
                        }

                        if (answerState == AnswerState.INCORRECT) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Correct: \"${currentQuestion.primaryAnswer}\"",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = if (isDark) GuruDarkText else GuruTextDark
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (answerState == AnswerState.CORRECT) {
                            GuruButton(
                                text = "Next Question",
                                icon = Icons.AutoMirrored.Filled.ArrowForward,
                                variant = GuruButtonVariant.PRIMARY,
                                onClick = {
                                    if (currentIndex < mistakes.size - 1) {
                                        currentIndex++
                                    } else {
                                        currentIndex = 0
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                height = 48.dp
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GuruButton(
                                    text = "Try Again",
                                    icon = Icons.Default.Refresh,
                                    variant = GuruButtonVariant.SECONDARY,
                                    onClick = { answerState = AnswerState.IDLE },
                                    modifier = Modifier.weight(1f),
                                    height = 48.dp
                                )
                                GuruButton(
                                    text = "Skip",
                                    variant = GuruButtonVariant.PRIMARY,
                                    onClick = {
                                        if (currentIndex < mistakes.size - 1) {
                                            currentIndex++
                                        } else {
                                            currentIndex = 0
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    height = 48.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
