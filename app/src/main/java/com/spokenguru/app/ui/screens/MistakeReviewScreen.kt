package com.spokenguru.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.spokenguru.app.ui.theme.isAppDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spokenguru.app.audio.SoundEffectManager
import com.spokenguru.app.audio.TtsManager
import com.spokenguru.app.data.UserPrefs
import com.spokenguru.app.data.model.QuestionEntity
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.BankWordTile
import com.spokenguru.app.ui.components.CelebrationBurstOverlay
import com.spokenguru.app.ui.components.GuruButton
import com.spokenguru.app.ui.components.GuruButtonVariant
import com.spokenguru.app.ui.components.SlotWordChip
import com.spokenguru.app.ui.components.rememberShakeOffset
import kotlin.math.roundToInt
import com.spokenguru.app.ui.theme.GuruAmber
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruCardBorderDashed
import com.spokenguru.app.ui.theme.GuruDarkBg
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkSurfaceContainer
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruDarkTextMuted
import com.spokenguru.app.ui.theme.GuruMint
import com.spokenguru.app.ui.theme.GuruMintDark
import com.spokenguru.app.ui.theme.GuruMintLight
import com.spokenguru.app.ui.theme.GuruProgressTrack
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruRedDark
import com.spokenguru.app.ui.theme.GuruRedLight
import com.spokenguru.app.ui.theme.GuruSurfaceContainer
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
    var lastSubmittedAnswer by remember { mutableStateOf("") }
    var celebrationTrigger by remember { mutableIntStateOf(0) }
    var shakeTrigger by remember { mutableIntStateOf(0) }

    val bankWords = remember { mutableStateListOf<BankWordItem>() }
    val selectedWords = remember { mutableStateListOf<BankWordItem>() }

    LaunchedEffect(currentIndex, mistakes, UserPrefs.smartDistractorsEnabled) {
        if (mistakes.isNotEmpty() && currentIndex < mistakes.size) {
            val q = mistakes[currentIndex]
            selectedWords.clear()
            answerState = AnswerState.IDLE

            val correctWords = q.primaryAnswer.split("\\s+".toRegex()).map { cleanWordPunctuation(it) }.filter { it.isNotBlank() }

            val list = mutableListOf<BankWordItem>()
            correctWords.forEachIndexed { idx, w ->
                list.add(BankWordItem(id = idx, word = w, isDistractor = false))
            }
            if (UserPrefs.smartDistractorsEnabled) {
                val targetDistractors = if (correctWords.size <= 2) 2 else 3
                val smartDistractors = selectSmartDistractors(
                    primaryAnswer = q.primaryAnswer,
                    alternateAnswersJson = q.alternateAnswersJson,
                    distractorsJson = q.distractorsJson,
                    targetCount = targetDistractors,
                    gson = gson
                )
                smartDistractors.forEachIndexed { idx, w ->
                    list.add(BankWordItem(id = 100 + idx, word = w, isDistractor = true))
                }
            }

            bankWords.clear()
            bankWords.addAll(
                shuffleAvoidOrder(list) { candidate ->
                    candidate.map { cleanSentence(it.word) } == correctWords.map { cleanSentence(it) }
                }
            )
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

    var selectedMcqOption by remember(currentQuestion.id) { mutableStateOf<String?>(null) }
    var selectedBlankWord by remember(currentQuestion.id) { mutableStateOf<String?>(null) }
    var selectedCorrection by remember(currentQuestion.id) { mutableStateOf<String?>(null) }

    val parsedOptions = remember(currentQuestion.id) {
        try {
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(currentQuestion.optionsJson, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    val parsedMatchPairs = remember(currentQuestion.id) {
        try {
            val mapType = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson<Map<String, String>>(currentQuestion.matchPairsJson, mapType) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    val hasAnswerInput = when (currentQuestion.questionType) {
        "WORD_MATCH" -> false
        "MCQ" -> !selectedMcqOption.isNullOrBlank()
        "FILL_BLANK" -> !selectedBlankWord.isNullOrBlank()
        "SENTENCE_CORRECTION" -> !selectedCorrection.isNullOrBlank()
        else -> selectedWords.isNotEmpty()
    }

    Scaffold(
        containerColor = if (isDark) GuruDarkBg else GuruBg,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = if (isDark) GuruDarkText else GuruTextDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Clean Animated Progress Bar with Prev / Pill Counter / Next
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        enabled = currentIndex > 0,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Mistake",
                            tint = if (currentIndex > 0) (if (isDark) GuruDarkText else GuruTextDark) else GuruTextSubtle.copy(alpha = 0.28f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / mistakes.size.toFloat() },
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(CircleShape),
                        color = GuruRed,
                        trackColor = if (isDark) GuruDarkSurfaceContainer else GuruProgressTrack
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isDark) Color(0xFF3F1D23) else GuruRedLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${currentIndex + 1}/${mistakes.size}",
                            fontFamily = FontFamily.Default,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            color = GuruRedDark
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = {
                            if (currentIndex < mistakes.size - 1) {
                                currentIndex++
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Mistake",
                            tint = if (isDark) GuruDarkText else GuruTextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (answerState == AnswerState.IDLE && currentQuestion.questionType != "WORD_MATCH") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDark) GuruDarkBg else GuruBg)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    GuruButton(
                        text = "Check Correction",
                        icon = Icons.Default.Check,
                        enabled = hasAnswerInput,
                        onClick = {
                            val userSentence = when (currentQuestion.questionType) {
                                "MCQ" -> selectedMcqOption ?: ""
                                "FILL_BLANK" -> selectedBlankWord ?: ""
                                "SENTENCE_CORRECTION" -> selectedCorrection ?: ""
                                else -> selectedWords.joinToString(" ") { it.word }
                            }
                            lastSubmittedAnswer = userSentence
                            val isCorrect = when (currentQuestion.questionType) {
                                "MCQ", "FILL_BLANK", "SENTENCE_CORRECTION" -> {
                                    userSentence.trim().equals(currentQuestion.primaryAnswer.trim(), ignoreCase = true)
                                }
                                else -> {
                                    checkAnswer(
                                        userSentence = userSentence,
                                        primary = currentQuestion.primaryAnswer,
                                        alternatesJson = currentQuestion.alternateAnswersJson,
                                        gson = gson
                                    )
                                }
                            }

                            if (isCorrect) {
                                answerState = AnswerState.CORRECT
                                celebrationTrigger++
                                SoundEffectManager.playSuccess()
                                if (UserPrefs.autoPlayAudio) {
                                    ttsManager.speak(extractSpeakableEnglish(currentQuestion.primaryAnswer))
                                }
                                UserPrefs.recordAnswerCompleted(context, currentQuestion.lessonNumber)
                                scope.launch {
                                    repository.markMistake(currentQuestion.id, false)
                                }
                            } else {
                                answerState = AnswerState.INCORRECT
                                shakeTrigger++
                                SoundEffectManager.playError()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        height = 54.dp,
                        shape = CircleShape
                    )
                }
            }
        }
    ) { padding ->
        val shakeOffset = rememberShakeOffset(shakeTrigger)

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
                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        val forward = targetState > initialState
                        if (forward) {
                            (slideInHorizontally(tween(250)) { it / 4 } + fadeIn(tween(230))) togetherWith
                                (slideOutHorizontally(tween(190)) { -it / 4 } + fadeOut(tween(170)))
                        } else {
                            (slideInHorizontally(tween(250)) { -it / 4 } + fadeIn(tween(230))) togetherWith
                                (slideOutHorizontally(tween(190)) { it / 4 } + fadeOut(tween(170)))
                        }
                    },
                    label = "mistakePromptSlide"
                ) { targetIdx ->
                    val q = mistakes.getOrNull(targetIdx) ?: currentQuestion
                    val qPromptDisplay = if (q.questionType == "FILL_BLANK" &&
                        q.hindiText.contains("\n") &&
                        q.hindiText.contains("____")
                    ) {
                        q.hindiText.substringBefore("\n").trim()
                    } else {
                        q.hindiText
                    }

                    Column {
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
                            text = qPromptDisplay,
                            fontFamily = FontFamily.Default,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) GuruDarkText else GuruTextDark,
                            lineHeight = 31.sp
                        )

                        if (q.romanText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = q.romanText,
                                fontFamily = FontFamily.Default,
                                fontSize = 14.sp,
                                color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(shakeOffset.roundToInt(), 0) }
                ) {
                when (currentQuestion.questionType) {
                    "WORD_MATCH" -> {
                        WordMatchQuizView(
                            matchPairs = parsedMatchPairs,
                            isDark = isDark,
                            onMatchSuccess = {
                                lastSubmittedAnswer = currentQuestion.primaryAnswer
                                answerState = AnswerState.CORRECT
                                celebrationTrigger++
                                SoundEffectManager.playSuccess()
                                if (UserPrefs.autoPlayAudio) {
                                    ttsManager.speak(extractSpeakableEnglish(currentQuestion.primaryAnswer))
                                }
                                UserPrefs.recordAnswerCompleted(context, currentQuestion.lessonNumber, xpReward = 15)
                                scope.launch {
                                    repository.markMistake(currentQuestion.id, false)
                                }
                            },
                            onMismatch = { userLeft, userRight, _ ->
                                lastSubmittedAnswer = "$userLeft ↔ $userRight"
                                answerState = AnswerState.INCORRECT
                                shakeTrigger++
                                SoundEffectManager.playError()
                            }
                        )
                    }
                    "MCQ" -> {
                        McqQuizView(
                            options = parsedOptions,
                            selectedOption = selectedMcqOption,
                            onOptionSelected = {
                                if (answerState == AnswerState.IDLE) {
                                    selectedMcqOption = it
                                    SoundEffectManager.playClick()
                                }
                            },
                            isDark = isDark
                        )
                    }
                    "FILL_BLANK" -> {
                        val sentenceWithBlank = remember(currentQuestion.id) {
                            val altsList: List<String> = try {
                                gson.fromJson(currentQuestion.alternateAnswersJson, object : TypeToken<List<String>>() {}.type) ?: emptyList()
                            } catch (e: Exception) { emptyList() }
                            val fromAlt = altsList.firstOrNull { it.contains("____") }
                            if (!fromAlt.isNullOrBlank()) {
                                fromAlt
                            } else if (currentQuestion.hindiText.contains("____")) {
                                val afterNl = currentQuestion.hindiText.substringAfter("\n", currentQuestion.hindiText).trim()
                                if (afterNl.startsWith("(") && afterNl.endsWith(")")) {
                                    afterNl.removePrefix("(").removeSuffix(")").trim()
                                } else {
                                    afterNl
                                }
                            } else if (currentQuestion.primaryAnswer.contains(" ")) {
                                val firstW = currentQuestion.primaryAnswer.split(" ")[0]
                                currentQuestion.primaryAnswer.replaceFirst(firstW, "____")
                            } else {
                                "____"
                            }
                        }
                        FillBlankQuizView(
                            sentenceWithBlank = sentenceWithBlank,
                            options = parsedOptions,
                            selectedWord = selectedBlankWord,
                            onWordSelected = {
                                if (answerState == AnswerState.IDLE) {
                                    selectedBlankWord = it
                                    SoundEffectManager.playClick()
                                }
                            },
                            isDark = isDark
                        )
                    }
                    "SENTENCE_CORRECTION" -> {
                        val incorrectSent = remember(currentQuestion.id) {
                            val text = currentQuestion.hindiText
                            if (text.contains("\"")) text.substringAfter("\"").substringBefore("\"") else text
                        }
                        SentenceCorrectionView(
                            incorrectSentence = incorrectSent,
                            options = parsedOptions,
                            selectedOption = selectedCorrection,
                            onOptionSelected = {
                                if (answerState == AnswerState.IDLE) {
                                    selectedCorrection = it
                                    SoundEffectManager.playClick()
                                }
                            },
                            isDark = isDark
                        )
                    }
                    else -> {
                        // Answer Slot Area (Dashed Reference Style)
                        val slotShape = RoundedCornerShape(22.dp)
                        val dashedBorderColor = if (isDark) GuruDarkCardBorder else GuruCardBorderDashed
                        val slotSurfaceBg = if (isDark) GuruDarkSurfaceContainer else GuruSurfaceContainer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 140.dp)
                                .clip(slotShape)
                                .background(slotSurfaceBg)
                                .drawBehind {
                                    drawRoundRect(
                                        color = dashedBorderColor,
                                        style = Stroke(
                                            width = 1.3.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                                        ),
                                        cornerRadius = CornerRadius(22.dp.toPx(), 22.dp.toPx())
                                    )
                                }
                                .padding(16.dp),
                            contentAlignment = Alignment.TopStart
                        ) {
                            if (selectedWords.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(108.dp),
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
                                            key(item.id) {
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
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Word Bank
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            bankWords.forEach { item ->
                                key(item.id) {
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
                        }
                    }
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
                            Spacer(modifier = Modifier.height(10.dp))
                            val analysis = remember(lastSubmittedAnswer, currentQuestion) {
                                MistakeAnalyzer.analyze(lastSubmittedAnswer, currentQuestion.primaryAnswer, currentQuestion.grammarRule)
                            }

                            if (lastSubmittedAnswer.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "आपका उत्तर: ",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GuruRedDark
                                    )
                                    Text(
                                        text = "\"$lastSubmittedAnswer\"",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) Color(0xFFFFB4AB) else Color(0xFFBA1A1A)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0xFF381B21) else Color(0xFFFFEBEB))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = GuruAmber,
                                        modifier = Modifier.size(15.dp).padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = analysis.explanation,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDark) GuruDarkText else GuruTextDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "सही उत्तर: \"${currentQuestion.primaryAnswer}\"",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GuruMintDark
                            )

                            if (currentQuestion.grammarRule.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "📖 नियम: ${currentQuestion.grammarRule}",
                                    fontSize = 11.sp,
                                    color = if (isDark) GuruDarkTextMuted else GuruTextMuted,
                                    lineHeight = 15.sp
                                )
                            }
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

            CelebrationBurstOverlay(
                triggerCount = celebrationTrigger,
                xpText = if (currentQuestion.questionType == "WORD_MATCH") "+15 XP" else "+10 XP"
            )
        }
    }
}
