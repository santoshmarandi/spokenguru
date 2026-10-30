package com.spokenguru.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.spokenguru.app.ui.theme.isAppDarkTheme
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.AppBottomNavigation
import com.spokenguru.app.ui.components.NavTab
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
import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.platform.LocalContext
import com.spokenguru.app.ui.theme.AppThemePreset
import com.spokenguru.app.ui.theme.DarkModePreference
import com.spokenguru.app.ui.theme.ThemeState
import com.spokenguru.app.ui.theme.GuruTextSubtle

@Composable
fun ProfileScreen(
    repository: LessonRepository,
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onOpenMistakes: () -> Unit,
    onOpenImport: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val mistakesCount by repository.mistakesCount.collectAsState(initial = 0)
    var remindersEnabled by remember { mutableStateOf(true) }

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
            // User Header Profile Card (from unnamed (4).png)
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
                        // User Avatar RS
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GuruPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RS",
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = GuruPurple
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Rahul Sharma",
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = if (isDark) GuruDarkText else GuruTextDark
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(GuruBlueLight)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "• Free Member",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GuruBlue
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Spoken English Learner",
                                fontSize = 13.sp,
                                color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                            )
                            Text(
                                text = "📅 Member since October 2026",
                                fontSize = 11.sp,
                                color = GuruTextSubtle
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
                                text = "Level 1 • Beginner",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GuruPurple
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0xFF232142) else Color(0xFFF6F4FF))
                                .clickable { onOpenImport() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = GuruPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Manage / DB",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GuruPurple
                                )
                            }
                        }
                    }
                }
            }

            // Learning Progress Section
            Text(
                text = "Learning Progress",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = if (isDark) GuruDarkText else GuruTextDark
            )

            // 4 Stats Cards in 2x2 grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak Card
                StatCard(
                    title = "Daily Practice Streak",
                    value = "18 Days",
                    tag = "Active",
                    tagBg = GuruAmberLight,
                    tagColor = GuruAmber,
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = GuruAmber,
                    modifier = Modifier.weight(1f)
                )

                // Sentences Card
                StatCard(
                    title = "Sentences Practiced",
                    value = "101 / 101",
                    tag = "100%",
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
                // Chapter Track
                StatCard(
                    title = "Chapters Available",
                    value = "40 / 40",
                    tag = "eBook",
                    tagBg = GuruPurpleLight,
                    tagColor = GuruPurple,
                    icon = Icons.Default.Translate,
                    iconTint = GuruPurple,
                    modifier = Modifier.weight(1f)
                )

                // Pending Revisions Card
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

            // Theme & Appearance Section (थीम और मोड)
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
                    // Dark / Light Mode Selector
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

                    // 3 Mode Pills: System, Light, Dark
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

                    // Color Theme Preset
                    Column {
                        Text(
                            text = "Color Theme (थीम रंग)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "4 Vibrant Color Palettes • 4 अलग-अलग थीम",
                            fontSize = 12.sp,
                            color = if (isDark) GuruDarkTextMuted else GuruTextMuted
                        )
                    }

                    // 4 Theme Preset Cards in 2x2 grid
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
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(
                                                    if (isSelected) (if (isDark) Color(0xFF292452) else Color(0xFFF3F0FE))
                                                    else (if (isDark) Color(0xFF1B1933) else Color(0xFFFBFBFE))
                                                )
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) preset.primaryColor else (if (isDark) Color(0xFF2C284C) else Color(0xFFE4E1F4)),
                                                    shape = RoundedCornerShape(16.dp)
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
                                                    // Primary Color swatch
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(preset.primaryColor)
                                                    )
                                                    if (isSelected) {
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .background(preset.primaryColor)
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = "✓ Selected",
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
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Study & eBook Preferences Section
            Text(
                text = "Study & Preferences",
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = if (isDark) GuruDarkText else GuruTextDark
            )

            // Preferences List Card
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
                    // Reading Mode
                    PreferenceRow(
                        icon = Icons.Default.Translate,
                        iconBg = GuruPurpleLight,
                        iconTint = GuruPurple,
                        title = "Reading Mode Preferences",
                        subtitle = "Hinglish translations ON • Speed 1.0x"
                    )

                    // Daily Goal & Reminders Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GuruMintLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = GuruMint,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daily Goal & Reminders",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isDark) GuruDarkText else GuruTextDark
                            )
                            Text(
                                text = "Target: 30 sentences/day • 8:00 PM",
                                fontSize = 12.sp,
                                color = GuruTextMuted
                            )
                        }
                        Switch(
                            checked = remindersEnabled,
                            onCheckedChange = { remindersEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GuruPurple
                            )
                        )
                    }

                    // Offline Download & Database Manager
                    PreferenceRow(
                        icon = Icons.Default.CloudDownload,
                        iconBg = GuruBlueLight,
                        iconTint = GuruBlue,
                        title = "Offline Database & Lessons",
                        subtitle = "Room SQLite Database • 40 Chapters",
                        onClick = onOpenImport
                    )

                    // Bookmarks
                    PreferenceRow(
                        icon = Icons.Default.Bookmark,
                        iconBg = GuruRedLight,
                        iconTint = GuruRed,
                        title = "Bookmarks & Saved Notes",
                        subtitle = "Saved practice sentences"
                    )
                }
            }
        }
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
                fontSize = 20.sp,
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
