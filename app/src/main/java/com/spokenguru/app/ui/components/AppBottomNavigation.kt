package com.spokenguru.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkPillBg
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleDeep
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruSurfaceContainer
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
    val bg = if (isDark) GuruDarkSurface else GuruSurfaceContainer
    val borderColor = if (isDark) GuruDarkCardBorder else GuruCardBorder

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bg)
    ) {
        HorizontalDivider(
            thickness = 0.8.dp,
            color = borderColor.copy(alpha = 0.45f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(horizontal = 12.dp),
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
                icon = Icons.AutoMirrored.Outlined.MenuBook,
                activeIcon = Icons.AutoMirrored.Filled.MenuBook,
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
    val pillWidth by animateDpAsState(
        targetValue = if (isSelected) 58.dp else 32.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pillWidth"
    )
    val pillBg by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDark) GuruDarkPillBg else GuruPurpleLight
        } else Color.Transparent,
        animationSpec = tween(220),
        label = "pillBg"
    )
    val tintColor by animateColorAsState(
        targetValue = if (isSelected) (if (isDark) Color.White else GuruPurpleDeep) else GuruTextSubtle,
        animationSpec = tween(200),
        label = "tintColor"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "iconScale"
    )

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
                .width(pillWidth)
                .height(30.dp)
                .clip(CircleShape)
                .background(pillBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) activeIcon else icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier
                    .size(20.dp)
                    .scale(iconScale)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontFamily = FontFamily.Default,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = tintColor
        )
    }
}
