package com.spokenguru.app.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.spokenguru.app.ui.theme.GuruAmberLight
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruCardBorderDashed
import com.spokenguru.app.ui.theme.GuruDarkBg
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkPillBg
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkSurfaceContainer
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruDarkTextMuted
import com.spokenguru.app.ui.theme.GuruMint
import com.spokenguru.app.ui.theme.GuruMintDark
import com.spokenguru.app.ui.theme.GuruMintLight
import com.spokenguru.app.ui.theme.GuruPillBg
import com.spokenguru.app.ui.theme.GuruProgressTrack
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleBorder
import com.spokenguru.app.ui.theme.GuruPurpleDeep
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruSurfaceContainer
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruRedDark
import com.spokenguru.app.ui.theme.GuruRedLight
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle
import kotlinx.coroutines.launch

enum class TapAudioMode(val label: String, val icon: String, val toastMessage: String) {
    TAP_SOUND("Tap Sound", "🔊", "Tap Sound: ON"),
    TTS("Word TTS", "🗣️", "Word TTS: ON (Reads word on tap)"),
    OFF("Mute", "🔇", "Sound: OFF")
}

data class BankWordItem(
    val id: Int,
    val word: String,
    val isDistractor: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordPickScreen(
    lessonNumber: Int,
    repository: LessonRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val scope = rememberCoroutineScope()
    val gson = remember { Gson() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val ttsManager = remember { TtsManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var answerState by remember { mutableStateOf(AnswerState.IDLE) }
    var showHintDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpInputText by remember { mutableStateOf("") }
    var showResetDialog by remember { mutableStateOf(false) }
    var isLessonCompleted by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var sessionCorrectCount by remember { mutableIntStateOf(0) }
    var sessionAttemptCount by remember { mutableIntStateOf(0) }
    var celebrationTrigger by remember { mutableIntStateOf(0) }
    var shakeTrigger by remember { mutableIntStateOf(0) }

    // Toggle options
    var tapAudioMode by remember { mutableStateOf(TapAudioMode.TAP_SOUND) }
    var hinglishVisible by remember { mutableStateOf(true) }
    var isTypingMode by remember { mutableStateOf(false) }
    var typedAnswerText by remember { mutableStateOf("") }
    var isAnswerRevealed by remember { mutableStateOf(false) }
    var lastSubmittedAnswer by remember { mutableStateOf("") }

    val bankWords = remember { mutableStateListOf<BankWordItem>() }
    val selectedWords = remember { mutableStateListOf<BankWordItem>() }

    // Load questions on launch
    LaunchedEffect(lessonNumber) {
        UserPrefs.setLastOpenedLesson(context, lessonNumber)
        val list = repository.getQuestionsForLessonSync(lessonNumber)
        if (list.isNotEmpty()) {
            questions = list
            val firstUncompleted = list.indexOfFirst { !it.isCompleted }
            currentIndex = if (firstUncompleted != -1) firstUncompleted else 0
        }
    }

    // Set up word bank when question or smartDistractorsEnabled changes
    LaunchedEffect(currentIndex, questions, UserPrefs.smartDistractorsEnabled) {
        if (questions.isNotEmpty() && currentIndex < questions.size) {
            val q = questions[currentIndex]
            selectedWords.clear()
            typedAnswerText = ""
            answerState = AnswerState.IDLE
            isAnswerRevealed = false
            lastSubmittedAnswer = ""

            val correctWords = q.primaryAnswer.split("\\s+".toRegex())
                .map { cleanWordPunctuation(it) }
                .filter { it.isNotBlank() }

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

    if (questions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) GuruDarkBg else GuruBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading Lesson...",
                fontFamily = FontFamily.Default,
                fontSize = 15.sp,
                color = GuruTextSubtle
            )
        }
        return
    }

    val currentQuestion = questions[currentIndex]

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

    fun getTtsSpeakText(q: QuestionEntity): String {
        if (q.questionType == "FILL_BLANK") {
            val altsList: List<String> = try {
                gson.fromJson(q.alternateAnswersJson, object : TypeToken<List<String>>() {}.type) ?: emptyList()
            } catch (e: Exception) { emptyList() }
            val full = altsList.firstOrNull { !it.contains("____") && it.contains(" ") }
            if (!full.isNullOrBlank()) return extractSpeakableEnglish(full)
        }
        return extractSpeakableEnglish(q.primaryAnswer)
    }

    fun doCheckAnswer() {
        val userSentence = when (currentQuestion.questionType) {
            "MCQ" -> selectedMcqOption ?: ""
            "FILL_BLANK" -> selectedBlankWord ?: ""
            "SENTENCE_CORRECTION" -> selectedCorrection ?: ""
            else -> if (isTypingMode) typedAnswerText else selectedWords.joinToString(" ") { it.word }
        }
        if (userSentence.isBlank()) return
        lastSubmittedAnswer = userSentence
        sessionAttemptCount++

        keyboardController?.hide()
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
            sessionCorrectCount++
            celebrationTrigger++
            if (tapAudioMode != TapAudioMode.OFF) SoundEffectManager.playSuccess()
            if (UserPrefs.autoPlayAudio) {
                ttsManager.speak(getTtsSpeakText(currentQuestion))
            }
            UserPrefs.recordAnswerCompleted(context, currentQuestion.lessonNumber)
            scope.launch {
                repository.markQuestionCompleted(currentQuestion.id, currentQuestion.lessonNumber)
                repository.markMistake(currentQuestion.id, false)
            }
        } else {
            answerState = AnswerState.INCORRECT
            shakeTrigger++
            if (tapAudioMode != TapAudioMode.OFF) SoundEffectManager.playError()
            scope.launch {
                repository.markMistake(currentQuestion.id, true)
            }
        }
    }

    // Speech-to-Text: User speaks English answer
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenResults = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = spokenResults?.firstOrNull()?.trim()
            if (!spokenText.isNullOrBlank()) {
                Toast.makeText(context, "\"$spokenText\"", Toast.LENGTH_SHORT).show()

                when (currentQuestion.questionType) {
                    "MCQ" -> {
                        val matchOpt = parsedOptions.firstOrNull {
                            cleanSentence(it) == cleanSentence(spokenText) ||
                                spokenText.contains(it, ignoreCase = true)
                        }
                        if (matchOpt != null) {
                            selectedMcqOption = matchOpt
                            doCheckAnswer()
                        }
                    }
                    "FILL_BLANK" -> {
                        val matchOpt = parsedOptions.firstOrNull {
                            cleanSentence(it) == cleanSentence(spokenText) ||
                                spokenText.contains(it, ignoreCase = true)
                        }
                        if (matchOpt != null) {
                            selectedBlankWord = matchOpt
                            doCheckAnswer()
                        }
                    }
                    "SENTENCE_CORRECTION" -> {
                        val matchOpt = parsedOptions.firstOrNull {
                            cleanSentence(it) == cleanSentence(spokenText)
                        }
                        if (matchOpt != null) {
                            selectedCorrection = matchOpt
                            doCheckAnswer()
                        }
                    }
                    else -> {
                        typedAnswerText = spokenText
                        val spokenTokens = spokenText.split("\\s+".toRegex())
                            .map { cleanWordPunctuation(it) }
                            .filter { it.isNotBlank() }

                        val matchedItems = mutableListOf<BankWordItem>()
                        val usedIds = mutableSetOf<Int>()
                        spokenTokens.forEachIndexed { index, token ->
                            val match = bankWords.firstOrNull {
                                it.id !in usedIds && cleanWordPunctuation(it.word).equals(token, ignoreCase = true)
                            }
                            if (match != null) {
                                matchedItems.add(match)
                                usedIds.add(match.id)
                            } else {
                                matchedItems.add(BankWordItem(id = 3000 + index, word = token))
                            }
                        }
                        selectedWords.clear()
                        selectedWords.addAll(matchedItems)
                        doCheckAnswer()
                    }
                }
            }
        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak English answer...")
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }

    val hasAnswerInput = when (currentQuestion.questionType) {
        "WORD_MATCH" -> false
        "MCQ" -> !selectedMcqOption.isNullOrBlank()
        "FILL_BLANK" -> !selectedBlankWord.isNullOrBlank()
        "SENTENCE_CORRECTION" -> !selectedCorrection.isNullOrBlank()
        else -> if (isTypingMode) typedAnswerText.isNotBlank() else selectedWords.isNotEmpty()
    }
    val progress = (currentIndex + 1).toFloat() / questions.size.toFloat()
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progressAnim")

    Scaffold(
        containerColor = if (isDark) GuruDarkBg else GuruBg,
        topBar = {
            // Reference-style Sleek M3 Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit",
                        tint = if (isDark) GuruDarkText else GuruTextDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Clean Animated Progress Bar with Prev / Pill Badge / Next
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
                            contentDescription = "Previous Question",
                            tint = if (currentIndex > 0) (if (isDark) GuruDarkText else GuruTextDark) else GuruTextSubtle.copy(alpha = 0.28f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(CircleShape),
                        color = GuruPurple,
                        trackColor = if (isDark) GuruDarkSurfaceContainer else GuruProgressTrack
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isDark) GuruDarkPillBg else GuruPurpleLight)
                            .clickable {
                                jumpInputText = (currentIndex + 1).toString()
                                showJumpDialog = true
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${currentIndex + 1}/${questions.size}",
                            fontFamily = FontFamily.Default,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false,
                            color = if (isDark) Color.White else GuruPurpleDeep
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = {
                            if (currentIndex < questions.size - 1) {
                                currentIndex++
                            } else {
                                isLessonCompleted = true
                            }
                        },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Question",
                            tint = if (isDark) GuruDarkText else GuruTextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Sound Toggle Button
                IconButton(
                    onClick = {
                        tapAudioMode = when (tapAudioMode) {
                            TapAudioMode.TAP_SOUND -> TapAudioMode.TTS
                            TapAudioMode.TTS -> TapAudioMode.OFF
                            TapAudioMode.OFF -> TapAudioMode.TAP_SOUND
                        }
                        Toast.makeText(context, tapAudioMode.toastMessage, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = when (tapAudioMode) {
                            TapAudioMode.OFF -> Icons.AutoMirrored.Filled.VolumeMute
                            else -> Icons.AutoMirrored.Filled.VolumeUp
                        },
                        contentDescription = "Toggle Audio",
                        tint = if (tapAudioMode == TapAudioMode.OFF) GuruTextSubtle else (if (isDark) GuruPurple else GuruPurpleDeep),
                        modifier = Modifier.size(21.dp)
                    )
                }

                // Subtle Options Dropdown
                Box {
                    IconButton(
                        onClick = { showOptionsMenu = !showOptionsMenu },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = if (isDark) GuruDarkTextMuted else GuruTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Jump to Question #...") },
                            onClick = {
                                showOptionsMenu = false
                                jumpInputText = (currentIndex + 1).toString()
                                showJumpDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (UserPrefs.autoPlayAudio) "Auto-Speak Answer: ON" else "Auto-Speak Answer: OFF") },
                            onClick = {
                                UserPrefs.updateAutoPlayAudio(context, !UserPrefs.autoPlayAudio)
                                showOptionsMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (hinglishVisible) "Hide Transliteration" else "Show Transliteration") },
                            onClick = {
                                hinglishVisible = !hinglishVisible
                                showOptionsMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (UserPrefs.smartDistractorsEnabled) "Smart Distractors: ON" else "Smart Distractors: OFF") },
                            onClick = {
                                UserPrefs.updateSmartDistractors(context, !UserPrefs.smartDistractorsEnabled)
                                showOptionsMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isTypingMode) "Switch to Word Tiles" else "Switch to Keyboard Typing") },
                            onClick = {
                                isTypingMode = !isTypingMode
                                showOptionsMenu = false
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Reset Lesson Progress", color = GuruRed) },
                            onClick = {
                                showOptionsMenu = false
                                showResetDialog = true
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Seamless Reference-Style Bottom Utility Row + Capsule CTA
            if (answerState == AnswerState.IDLE) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDark) GuruDarkBg else GuruBg)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    if (currentQuestion.questionType == "SENTENCE_BUILDER") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { isTypingMode = !isTypingMode }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isTypingMode) Icons.Default.Translate else Icons.Default.Keyboard,
                                    contentDescription = null,
                                    tint = if (isDark) GuruPurple else GuruPurpleDeep,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTypingMode) "USE TILES" else "TYPE ANSWER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = if (isDark) GuruPurple else GuruPurpleDeep
                                )
                            }

                            Text(
                                text = "SHOW ANSWER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = if (isDark) GuruPurple else GuruPurpleDeep,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        isAnswerRevealed = true
                                        val correctWords = currentQuestion.primaryAnswer.split("\\s+".toRegex())
                                            .map { cleanWordPunctuation(it) }
                                            .filter { it.isNotBlank() }
                                        selectedWords.clear()
                                        correctWords.forEachIndexed { idx, w ->
                                            selectedWords.add(BankWordItem(id = 2000 + idx, word = w))
                                        }
                                        typedAnswerText = currentQuestion.primaryAnswer
                                        ttsManager.speak(getTtsSpeakText(currentQuestion))
                                    }
                                    .padding(vertical = 4.dp, horizontal = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Primary Full-Width Capsule Action Button
                    if (currentQuestion.questionType != "WORD_MATCH") {
                        GuruButton(
                            text = "Check Answer",
                            icon = Icons.Default.Check,
                            variant = GuruButtonVariant.PRIMARY,
                            enabled = hasAnswerInput,
                            onClick = { doCheckAnswer() },
                            modifier = Modifier.fillMaxWidth(),
                            height = 54.dp,
                            shape = CircleShape
                        )
                    }
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
                // Direction-aware smooth question prompt transition
                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        val forward = targetState > initialState
                        if (forward) {
                            (slideInHorizontally(tween(260)) { it / 4 } + fadeIn(tween(240))) togetherWith
                                (slideOutHorizontally(tween(200)) { -it / 4 } + fadeOut(tween(180)))
                        } else {
                            (slideInHorizontally(tween(260)) { -it / 4 } + fadeIn(tween(240))) togetherWith
                                (slideOutHorizontally(tween(200)) { it / 4 } + fadeOut(tween(180)))
                        }
                    },
                    label = "promptSlide"
                ) { targetIdx ->
                    val q = questions.getOrNull(targetIdx) ?: currentQuestion
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
                            text = q.categoryTag.ifBlank { "Lesson Practice" }.uppercase(),
                            fontFamily = FontFamily.Default,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) GuruPurple else GuruPurpleDeep,
                            letterSpacing = 1.2.sp
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

                        if (hinglishVisible && q.romanText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = q.romanText,
                                fontFamily = FontFamily.Default,
                                fontSize = 14.5.sp,
                                color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4-Pill Reference Action Bar (Listen, Slow, Speak, Hint)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MinimalistActionButton(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        label = "Listen",
                        onClick = { ttsManager.speak(getTtsSpeakText(currentQuestion), isSlow = false) }
                    )

                    MinimalistActionButton(
                        icon = Icons.Default.Speed,
                        label = "Slow",
                        onClick = { ttsManager.speak(getTtsSpeakText(currentQuestion), isSlow = true) }
                    )

                    MinimalistActionButton(
                        icon = Icons.Default.Mic,
                        label = "Speak",
                        onClick = { startListening() }
                    )

                    MinimalistActionButton(
                        icon = Icons.Outlined.Lightbulb,
                        label = "Hint",
                        onClick = { showHintDialog = true }
                    )
                }

                // Smooth expandable Solution Reveal Banner
                AnimatedVisibility(
                    visible = isAnswerRevealed,
                    enter = expandVertically(tween(220)) + fadeIn(tween(220)),
                    exit = shrinkVertically(tween(180)) + fadeOut(tween(160))
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDark) Color(0xFF142B20) else GuruMintLight)
                                .border(1.dp, GuruMint.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "SOLUTION",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GuruMintDark,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = currentQuestion.primaryAnswer,
                                        fontFamily = FontFamily.Default,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        color = GuruMintDark
                                    )
                                }
                                IconButton(
                                    onClick = { ttsManager.speak(getTtsSpeakText(currentQuestion)) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Listen",
                                        tint = GuruMintDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Interactive exercise body with tactile error shake
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
                            onPairMatched = { left, right ->
                                if (tapAudioMode == TapAudioMode.TTS) {
                                    ttsManager.speak(extractSpeakableEnglish("$left, $right"))
                                }
                            },
                            onMatchSuccess = {
                                lastSubmittedAnswer = currentQuestion.primaryAnswer
                                answerState = AnswerState.CORRECT
                                sessionAttemptCount++
                                sessionCorrectCount++
                                celebrationTrigger++
                                if (tapAudioMode != TapAudioMode.OFF) SoundEffectManager.playSuccess()
                                if (UserPrefs.autoPlayAudio) {
                                    ttsManager.speak(getTtsSpeakText(currentQuestion))
                                }
                                UserPrefs.recordAnswerCompleted(context, currentQuestion.lessonNumber, xpReward = 15)
                                scope.launch {
                                    repository.markQuestionCompleted(currentQuestion.id, currentQuestion.lessonNumber)
                                    repository.markMistake(currentQuestion.id, false)
                                }
                            },
                            onMismatch = { userLeft, userRight, _ ->
                                lastSubmittedAnswer = "$userLeft ↔ $userRight"
                                answerState = AnswerState.INCORRECT
                                sessionAttemptCount++
                                shakeTrigger++
                                if (tapAudioMode != TapAudioMode.OFF) SoundEffectManager.playError()
                                scope.launch {
                                    repository.markMistake(currentQuestion.id, true)
                                }
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
                                    if (tapAudioMode == TapAudioMode.TTS) {
                                        ttsManager.speak(it)
                                    } else if (tapAudioMode == TapAudioMode.TAP_SOUND) {
                                        SoundEffectManager.playClick()
                                    }
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
                                    if (tapAudioMode == TapAudioMode.TTS) {
                                        ttsManager.speak(it)
                                    } else if (tapAudioMode == TapAudioMode.TAP_SOUND) {
                                        SoundEffectManager.playClick()
                                    }
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
                                    if (tapAudioMode != TapAudioMode.OFF) SoundEffectManager.playClick()
                                }
                            },
                            isDark = isDark
                        )
                    }
                    else -> {
                        // DASHED REFERENCE ANSWER BUILDER BOX
                        val slotShape = RoundedCornerShape(22.dp)
                        val dashedBorderColor = if (isDark) GuruDarkCardBorder else GuruCardBorderDashed
                        val slotSurfaceBg = if (isDark) GuruDarkSurfaceContainer else GuruSurfaceContainer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 154.dp)
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
                            if (isTypingMode) {
                                OutlinedTextField(
                                    value = typedAnswerText,
                                    onValueChange = { typedAnswerText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    placeholder = {
                                        Text(
                                            text = "Type English translation here...",
                                            fontSize = 15.sp,
                                            color = GuruTextSubtle
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { doCheckAnswer() }),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GuruPurple,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontFamily = FontFamily.Default,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDark) GuruDarkText else GuruTextDark
                                    )
                                )
                            } else {
                                if (selectedWords.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(122.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Tap words below to build sentence",
                                            fontFamily = FontFamily.Default,
                                            fontSize = 14.sp,
                                            color = GuruTextSubtle
                                        )
                                    }
                                } else {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        // Subtle Undo / Clear top-right controls
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
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
                                                                when (tapAudioMode) {
                                                                    TapAudioMode.TAP_SOUND -> SoundEffectManager.playClick()
                                                                    TapAudioMode.TTS -> ttsManager.speak(item.word)
                                                                    TapAudioMode.OFF -> {}
                                                                }
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
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // WORD BANK GRID (Minimalist, Spacious)
                        if (!isTypingMode) {
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
                                                    when (tapAudioMode) {
                                                        TapAudioMode.TAP_SOUND -> SoundEffectManager.playClick()
                                                        TapAudioMode.TTS -> ttsManager.speak(item.word)
                                                        TapAudioMode.OFF -> {}
                                                    }
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
                }

                Spacer(modifier = Modifier.height(30.dp))
            }

            // FEEDBACK SLIDE-UP BOTTOM SHEET (Clean, Modern, Uncluttered)
            AnimatedVisibility(
                visible = answerState != AnswerState.IDLE,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                if (answerState == AnswerState.CORRECT) {
                    MinimalCorrectFeedbackCard(
                        currentQuestion = currentQuestion,
                        currentIndex = currentIndex,
                        totalQuestions = questions.size,
                        onNext = {
                            if (currentIndex < questions.size - 1) {
                                currentIndex++
                            } else {
                                isLessonCompleted = true
                            }
                        }
                    )
                } else if (answerState == AnswerState.INCORRECT) {
                    MinimalIncorrectFeedbackCard(
                        currentQuestion = currentQuestion,
                        userAnswer = lastSubmittedAnswer,
                        ttsManager = ttsManager,
                        onTryAgain = {
                            answerState = AnswerState.IDLE
                        },
                        onNext = {
                            if (currentIndex < questions.size - 1) {
                                currentIndex++
                            } else {
                                isLessonCompleted = true
                            }
                        }
                    )
                }
            }

            // Celebration confetti + floating XP burst
            CelebrationBurstOverlay(
                triggerCount = celebrationTrigger,
                xpText = if (currentQuestion.questionType == "WORD_MATCH") "+15 XP" else "+10 XP"
            )
        }
    }

    // Hint Dialog
    if (showHintDialog) {
        AlertDialog(
            onDismissRequest = { showHintDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = GuruAmber
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentQuestion.ruleTitle.ifBlank { "Grammar Hint" },
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = currentQuestion.hint.ifBlank { "Notice singular vs plural and near vs far." },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (currentQuestion.grammarRule.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Rule: ${currentQuestion.grammarRule}",
                            fontSize = 13.sp,
                            color = GuruPurple
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHintDialog = false }) {
                    Text(text = "Got it", fontWeight = FontWeight.Bold, color = GuruPurple)
                }
            }
        )
    }

    // Exit Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = "Leave lesson?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Your progress has been saved. You can resume anytime.") },
            confirmButton = {
                TextButton(onClick = { showExitDialog = false; onNavigateBack() }) {
                    Text(text = "Exit", color = GuruRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(text = "Keep Practicing", color = GuruPurple, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Jump to Question # Dialog
    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = { Text(text = "Jump to Question", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Enter question number (1 to ${questions.size}):",
                        fontSize = 13.sp,
                        color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = jumpInputText,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) {
                                jumpInputText = input
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Go
                        ),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                val target = jumpInputText.toIntOrNull()
                                if (target != null && target in 1..questions.size) {
                                    currentIndex = target - 1
                                    showJumpDialog = false
                                }
                            }
                        ),
                        placeholder = { Text("1 - ${questions.size}") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val target = jumpInputText.toIntOrNull()
                        if (target != null && target in 1..questions.size) {
                            currentIndex = target - 1
                            showJumpDialog = false
                        } else {
                            Toast.makeText(context, "Enter number between 1 and ${questions.size}", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Go", fontWeight = FontWeight.Bold, color = GuruPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("Cancel", color = GuruTextMuted)
                }
            }
        )
    }

    // Reset Lesson Progress Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = "Reset Lesson Progress?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "This will clear completed flags for this lesson so you can practice all questions fresh from #1.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        scope.launch {
                            repository.resetLesson(lessonNumber)
                            questions = repository.getQuestionsForLessonSync(lessonNumber)
                            currentIndex = 0
                            sessionCorrectCount = 0
                            sessionAttemptCount = 0
                        }
                        Toast.makeText(context, "Lesson progress reset", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Reset", color = GuruRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = GuruPurple, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Completion Summary Dialog
    if (isLessonCompleted) {
        val accuracy = if (sessionAttemptCount > 0) {
            ((sessionCorrectCount * 100f) / sessionAttemptCount).toInt()
        } else 100
        val xpEarned = sessionCorrectCount * 10
        AlertDialog(
            onDismissRequest = { onNavigateBack() },
            title = { Text(text = "🎉 Lesson Completed!", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "शानदार! आपने इस लेसन का अभ्यास पूरा कर लिया।")
                    if (sessionAttemptCount > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF1E293B) else GuruPurpleLight.copy(alpha = 0.6f))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Accuracy: $accuracy% ($sessionCorrectCount/$sessionAttemptCount)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GuruPurple
                                )
                                Text(
                                    text = "XP Earned: +$xpEarned XP • Streak: ${UserPrefs.streakDays} Days",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GuruMintDark
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                GuruButton(
                    text = "Back to Lessons",
                    onClick = onNavigateBack,
                    height = 46.dp
                )
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isLessonCompleted = false
                        currentIndex = 0
                    }
                ) {
                    Text("Restart Q1", color = GuruPurple, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

/**
 * Minimalist Action Pill Button
 */
@Composable
private fun MinimalistActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = GuruPurpleDeep
) {
    val isDark = isAppDarkTheme()
    val shape = CircleShape
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "actionPillScale"
    )
    val resolvedTint = if (isDark && tint == GuruPurpleDeep) GuruPurple else tint

    Row(
        modifier = Modifier
            .scale(scale)
            .clip(shape)
            .background(if (isDark) GuruDarkPillBg else GuruPillBg)
            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = resolvedTint,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontFamily = FontFamily.Default,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
            color = if (isDark) GuruDarkText else GuruTextDark
        )
    }
}

/**
 * Minimalist Correct Feedback Card
 */
@Composable
fun MinimalCorrectFeedbackCard(
    currentQuestion: QuestionEntity,
    currentIndex: Int,
    totalQuestions: Int,
    onNext: () -> Unit
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, shape)
            .clip(shape)
            .background(if (isDark) Color(0xFF10281D) else GuruMintLight)
            .border(1.dp, GuruMint.copy(alpha = 0.3f), shape)
            .padding(20.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = GuruMintDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Great job! (बिल्कुल सही)",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = GuruMintDark
                    )
                    Text(
                        text = "\"${currentQuestion.primaryAnswer}\"",
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                }
            }

            if (currentQuestion.grammarRule.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                if (currentQuestion.ruleTitle.isNotBlank()) {
                    Text(
                        text = "📖 ${currentQuestion.ruleTitle}",
                        fontFamily = FontFamily.Default,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuruMintDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = currentQuestion.grammarRule,
                    fontFamily = FontFamily.Default,
                    fontSize = 12.sp,
                    color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            GuruButton(
                text = "Continue",
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                variant = GuruButtonVariant.PRIMARY,
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                height = 48.dp
            )
        }
    }
}

/**
 * Minimalist Incorrect Feedback Card with Detailed Error Analysis & Rules
 */
@Composable
fun MinimalIncorrectFeedbackCard(
    currentQuestion: QuestionEntity,
    userAnswer: String,
    ttsManager: TtsManager,
    onTryAgain: () -> Unit,
    onNext: () -> Unit
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val analysis = remember(userAnswer, currentQuestion) {
        MistakeAnalyzer.analyze(userAnswer, currentQuestion.primaryAnswer, currentQuestion.grammarRule)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, shape)
            .clip(shape)
            .background(if (isDark) Color(0xFF2C1318) else GuruRedLight)
            .border(1.dp, GuruRed.copy(alpha = 0.3f), shape)
            .padding(18.dp)
    ) {
        Column {
            // Header: Icon + Title + Audio Listen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = GuruRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Not quite right (गलत उत्तर)",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GuruRedDark
                    )
                }

                IconButton(
                    onClick = { ttsManager.speak(extractSpeakableEnglish(currentQuestion.primaryAnswer)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        tint = GuruRedDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // User Answer Section (आपकी पसंद)
            if (userAnswer.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "आपका उत्तर: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuruRedDark
                    )
                    Text(
                        text = "\"$userAnswer\"",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFFFB4AB) else Color(0xFFBA1A1A)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Mistake Explanation Box (क्या गलती हुई)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) Color(0xFF381B21) else Color(0xFFFFEBEB))
                    .border(1.dp, GuruRed.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = GuruAmber,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = analysis.explanation,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) GuruDarkText else GuruTextDark,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Correct Solution (सही उत्तर क्या होना चाहिए)
            Column {
                Text(
                    text = "सही उत्तर (Correct Solution):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GuruMintDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "\"${currentQuestion.primaryAnswer}\"",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isDark) GuruDarkText else GuruTextDark
                )
            }

            // Grammar Rule & Explanation Box (नियम व कारण)
            if (currentQuestion.grammarRule.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isDark) Color(0xFF1F1D36) else GuruPurpleLight)
                        .border(1.dp, GuruPurple.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📖 ", fontSize = 12.sp)
                            Text(
                                text = currentQuestion.ruleTitle.ifBlank { "Guru Grammar Rule" },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GuruPurple
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentQuestion.grammarRule,
                            fontFamily = FontFamily.Default,
                            fontSize = 11.sp,
                            color = if (isDark) GuruDarkTextMuted else GuruTextDark,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Buttons: Try Again & Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GuruButton(
                    text = "Try Again",
                    icon = Icons.Default.Refresh,
                    variant = GuruButtonVariant.SECONDARY,
                    onClick = onTryAgain,
                    modifier = Modifier.weight(1f),
                    height = 46.dp
                )

                GuruButton(
                    text = "Next",
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    variant = GuruButtonVariant.PRIMARY,
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    height = 46.dp
                )
            }
        }
    }
}
