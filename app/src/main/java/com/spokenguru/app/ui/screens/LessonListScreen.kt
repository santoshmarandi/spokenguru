package com.spokenguru.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.R
import com.spokenguru.app.data.UserPrefs
import com.spokenguru.app.data.model.LessonEntity
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.AppBottomNavigation
import com.spokenguru.app.ui.components.NavTab
import com.spokenguru.app.ui.theme.AppThemePreset
import com.spokenguru.app.ui.theme.DarkModePreference
import com.spokenguru.app.ui.theme.GuruAmber
import com.spokenguru.app.ui.theme.GuruAmberLight
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruBlue
import com.spokenguru.app.ui.theme.GuruBlueLight
import com.spokenguru.app.ui.theme.GuruCardBorder
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
import com.spokenguru.app.ui.theme.GuruPurpleDeep
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruRedLight
import com.spokenguru.app.ui.theme.GuruSurfaceContainer
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle
import com.spokenguru.app.ui.theme.ThemeState
import com.spokenguru.app.ui.theme.isAppDarkTheme

private enum class LessonFilter(val label: String) {
    ALL("All"),
    SENTENCE("Sentence Builder"),
    WORD_MATCH("Word Match"),
    QUIZ("MCQ & Error"),
    IN_PROGRESS("In Progress")
}

@Composable
private fun getLessonFormatBadge(lessonNumber: Int): Triple<String, Color, Color> {
    return when (lessonNumber) {
        4, 5, 6, 33, 34, 38 -> Triple("Word Match", GuruMintDark, GuruMintLight)
        39 -> Triple("MCQ", GuruBlue, GuruBlueLight)
        37 -> Triple("Error Spot", GuruRed, GuruRedLight)
        31 -> Triple("Fill Blank", GuruAmber, GuruAmberLight)
        else -> Triple("Sentence", GuruPurple, GuruPurpleLight)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    repository: LessonRepository,
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onSelectLesson: (Int) -> Unit,
    onOpenMistakes: () -> Unit
) {
    val lessons by repository.allLessons.collectAsState(initial = emptyList())
    val mistakesCount by repository.mistakesCount.collectAsState(initial = 0)
    val isDark = isAppDarkTheme()
    var showAllChapters by remember { mutableStateOf(true) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(LessonFilter.ALL) }

    val filteredLessons = remember(lessons, searchQuery, selectedFilter) {
        lessons.filter { lesson ->
            val q = searchQuery.trim().lowercase()
            val matchesQuery = q.isBlank() ||
                lesson.title.lowercase().contains(q) ||
                "lesson ${lesson.lessonNumber}".contains(q) ||
                lesson.lessonNumber.toString() == q

            val matchesFilter = when (selectedFilter) {
                LessonFilter.ALL -> true
                LessonFilter.SENTENCE -> lesson.lessonNumber !in setOf(4, 5, 6, 31, 33, 34, 37, 38, 39)
                LessonFilter.WORD_MATCH -> lesson.lessonNumber in setOf(4, 5, 6, 33, 34, 38)
                LessonFilter.QUIZ -> lesson.lessonNumber in setOf(31, 37, 39)
                LessonFilter.IN_PROGRESS -> lesson.completedQuestions > 0 && lesson.completedQuestions < lesson.totalQuestions
            }

            matchesQuery && matchesFilter
        }
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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Top Bar with Streak, XP, and Theme Switcher
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_app_brand_logo),
                            contentDescription = "Spoken Guru Logo",
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(2.dp, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Spoken Guru",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 21.sp,
                                color = if (isDark) GuruDarkText else GuruTextDark
                            )
                            Text(
                                text = "Today: ${UserPrefs.todayPracticed}/${UserPrefs.dailyGoal} questions",
                                fontFamily = FontFamily.Default,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (UserPrefs.todayPracticed >= UserPrefs.dailyGoal) GuruMintDark else (if (isDark) GuruDarkTextMuted else GuruTextMuted)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Streak Pill
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF2D2212) else GuruAmberLight)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = GuruAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${UserPrefs.streakDays}d",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GuruAmber
                            )
                        }

                        // XP Pill
                        Row(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isDark) GuruDarkPillBg else GuruPurpleLight)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "XP",
                                tint = if (isDark) GuruPurple else GuruPurpleDeep,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${UserPrefs.totalXp}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else GuruPurpleDeep
                            )
                        }

                        // Theme Palette Switcher
                        IconButton(
                            onClick = { showThemeDialog = true },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isDark) GuruDarkPillBg else GuruPillBg)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme",
                                tint = if (isDark) GuruPurple else GuruPurpleDeep,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Smart Resume / Active Lesson Banner
            val activeLesson = lessons.firstOrNull { it.lessonNumber == UserPrefs.lastOpenedLesson && it.isAvailable }
                ?: lessons.firstOrNull { it.isAvailable && it.completedQuestions > 0 && it.completedQuestions < it.totalQuestions }
                ?: lessons.firstOrNull { it.isAvailable }
            if (activeLesson != null) {
                item {
                    val shape = RoundedCornerShape(22.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(1.5.dp, shape, ambientColor = Color.Black.copy(alpha = 0.05f))
                            .clip(shape)
                            .background(if (isDark) GuruDarkSurfaceContainer else GuruSurfaceContainer)
                            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
                            .clickable { onSelectLesson(activeLesson.lessonNumber) }
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CONTINUE PRACTICE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) GuruPurple else GuruPurpleDeep,
                                    letterSpacing = 1.1.sp
                                )
                                Text(
                                    text = "Lesson ${activeLesson.lessonNumber}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = activeLesson.title,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = if (isDark) GuruDarkText else GuruTextDark
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            val progress = if (activeLesson.totalQuestions > 0) {
                                (activeLesson.completedQuestions.toFloat() / activeLesson.totalQuestions).coerceIn(0f, 1f)
                            } else 0f

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(CircleShape),
                                    color = GuruPurple,
                                    trackColor = if (isDark) GuruDarkSurface else GuruProgressTrack
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (isDark) GuruDarkPillBg else GuruPurpleLight)
                                        .padding(horizontal = 9.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${activeLesson.completedQuestions}/${activeLesson.totalQuestions}",
                                        fontFamily = FontFamily.Default,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else GuruPurpleDeep
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Mistakes revision banner (Direct jump to Mistakes screen)
            if (mistakesCount > 0) {
                item {
                    val shape = RoundedCornerShape(14.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(if (isDark) Color(0xFF2C1318) else GuruRedLight)
                            .border(1.dp, GuruRed.copy(alpha = 0.3f), shape)
                            .clickable { onOpenMistakes() }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = GuruRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "$mistakesCount mistakes need review",
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = GuruRed
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = GuruRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            // Search & Filter Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = CircleShape,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = GuruTextSubtle,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = GuruTextSubtle,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Search 40 chapters (e.g. Tenses, Verb, MCQ)...",
                            fontSize = 13.sp,
                            color = GuruTextSubtle
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GuruPurple,
                        unfocusedBorderColor = if (isDark) GuruDarkCardBorder else GuruCardBorder,
                        focusedContainerColor = if (isDark) GuruDarkSurfaceContainer else GuruSurfaceContainer,
                        unfocusedContainerColor = if (isDark) GuruDarkSurfaceContainer else GuruSurfaceContainer
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Format Filter Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LessonFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        val chipBg by animateColorAsState(
                            targetValue = if (isSelected) GuruPurple else (if (isDark) GuruDarkPillBg else GuruPillBg),
                            animationSpec = tween(200),
                            label = "filterChipBg"
                        )
                        val chipBorder by animateColorAsState(
                            targetValue = if (isSelected) GuruPurple else (if (isDark) GuruDarkCardBorder else GuruCardBorder),
                            animationSpec = tween(200),
                            label = "filterChipBorder"
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(chipBg)
                                .border(1.dp, chipBorder, CircleShape)
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 13.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = filter.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else (if (isDark) GuruDarkText else GuruTextDark)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Curriculum Lessons",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                    Text(
                        text = "${filteredLessons.size} Lessons",
                        fontFamily = FontFamily.Default,
                        fontSize = 13.sp,
                        color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (filteredLessons.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matching lessons found",
                            fontSize = 14.sp,
                            color = GuruTextSubtle
                        )
                    }
                }
            } else {
                val visibleLessons = if (showAllChapters || searchQuery.isNotBlank() || selectedFilter != LessonFilter.ALL) {
                    filteredLessons
                } else {
                    filteredLessons.take(6)
                }
                itemsIndexed(visibleLessons, key = { _, l -> l.lessonNumber }) { index, lesson ->
                    Column(modifier = Modifier.animateItem()) {
                        MinimalLessonCard(
                            lesson = lesson,
                            index = index,
                            onClick = {
                                if (lesson.isAvailable) {
                                    onSelectLesson(lesson.lessonNumber)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (searchQuery.isBlank() && selectedFilter == LessonFilter.ALL && filteredLessons.size > 6) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(
                                onClick = { showAllChapters = !showAllChapters }
                            ) {
                                Text(
                                    text = if (showAllChapters) "Show fewer lessons" else "View all ${filteredLessons.size} lessons",
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = GuruPurple
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            onDismiss = { showThemeDialog = false }
        )
    }
}

@Composable
fun MinimalLessonCard(
    lesson: LessonEntity,
    index: Int,
    onClick: () -> Unit
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(20.dp)
    val isCompleted = lesson.isAvailable && lesson.completedQuestions >= lesson.totalQuestions && lesson.totalQuestions > 0
    val (formatLabel, formatColor, formatLightBg) = getLessonFormatBadge(lesson.lessonNumber)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed && lesson.isAvailable) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "lessonCardScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .shadow(1.5.dp, shape, ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
            .clickable(
                enabled = lesson.isAvailable,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clean Index Indicator
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isCompleted) GuruMintLight
                        else if (lesson.isAvailable) (if (isDark) GuruDarkPillBg else GuruPurpleLight)
                        else (if (isDark) GuruDarkSurfaceContainer else GuruPillBg)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = GuruMint,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = String.format("%02d", lesson.lessonNumber),
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (lesson.isAvailable) (if (isDark) Color.White else GuruPurpleDeep) else GuruTextSubtle
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Lesson Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = if (lesson.isAvailable) {
                        if (isDark) GuruDarkText else GuruTextDark
                    } else GuruTextSubtle
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isDark) formatColor.copy(alpha = 0.16f) else formatLightBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = formatLabel,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = formatColor
                        )
                    }

                    Text(
                        text = "${lesson.completedQuestions}/${lesson.totalQuestions} Qs",
                        fontSize = 11.sp,
                        color = GuruTextSubtle
                    )

                    if (lesson.isAvailable && lesson.completedQuestions > 0 && !isCompleted) {
                        LinearProgressIndicator(
                            progress = { (lesson.completedQuestions.toFloat() / lesson.totalQuestions).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .width(54.dp)
                                .height(4.dp)
                                .clip(CircleShape),
                            color = GuruPurple,
                            trackColor = if (isDark) GuruDarkSurfaceContainer else GuruProgressTrack
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status Indicator
            if (lesson.isAvailable) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = GuruTextSubtle,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = GuruTextSubtle.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ThemePickerDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = GuruPurple,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Theme & Appearance",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isDark) GuruDarkText else GuruTextDark
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text(
                        text = "Appearance:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DarkModePreference.values().forEach { mode ->
                            val isSelected = ThemeState.darkModePref == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) GuruPurple else (if (isDark) Color(0xFF1E293B) else Color(0xFFF3F4F6))
                                    )
                                    .clickable {
                                        ThemeState.updateDarkMode(context, mode)
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${mode.icon} ${mode.label}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else (if (isDark) GuruDarkText else GuruTextDark)
                                )
                            }
                        }
                    }
                }

                Column {
                    Text(
                        text = "Color Palette:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val presets = AppThemePreset.values()
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in presets.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (j in 0..1) {
                                    if (i + j < presets.size) {
                                        val preset = presets[i + j]
                                        val isSelected = ThemeState.currentPreset == preset
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(
                                                    if (isSelected) preset.primaryColor.copy(alpha = if (isDark) 0.24f else 0.12f)
                                                    else (if (isDark) GuruDarkSurfaceContainer else GuruPillBg.copy(alpha = 0.55f))
                                                )
                                                .border(
                                                    width = if (isSelected) 1.8.dp else 1.dp,
                                                    color = if (isSelected) preset.primaryColor else (if (isDark) GuruDarkCardBorder else GuruCardBorder),
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .clickable {
                                                    ThemeState.updatePreset(context, preset)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 9.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(16.dp)
                                                        .clip(CircleShape)
                                                        .background(preset.primaryColor)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = preset.title,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 12.sp,
                                                    color = if (isDark) GuruDarkText else GuruTextDark
                                                )
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", fontWeight = FontWeight.Bold, color = GuruPurple)
            }
        },
        containerColor = if (isDark) GuruDarkSurface else Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
