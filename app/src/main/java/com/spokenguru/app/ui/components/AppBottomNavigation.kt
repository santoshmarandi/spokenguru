package com.spokenguru.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.spokenguru.app.ui.theme.isAppDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruTextSubtle

enum class NavTab {
    HOME,
    EBOOK,
    PRACTICE,
    PROFILE
}

@Composable
fun AppBottomNavigation(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val bg = if (isDark) GuruDarkSurface else Color.White
    val borderColor = if (isDark) GuruDarkCardBorder else GuruCardBorder

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bg)
    ) {
        HorizontalDivider(
            thickness = 0.8.dp,
            color = borderColor.copy(alpha = 0.7f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = "Home",
                icon = Icons.Outlined.Home,
                activeIcon = Icons.Default.Home,
                isSelected = currentTab == NavTab.HOME,
                onClick = { onTabSelected(NavTab.HOME) }
            )
            NavItem(
                label = "eBook",
                icon = Icons.Outlined.MenuBook,
                activeIcon = Icons.Default.MenuBook,
                isSelected = currentTab == NavTab.EBOOK,
                onClick = { onTabSelected(NavTab.EBOOK) }
            )
            NavItem(
                label = "Practice",
                icon = Icons.Outlined.Bolt,
                activeIcon = Icons.Default.Bolt,
                isSelected = currentTab == NavTab.PRACTICE,
                onClick = { onTabSelected(NavTab.PRACTICE) }
            )
            NavItem(
                label = "Profile",
                icon = Icons.Outlined.Person,
                activeIcon = Icons.Default.Person,
                isSelected = currentTab == NavTab.PROFILE,
                onClick = { onTabSelected(NavTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    activeIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = isAppDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 46.dp, height = 26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    if (isSelected) {
                        if (isDark) Color(0xFF1E293B) else GuruPurpleLight
                    } else Color.Transparent
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) activeIcon else icon,
                contentDescription = label,
                tint = if (isSelected) GuruPurple else GuruTextSubtle,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = FontFamily.Default,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) GuruPurple else GuruTextSubtle
        )
    }
}
