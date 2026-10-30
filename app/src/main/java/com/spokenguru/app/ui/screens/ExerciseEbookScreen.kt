package com.spokenguru.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.spokenguru.app.ui.theme.isAppDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.spokenguru.app.audio.TtsManager
import com.spokenguru.app.data.model.LessonEntity
import com.spokenguru.app.data.model.QuestionEntity
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.AppBottomNavigation
import com.spokenguru.app.ui.components.GuruButton
import com.spokenguru.app.ui.components.NavTab
import com.spokenguru.app.ui.theme.GuruAmber
import com.spokenguru.app.ui.theme.GuruAmberLight
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruBlue
import com.spokenguru.app.ui.theme.GuruBlueLight
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
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle

@Composable
fun ExerciseEbookScreen(
    repository: LessonRepository,
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onStartPractice: (Int) -> Unit
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val ttsManager = remember { TtsManager(context) }
    DisposableEffect(Unit) {
        onDispose { ttsManager.shutdown() }
    }

    val lessons by repository.allLessons.collectAsState(initial = emptyList())
    var selectedLesson by remember { mutableStateOf<LessonEntity?>(null) }

    if (selectedLesson == null) {
        // VIEW 1: EBOOK LESSONS / CHAPTERS LIST (matching unnamed (6).png)
        EbookChaptersListView(
            lessons = lessons,
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            onSelectLesson = { lesson -> selectedLesson = lesson }
        )
    } else {
        // VIEW 2: LESSON EXERCISE READER (matching unnamed (5).png)
        // With global top toggle (all show/hide) and individual item show/hide
        LessonExerciseReaderView(
            lesson = selectedLesson!!,
            repository = repository,
            ttsManager = ttsManager,
            currentTab = currentTab,
            onTabSelected = onTabSelected,
            onBackToChapters = { selectedLesson = null },
            onStartPractice = { lessonNumber -> onStartPractice(lessonNumber) }
        )
    }
}

/**
 * View 1: List of all 40 Lessons in Exercise eBook
 */
@Composable
fun EbookChaptersListView(
    lessons: List<LessonEntity>,
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onSelectLesson: (LessonEntity) -> Unit
) {
    val isDark = isAppDarkTheme()

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = if (isDark) GuruDarkBg else GuruBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header: Exercise eBook
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(GuruPurpleLight)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ई-बुक पठन व अभ्यास संग्रह • Reader Edition",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuruPurple
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Exercise eBook",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                    Text(
                        text = "Read & Memorize Sentences • अध्ययन एवं स्मरण",
                        fontSize = 13.sp,
                        color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                    )
                }
            }

            // Hero Card: Spoken English Guru eBook — Reading Edition (from unnamed (6).png)
            item {
                val heroShape = RoundedCornerShape(24.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, heroShape, ambientColor = GuruPurple.copy(alpha = 0.08f))
                        .clip(heroShape)
                        .background(if (isDark) GuruDarkSurface else Color.White)
                        .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, heroShape)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GuruMintLight)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "• Complete Sentence Bank",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GuruMint
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GuruPurpleLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = GuruPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Spoken English Guru eBook — Reading Edition",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "40 Chapters • 101 Bilingual Sentences in Lesson 1",
                            fontSize = 12.sp,
                            color = GuruTextMuted
                        )
                    }
                }
            }

            // Memorize Mode Tip Card
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isDark) Color(0xFF1E1D38) else Color(0xFFF6F4FF))
                        .border(1.dp, GuruPurpleBorder, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(GuruPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = GuruPurple,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Memorize Mode Tip",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GuruPurple
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pehle kisi bhi lesson par tap karein, fir sentences ko ek-ek karke ya sabhi ko ek sath hide/show karke practice karein.",
                                fontSize = 12.sp,
                                color = if (isDark) GuruDarkText else GuruTextDark,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Section: Curriculum Chapters
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Curriculum Chapters",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                    Text(
                        text = "Table of Contents",
                        fontSize = 12.sp,
                        color = GuruTextSubtle
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // All 40 Chapters List
            itemsIndexed(lessons) { index, lesson ->
                val cardShape = RoundedCornerShape(22.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .shadow(2.dp, cardShape, ambientColor = GuruPurple.copy(alpha = 0.08f))
                        .clip(cardShape)
                        .background(if (isDark) GuruDarkSurface else Color.White)
                        .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                        .clickable(enabled = lesson.isAvailable) { onSelectLesson(lesson) }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GuruPurpleLight)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Lesson ${String.format("%02d", lesson.lessonNumber)} • Page ${lesson.pageNumber}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GuruPurple
                                    )
                                }
                                if (lesson.isAvailable) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GuruMintLight)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "${lesson.totalQuestions} Sentences",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GuruMintDark
                                        )
                                    }
                                }
                            }

                            Icon(
                                imageVector = if (lesson.isAvailable) Icons.Default.BookmarkBorder else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (lesson.isAvailable) GuruPurple else GuruTextSubtle,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = lesson.title,
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (lesson.isAvailable) "दैनिक बोलचाल के वाक्य व नियम • Quick Memorize Mode" else "eBook Chapter • Content coming soon",
                            fontSize = 12.sp,
                            color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lesson.isAvailable) "📖 Read & Memorize" else "Coming Soon",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (lesson.isAvailable) GuruMintDark else GuruTextSubtle
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (lesson.isAvailable) GuruPurpleLight else (if (isDark) Color(0xFF1B1A2C) else Color(0xFFF3F3F7)))
                                    .clickable(enabled = lesson.isAvailable) { onSelectLesson(lesson) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (lesson.isAvailable) "Read Lesson 📖" else "Soon",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (lesson.isAvailable) GuruPurple else GuruTextSubtle
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * View 2: Exercise Reader for a selected Lesson (matching unnamed (5).png)
 * Supports both:
 * 1. Global top button: Hide All / Show All
 * 2. Individual sentence button: Hide / Show single answer
 */
@Composable
fun LessonExerciseReaderView(
    lesson: LessonEntity,
    repository: LessonRepository,
    ttsManager: TtsManager,
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onBackToChapters: () -> Unit,
    onStartPractice: (Int) -> Unit
) {
    val isDark = isAppDarkTheme()
    var questions by remember { mutableStateOf<List<QuestionEntity>>(emptyList()) }

    // Global state: whether answers are hidden or shown by default
    var allAnswersHidden by remember { mutableStateOf(false) }

    // Map storing individual overrides for each sentence (true = revealed, false = hidden)
    val individualVisibility = remember { mutableStateMapOf<Long, Boolean>() }

    LaunchedEffect(lesson.lessonNumber) {
        questions = repository.getQuestionsForLessonSync(lesson.lessonNumber)
        individualVisibility.clear()
        allAnswersHidden = false
    }

    // Helper to determine if a specific sentence answer is revealed
    fun isSentenceRevealed(qId: Long): Boolean {
        return individualVisibility[qId] ?: (!allAnswersHidden)
    }

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = if (isDark) GuruDarkBg else GuruBg
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            // Top Navigation Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .shadow(1.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (isDark) GuruDarkSurface else Color.White)
                            .size(40.dp)
                            .clickable { onBackToChapters() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Chapters",
                            tint = if (isDark) GuruDarkText else GuruTextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Lesson ${String.format("%02d", lesson.lessonNumber)} • p. ${lesson.pageNumber}/686",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuruPurple
                        )
                        Text(
                            text = lesson.title,
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .shadow(1.dp, CircleShape)
                            .clip(CircleShape)
                            .background(if (isDark) GuruDarkSurface else Color.White)
                            .size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isDark) GuruDarkText else GuruTextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Reading Stream Card Header
            item {
                val headerShape = RoundedCornerShape(24.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, headerShape)
                        .clip(headerShape)
                        .background(if (isDark) GuruDarkSurface else Color.White)
                        .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, headerShape)
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GuruBlueLight)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "• eBook Reading Stream",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GuruBlue
                                )
                            }
                            Text(
                                text = "${questions.size} Sentences",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GuruPurple
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Lesson ${String.format("%02d", lesson.lessonNumber)}: ${lesson.title}",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "High-frequency conversational drills from eBook 1.",
                            fontSize = 12.sp,
                            color = GuruTextMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Controls Row: Listen Page + GLOBAL HIDE/SHOW ALL TOGGLE BUTTON!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Listen Page
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isDark) Color(0xFF262347) else GuruPurpleLight)
                                    .clickable {
                                        if (questions.isNotEmpty()) {
                                            ttsManager.speak(questions.first().primaryAnswer)
                                        }
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = GuruPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Listen 1.0x",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GuruPurple
                                    )
                                }
                            }

                            // GLOBAL TOGGLE (Upper wala click karne par sab ek sath show/hide)
                            Box(
                                modifier = Modifier
                                    .weight(1.3f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (allAnswersHidden) (if (isDark) Color(0xFF142B20) else GuruMintLight)
                                        else (if (isDark) Color(0xFF2E1922) else Color(0xFFFFF0F2))
                                    )
                                    .border(
                                        1.dp,
                                        if (allAnswersHidden) GuruMint else Color(0xFFFFAEB9),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        // Toggle global state and clear individual overrides
                                        allAnswersHidden = !allAnswersHidden
                                        individualVisibility.clear()
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (allAnswersHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = if (allAnswersHidden) GuruMintDark else Color(0xFFBE123C),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (allAnswersHidden) "Show All Answers 👁" else "Hide All Answers 👁",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (allAnswersHidden) GuruMintDark else Color(0xFFBE123C)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Self-Testing Mode Banner
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xFF161529) else Color(0xFFFBFBFE))
                        .border(1.dp, GuruCardBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Self-Testing Mode: English answers ko hide karke pehle khud translate karein, fir tap karke verify karein.",
                            fontSize = 11.sp,
                            color = if (isDark) GuruDarkTextMuted else GuruTextMuted,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Section Header
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXERCISE SENTENCES (1 – ${questions.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuruPurple,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (allAnswersHidden) "All Hidden" else "All Visible",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (allAnswersHidden) Color(0xFFBE123C) else GuruMintDark
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Sentence Cards with individual Show/Hide toggle
            itemsIndexed(questions) { index, q ->
                val isRevealed = isSentenceRevealed(q.id)
                val cardShape = RoundedCornerShape(22.dp)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .shadow(2.dp, cardShape, ambientColor = GuruPurple.copy(alpha = 0.08f))
                        .clip(cardShape)
                        .background(if (isDark) GuruDarkSurface else Color.White)
                        .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                        .padding(16.dp)
                ) {
                    Column {
                        // Top row of card: Number tag, individual eye toggle, audio speaker
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GuruPurpleLight)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "#${q.questionNumber}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GuruPurple
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = q.tagKeyword.ifBlank { "Demonstratives" },
                                    fontSize = 12.sp,
                                    color = GuruTextMuted
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // INDIVIDUAL TOGGLE BUTTON (Ek-ek karke show/hide)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isRevealed) (if (isDark) Color(0xFF262347) else GuruPurpleLight) else GuruMintLight)
                                        .clickable {
                                            individualVisibility[q.id] = !isRevealed
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (isRevealed) "Hide" else "Reveal",
                                            tint = if (isRevealed) GuruPurple else GuruMintDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isRevealed) "Hide" else "Show",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isRevealed) GuruPurple else GuruMintDark
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                IconButton(
                                    onClick = { ttsManager.speak(q.primaryAnswer) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Listen",
                                        tint = GuruPurple,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Target Hindi Sentence
                        Text(
                            text = q.hindiText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        if (q.romanText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "(${q.romanText})",
                                fontSize = 13.sp,
                                color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // English Answer: Revealed vs Hidden
                        if (isRevealed) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0xFF1B1936) else Color(0xFFF6F4FF))
                                    .border(1.dp, GuruPurpleBorder, RoundedCornerShape(14.dp))
                                    .clickable { individualVisibility[q.id] = false }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = q.primaryAnswer,
                                        fontFamily = FontFamily.Default,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = GuruPurple
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Tap to hide • उत्तर छुपाएं",
                                        fontSize = 10.sp,
                                        color = GuruTextSubtle
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0xFF131222) else Color(0xFFFAF9FF))
                                    .border(1.dp, GuruCardBorderDashed, RoundedCornerShape(14.dp))
                                    .clickable { individualVisibility[q.id] = true }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = GuruPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tap to reveal English answer (उत्तर देखने के लिए टैप करें)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GuruPurple
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Practice Launcher
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    GuruButton(
                        text = "Practice Word-Pick Quiz ⚡",
                        onClick = { onStartPractice(lesson.lessonNumber) },
                        modifier = Modifier.fillMaxWidth(),
                        height = 50.dp
                    )
                }
            }
        }
    }
}
