package com.spokenguru.app.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.data.model.LessonEntity
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.AppBottomNavigation
import com.spokenguru.app.ui.components.GuruButton
import com.spokenguru.app.ui.components.GuruButtonVariant
import com.spokenguru.app.ui.components.NavTab
import com.spokenguru.app.ui.theme.AppThemePreset
import com.spokenguru.app.ui.theme.DarkModePreference
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkBg
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruDarkTextMuted
import com.spokenguru.app.ui.theme.GuruMint
import com.spokenguru.app.ui.theme.GuruMintLight
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruRedLight
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle
import com.spokenguru.app.ui.theme.ThemeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    repository: LessonRepository,
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onSelectLesson: (Int) -> Unit
) {
    val lessons by repository.allLessons.collectAsState(initial = emptyList())
    val mistakesCount by repository.mistakesCount.collectAsState(initial = 0)
    val isDark = isAppDarkTheme()
    var showAllChapters by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

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
            // Minimalist Clean Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Spoken Guru",
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        Text(
                            text = "English Practice & Lessons",
                            fontFamily = FontFamily.Default,
                            fontSize = 13.sp,
                            color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                        )
                    }

                    // Theme Palette Switcher
                    IconButton(
                        onClick = { showThemeDialog = true },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme",
                            tint = GuruPurple,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Quick Resume / Active Lesson Banner (Minimalist)
            val activeLesson = lessons.firstOrNull { it.isAvailable }
            if (activeLesson != null) {
                item {
                    val shape = RoundedCornerShape(18.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, shape, ambientColor = Color.Black.copy(alpha = 0.05f))
                            .clip(shape)
                            .background(if (isDark) GuruDarkSurface else Color.White)
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
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GuruPurple,
                                    letterSpacing = 0.8.sp
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

                            // Clean Progress Bar
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
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = GuruPurple,
                                    trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE5E7EB)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "${activeLesson.completedQuestions}/${activeLesson.totalQuestions}",
                                    fontFamily = FontFamily.Default,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GuruPurple
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Mistakes revision banner if any (Subtle & Clean)
            if (mistakesCount > 0) {
                item {
                    val shape = RoundedCornerShape(14.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(if (isDark) Color(0xFF2C1318) else GuruRedLight)
                            .border(1.dp, GuruRed.copy(alpha = 0.3f), shape)
                            .clickable { onTabSelected(NavTab.PROFILE) }
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
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
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
                        text = "${lessons.size} Lessons",
                        fontFamily = FontFamily.Default,
                        fontSize = 13.sp,
                        color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Display lessons
            val visibleLessons = if (showAllChapters) lessons else lessons.take(6)
            itemsIndexed(visibleLessons) { index, lesson ->
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

            // "Browse All 40 Chapters" button
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
                            text = if (showAllChapters) "Show fewer lessons" else "View all 40 lessons",
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
    val shape = RoundedCornerShape(16.dp)

    val isCompleted = lesson.isAvailable && lesson.completedQuestions >= lesson.totalQuestions && lesson.totalQuestions > 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, shape, ambientColor = Color.Black.copy(alpha = 0.04f))
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
            .clickable(enabled = lesson.isAvailable, onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clean Index Indicator
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isCompleted) GuruMintLight
                        else if (lesson.isAvailable) (if (isDark) Color(0xFF1E293B) else GuruPurpleLight)
                        else (if (isDark) Color(0xFF161E2E) else Color(0xFFF3F4F6))
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
                        color = if (lesson.isAvailable) GuruPurple else GuruTextSubtle
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

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

                if (lesson.isAvailable && lesson.completedQuestions > 0 && !isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LinearProgressIndicator(
                            progress = { (lesson.completedQuestions.toFloat() / lesson.totalQuestions).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .width(90.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = GuruPurple,
                            trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE5E7EB)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${lesson.completedQuestions}/${lesson.totalQuestions}",
                            fontSize = 11.sp,
                            color = GuruTextSubtle
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
                // Display Mode
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

                // Color Palette
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
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isSelected) (if (isDark) Color(0xFF1E293B) else Color(0xFFF3F4F6))
                                                    else Color.Transparent
                                                )
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 1.dp,
                                                    color = if (isSelected) preset.primaryColor else (if (isDark) GuruDarkCardBorder else GuruCardBorder),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clickable {
                                                    ThemeState.updatePreset(context, preset)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 8.dp)
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
