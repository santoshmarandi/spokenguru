package com.spokenguru.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import com.spokenguru.app.ui.theme.isAppDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleDeep
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle

enum class GuruButtonVariant {
    PRIMARY,
    SECONDARY,
    DANGER,
    MUTED
}

@Composable
fun GuruButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GuruButtonVariant = GuruButtonVariant.PRIMARY,
    icon: ImageVector? = null,
    subText: String? = null,
    enabled: Boolean = true,
    height: Dp = 50.dp
) {
    val isDark = isAppDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed && enabled) 0.98f else 1f, label = "btnScale")

    val shape = RoundedCornerShape(16.dp)

    val (bg, contentColor, borderColor) = when {
        !enabled -> Triple(
            if (isDark) Color(0xFF1E293B) else Color(0xFFF3F4F6),
            if (isDark) Color(0xFF475569) else Color(0xFF9CA3AF),
            Color.Transparent
        )
        variant == GuruButtonVariant.PRIMARY -> Triple(GuruPurple, Color.White, Color.Transparent)
        variant == GuruButtonVariant.SECONDARY -> Triple(
            if (isDark) GuruDarkSurface else Color.White,
            if (isDark) Color.White else GuruTextDark,
            if (isDark) GuruDarkCardBorder else GuruCardBorder
        )
        variant == GuruButtonVariant.DANGER -> Triple(GuruRed, Color.White, Color.Transparent)
        variant == GuruButtonVariant.MUTED -> Triple(
            if (isDark) Color(0xFF1E293B) else GuruPurpleLight,
            GuruPurple,
            Color.Transparent
        )
        else -> Triple(GuruPurple, Color.White, Color.Transparent)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .shadow(
                elevation = if (enabled && variant == GuruButtonVariant.PRIMARY) 2.dp else 0.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(shape)
            .background(bg)
            .border(if (borderColor != Color.Transparent) 1.dp else 0.dp, borderColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (subText != null) {
                Text(
                    text = subText.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(16.dp)
                        .background(contentColor.copy(alpha = 0.3f))
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                fontFamily = FontFamily.Default,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                letterSpacing = 0.2.sp
            )

            if (icon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
