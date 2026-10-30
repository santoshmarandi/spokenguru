package com.spokenguru.app.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
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
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.spokenguru.app.ui.theme.GuruAmber
import com.spokenguru.app.ui.theme.GuruAmberLight
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruCardBorderDashed
import com.spokenguru.app.ui.theme.GuruDarkBg
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruDarkTextMuted
import com.spokenguru.app.ui.theme.GuruMint
import com.spokenguru.app.ui.theme.GuruMintDark
import com.spokenguru.app.ui.theme.GuruMintLight
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleBorder
import com.spokenguru.app.ui.theme.GuruPurpleLight
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
    var isLessonCompleted by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }

    // Toggle options
    var tapAudioMode by remember { mutableStateOf(TapAudioMode.TAP_SOUND) }
    var hinglishVisible by remember { mutableStateOf(true) }
    var extraDistractorsEnabled by remember { mutableStateOf(false) }
    var isTypingMode by remember { mutableStateOf(false) }
    var typedAnswerText by remember { mutableStateOf("") }
    var isAnswerRevealed by remember { mutableStateOf(false) }

    val bankWords = remember { mutableStateListOf<BankWordItem>() }
    val selectedWords = remember { mutableStateListOf<BankWordItem>() }

    // Load questions on launch
    LaunchedEffect(lessonNumber) {
        val list = repository.getQuestionsForLessonSync(lessonNumber)
        if (list.isNotEmpty()) {
            questions = list
            val firstUncompleted = list.indexOfFirst { !it.isCompleted }
            currentIndex = if (firstUncompleted != -1) firstUncompleted else 0
        }
    }

    // Set up word bank when question or extraDistractorsEnabled changes
    LaunchedEffect(currentIndex, questions, extraDistractorsEnabled) {
        if (questions.isNotEmpty() && currentIndex < questions.size) {
            val q = questions[currentIndex]
            selectedWords.clear()
            typedAnswerText = ""
            answerState = AnswerState.IDLE
            isAnswerRevealed = false

            val correctWords = q.primaryAnswer.split("\\s+".toRegex())
                .map { cleanWordPunctuation(it) }
                .filter { it.isNotBlank() }

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
            if (extraDistractorsEnabled) {
                distractors.take(2).forEachIndexed { idx, w ->
                    list.add(BankWordItem(id = 100 + idx, word = w, isDistractor = true))
                }
            }

            bankWords.clear()
            bankWords.addAll(list.shuffled())
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

    fun doCheckAnswer() {
        val userSentence = if (isTypingMode) typedAnswerText else selectedWords.joinToString(" ") { it.word }
        if (userSentence.isBlank()) return

        keyboardController?.hide()
        val isCorrect = checkAnswer(
            userSentence = userSentence,
            primary = currentQuestion.primaryAnswer,
            alternatesJson = currentQuestion.alternateAnswersJson,
            gson = gson
        )

        if (isCorrect) {
            answerState = AnswerState.CORRECT
            if (tapAudioMode != TapAudioMode.OFF) SoundEffectManager.playSuccess()
            ttsManager.speak(currentQuestion.primaryAnswer)
            scope.launch {
                repository.markQuestionCompleted(currentQuestion.id, currentQuestion.lessonNumber)
                repository.markMistake(currentQuestion.id, false)
            }
        } else {
            answerState = AnswerState.INCORRECT
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

    val hasAnswerInput = if (isTypingMode) typedAnswerText.isNotBlank() else selectedWords.isNotEmpty()
    val progress = (currentIndex + 1).toFloat() / questions.size.toFloat()
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progressAnim")

    Scaffold(
        containerColor = if (isDark) GuruDarkBg else GuruBg,
        topBar = {
            // Minimalist Sleek Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit",
                        tint = if (isDark) GuruDarkText else GuruTextDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Clean Animated Progress Bar
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = GuruPurple,
                        trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE5E7EB)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${currentIndex + 1}/${questions.size}",
                        fontFamily = FontFamily.Default,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                    )
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
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = when (tapAudioMode) {
                            TapAudioMode.OFF -> Icons.Default.VolumeMute
                            else -> Icons.AutoMirrored.Filled.VolumeUp
                        },
                        contentDescription = "Toggle Audio",
                        tint = if (tapAudioMode == TapAudioMode.OFF) GuruTextSubtle else GuruPurple,
                        modifier = Modifier.size(20.dp)
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
                            text = { Text(if (hinglishVisible) "Hide Transliteration" else "Show Transliteration") },
                            onClick = {
                                hinglishVisible = !hinglishVisible
                                showOptionsMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (extraDistractorsEnabled) "Less Distractor Tiles" else "More Distractor Tiles") },
                            onClick = {
                                extraDistractorsEnabled = !extraDistractorsEnabled
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
                    }
                }
            }
        },
        bottomBar = {
            // Fixed Minimalist Sticky Bottom Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) GuruDarkSurface else Color.White)
            ) {
                HorizontalDivider(
                    thickness = 0.8.dp,
                    color = (if (isDark) GuruDarkCardBorder else GuruCardBorder).copy(alpha = 0.6f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    // Secondary Controls: Mode Switcher & Show Answer
                    if (answerState == AnswerState.IDLE) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Mode toggle button
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { isTypingMode = !isTypingMode }
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isTypingMode) Icons.Default.Translate else Icons.Default.Keyboard,
                                    contentDescription = null,
                                    tint = GuruPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTypingMode) "Use Tiles" else "Type Answer",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GuruPurple
                                )
                            }

                            // Show Answer text link (subtle, non-intrusive)
                            Text(
                                text = "Show Answer",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) GuruDarkTextMuted else GuruTextMuted,
                                modifier = Modifier
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
                                        ttsManager.speak(currentQuestion.primaryAnswer)
                                    }
                                    .padding(vertical = 4.dp, horizontal = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Primary Action Button
                        GuruButton(
                            text = "Check Answer",
                            icon = Icons.Default.Check,
                            variant = GuruButtonVariant.PRIMARY,
                            enabled = hasAnswerInput,
                            onClick = { doCheckAnswer() },
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
                // Subtle Category Label
                Text(
                    text = currentQuestion.categoryTag.ifBlank { "Lesson Practice" }.uppercase(),
                    fontFamily = FontFamily.Default,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GuruPurple,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // The Hindi Prompt (Clean, Large, High Readability)
                Text(
                    text = currentQuestion.hindiText,
                    fontFamily = FontFamily.Default,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) GuruDarkText else GuruTextDark,
                    lineHeight = 32.sp
                )

                // Optional Transliteration
                if (hinglishVisible && currentQuestion.romanText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentQuestion.romanText,
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp,
                        color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sleek Minimalist Audio & Help Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MinimalistActionButton(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        label = "Listen",
                        onClick = { ttsManager.speak(currentQuestion.primaryAnswer, isSlow = false) }
                    )

                    MinimalistActionButton(
                        icon = Icons.Default.Pets,
                        label = "Slow",
                        onClick = { ttsManager.speak(currentQuestion.primaryAnswer, isSlow = true) }
                    )

                    MinimalistActionButton(
                        icon = Icons.Default.Mic,
                        label = "Speak",
                        onClick = { startListening() }
                    )

                    MinimalistActionButton(
                        icon = Icons.Default.Lightbulb,
                        label = "Hint",
                        tint = GuruAmber,
                        onClick = { showHintDialog = true }
                    )
                }

                // Correct Answer Revealed Banner
                if (isAnswerRevealed) {
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
                                onClick = { ttsManager.speak(currentQuestion.primaryAnswer) },
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

                Spacer(modifier = Modifier.height(20.dp))

                // ANSWER PLACEMENT AREA
                val slotShape = RoundedCornerShape(16.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 110.dp)
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
                    if (isTypingMode) {
                        OutlinedTextField(
                            value = typedAnswerText,
                            onValueChange = { typedAnswerText = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
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
                                    .height(80.dp),
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

                Spacer(modifier = Modifier.height(24.dp))

                // WORD BANK GRID (Minimalist, Spacious)
                if (!isTypingMode) {
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
                    Text(text = "Grammar Hint", fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold)
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

    // Completion Dialog
    if (isLessonCompleted) {
        AlertDialog(
            onDismissRequest = { onNavigateBack() },
            title = { Text(text = "🎉 Lesson Completed!", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Great job! You have successfully completed all practice questions in this lesson.") },
            confirmButton = {
                GuruButton(
                    text = "Back to Lessons",
                    onClick = onNavigateBack,
                    height = 46.dp
                )
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
    tint: Color = GuruPurple
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = Modifier
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontFamily = FontFamily.Default,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
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
 * Minimalist Incorrect Feedback Card
 */
@Composable
fun MinimalIncorrectFeedbackCard(
    currentQuestion: QuestionEntity,
    ttsManager: TtsManager,
    onTryAgain: () -> Unit,
    onNext: () -> Unit
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, shape)
            .clip(shape)
            .background(if (isDark) Color(0xFF2C1318) else GuruRedLight)
            .border(1.dp, GuruRed.copy(alpha = 0.3f), shape)
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = GuruRed,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Correct solution:",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GuruRedDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentQuestion.primaryAnswer,
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                    }
                }

                IconButton(
                    onClick = { ttsManager.speak(currentQuestion.primaryAnswer) },
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

            Spacer(modifier = Modifier.height(16.dp))

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
                    height = 48.dp
                )

                GuruButton(
                    text = "Next",
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    variant = GuruButtonVariant.PRIMARY,
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    height = 48.dp
                )
            }
        }
    }
}
