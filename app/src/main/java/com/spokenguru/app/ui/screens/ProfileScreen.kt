package com.spokenguru.app.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.data.UserPrefs
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
import com.spokenguru.app.ui.theme.isAppDarkTheme

@Composable
fun ProfileScreen(
    repository: LessonRepository,
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onOpenMistakes: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val lessons by repository.allLessons.collectAsState(initial = emptyList())
    val mistakesCount by repository.mistakesCount.collectAsState(initial = 0)

    val totalQuestions = remember(lessons) { lessons.sumOf { it.totalQuestions } }
    val totalCompleted = remember(lessons) { lessons.sumOf { it.completedQuestions } }
    val completedLessons = remember(lessons) {
        lessons.count { it.isAvailable && it.totalQuestions > 0 && it.completedQuestions >= it.totalQuestions }
    }
    val progressPct = if (totalQuestions > 0) ((totalCompleted * 100L) / totalQuestions).toInt() else 0
    val (levelNum, levelTitle) = UserPrefs.getLevelTitle()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(UserPrefs.learnerName) }
    var showDailyGoalDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            AppBottomNavigation(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = if (isDark) GuruDarkBg else GuruBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Header Profile Card
            val cardShape = RoundedCornerShape(24.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, cardShape, ambientColor = GuruPurple.copy(alpha = 0.08f))
                    .clip(cardShape)
                    .background(if (isDark) GuruDarkSurface else Color.White)
                    .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GuruPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = UserPrefs.getInitials(),
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = GuruPurple
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = UserPrefs.learnerName,
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = if (isDark) GuruDarkText else GuruTextDark,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        nameInput = UserPrefs.learnerName
                                        showEditNameDialog = true
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Name",
                                        tint = GuruPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${UserPrefs.totalXp} XP Earned • Daily Goal: ${UserPrefs.todayPracticed}/${UserPrefs.dailyGoal}",
                                fontSize = 12.sp,
                                color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF262447) else GuruPurpleLight)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Level $levelNum • $levelTitle",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GuruPurple
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF1E3228) else GuruMintLight)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GuruMint,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "$progressPct% Mastery",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GuruMint
                                )
                            }
                        }
                    }
                }
            }

            // Live Learning Progress Section
            Text(
                text = "Learning Progress (लाइव प्रगति)",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = if (isDark) GuruDarkText else GuruTextDark
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Daily Practice Streak",
                    value = "${UserPrefs.streakDays} Days",
                    tag = "${UserPrefs.todayPracticed}/${UserPrefs.dailyGoal} Today",
                    tagBg = GuruAmberLight,
                    tagColor = GuruAmber,
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = GuruAmber,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Questions Practiced",
                    value = "$totalCompleted / ${if (totalQuestions > 0) totalQuestions else 8207}",
                    tag = "$progressPct%",
                    tagBg = GuruBlueLight,
                    tagColor = GuruBlue,
                    icon = Icons.Default.CheckCircle,
                    iconTint = GuruBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Lessons Completed",
                    value = "$completedLessons / ${if (lessons.isNotEmpty()) lessons.size else 40}",
                    tag = "Chapters",
                    tagBg = GuruPurpleLight,
                    tagColor = GuruPurple,
                    icon = Icons.Default.Translate,
                    iconTint = GuruPurple,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Pending Revisions",
                    value = "$mistakesCount",
                    tag = if (mistakesCount > 0) "Review >" else "Clear",
                    tagBg = if (mistakesCount > 0) GuruRedLight else GuruMintLight,
                    tagColor = if (mistakesCount > 0) GuruRed else GuruMint,
                    icon = Icons.Default.Edit,
                    iconTint = if (mistakesCount > 0) GuruRed else GuruMint,
                    onClick = if (mistakesCount > 0) onOpenMistakes else null,
                    modifier = Modifier.weight(1f)
                )
            }

            // Theme & Appearance Section
            Text(
                text = "Theme & Appearance",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = if (isDark) GuruDarkText else GuruTextDark
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, cardShape, ambientColor = GuruPurple.copy(alpha = 0.08f))
                    .clip(cardShape)
                    .background(if (isDark) GuruDarkSurface else Color.White)
                    .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text(
                            text = "Display Mode (प्रदर्शन मोड)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Light, Dark, or System mode • तुरंत बदलें",
                            fontSize = 12.sp,
                            color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DarkModePreference.values().forEach { mode ->
                            val isSelected = ThemeState.darkModePref == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isSelected) GuruPurple else (if (isDark) Color(0xFF262447) else GuruPurpleLight)
                                    )
                                    .clickable {
                                        ThemeState.updateDarkMode(context, mode)
                                    }
                                    .padding(vertical = 11.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(text = mode.icon, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = mode.label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else (if (isDark) GuruDarkText else GuruTextDark)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Column {
                        Text(
                            text = "Color Theme (थीम रंग)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "5 Vibrant Color Palettes • 5 अलग-अलग थीम",
                            fontSize = 12.sp,
                            color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                        )
                    }

                    val presets = AppThemePreset.values()
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        for (i in presets.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (j in 0..1) {
                                    if (i + j < presets.size) {
                                        val preset = presets[i + j]
                                        val isSelected = ThemeState.currentPreset == preset
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(
                                                    if (isSelected) preset.primaryColor.copy(alpha = if (isDark) 0.22f else 0.12f)
                                                    else (if (isDark) Color(0xFF1F1D26) else Color.White)
                                                )
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) preset.primaryColor else (if (isDark) Color(0xFF3A3548) else Color(0xFFD5CEDC)),
                                                    shape = RoundedCornerShape(18.dp)
                                                )
                                                .clickable {
                                                    ThemeState.updatePreset(context, preset)
                                                }
                                                .padding(12.dp)
                                        ) {
                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(preset.primaryColor)
                                                    )
                                                    if (isSelected) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(CircleShape)
                                                                .background(preset.primaryColor)
                                                                .padding(horizontal = 7.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = "✓ Active",
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = preset.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = if (isDark) GuruDarkText else GuruTextDark
                                                )
                                                Text(
                                                    text = preset.subtitle,
                                                    fontSize = 11.sp,
                                                    color = if (isDark) GuruDarkTextMuted else GuruTextMuted
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

            // Study & Preferences Section
            Text(
                text = "Study & Preferences",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = if (isDark) GuruDarkText else GuruTextDark
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, cardShape, ambientColor = GuruPurple.copy(alpha = 0.08f))
                    .clip(cardShape)
                    .background(if (isDark) GuruDarkSurface else Color.White)
                    .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Auto-Speak Correct Answers Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GuruPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = GuruPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Speak Answers",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isDark) GuruDarkText else GuruTextDark
                            )
                            Text(
                                text = "Pronounce English solution on correct answer",
                                fontSize = 12.sp,
                                color = GuruTextMuted
                            )
                        }
                        Switch(
                            checked = UserPrefs.autoPlayAudio,
                            onCheckedChange = { UserPrefs.updateAutoPlayAudio(context, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GuruPurple
                            )
                        )
                    }

                    // Smart Confusable Distractors Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GuruPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = GuruPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Smart Confusable Distractors",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isDark) GuruDarkText else GuruTextDark
                            )
                            Text(
                                text = "Challenge mode with tricky tense & grammar traps",
                                fontSize = 12.sp,
                                color = GuruTextMuted
                            )
                        }
                        Switch(
                            checked = UserPrefs.smartDistractorsEnabled,
                            onCheckedChange = { UserPrefs.updateSmartDistractors(context, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GuruPurple
                            )
                        )
                    }

                    // Daily Practice Goal Target
                    PreferenceRow(
                        icon = Icons.Default.Flag,
                        iconBg = GuruMintLight,
                        iconTint = GuruMint,
                        title = "Daily Practice Goal",
                        subtitle = "Target: ${UserPrefs.dailyGoal} questions/day (Tap to change)",
                        onClick = { showDailyGoalDialog = true }
                    )

                    // Bookmarked Chapters
                    PreferenceRow(
                        icon = Icons.Default.Bookmark,
                        iconBg = GuruRedLight,
                        iconTint = GuruRed,
                        title = "Bookmarked eBook Chapters",
                        subtitle = if (UserPrefs.bookmarkedLessons.isEmpty()) "Bookmark chapters inside Exercise eBook"
                        else "${UserPrefs.bookmarkedLessons.size} saved chapters (${UserPrefs.bookmarkedLessons.sorted().joinToString { "L$it" }})",
                        onClick = { onTabSelected(NavTab.EBOOK) }
                    )
                }
            }
        }
    }

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Learner Name", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    singleLine = true,
                    placeholder = { Text("Enter your name") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        UserPrefs.updateLearnerName(context, nameInput)
                        showEditNameDialog = false
                    }
                ) {
                    Text("Save", fontWeight = FontWeight.Bold, color = GuruPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel", color = GuruTextMuted)
                }
            }
        )
    }

    if (showDailyGoalDialog) {
        val goals = listOf(10, 20, 30, 50)
        AlertDialog(
            onDismissRequest = { showDailyGoalDialog = false },
            title = { Text("Select Daily Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    goals.forEach { goal ->
                        val isSelected = UserPrefs.dailyGoal == goal
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) GuruPurpleLight
                                    else (if (isDark) Color(0xFF1E293B) else Color(0xFFF3F4F6))
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) GuruPurple else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    UserPrefs.updateDailyGoal(context, goal)
                                    showDailyGoalDialog = false
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "$goal questions / day",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) GuruPurple else (if (isDark) GuruDarkText else GuruTextDark)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDailyGoalDialog = false }) {
                    Text("Close", color = GuruPurple, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    tag: String,
    tagBg: Color,
    tagColor: Color,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .shadow(2.dp, shape, ambientColor = GuruPurple.copy(alpha = 0.08f))
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(tagBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(tagBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = tag,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = tagColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (isDark) GuruDarkText else GuruTextDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = if (isDark) GuruDarkTextMuted else GuruTextMuted
            )
        }
    }
}

@Composable
fun PreferenceRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null
) {
    val isDark = isAppDarkTheme()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isDark) GuruDarkText else GuruTextDark
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = GuruTextMuted
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = GuruTextSubtle,
            modifier = Modifier.size(16.dp)
        )
    }
}
