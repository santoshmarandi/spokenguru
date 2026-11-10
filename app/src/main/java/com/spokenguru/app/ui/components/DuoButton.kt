package com.spokenguru.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkSurfaceContainer
import com.spokenguru.app.ui.theme.GuruPillBg
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleDeep
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruTextDark
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
    height: Dp = 54.dp,
    shape: Shape = CircleShape
) {
    val isDark = isAppDarkTheme()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "btnScale"
    )

    val (targetBg, targetContentColor, borderColor) = when {
        !enabled -> Triple(
            if (isDark) GuruDarkSurfaceContainer else GuruPillBg,
            if (isDark) GuruTextSubtle else GuruTextSubtle,
            if (isDark) GuruDarkCardBorder.copy(alpha = 0.5f) else GuruCardBorder.copy(alpha = 0.7f)
        )
        variant == GuruButtonVariant.PRIMARY -> Triple(GuruPurple, Color.White, Color.Transparent)
        variant == GuruButtonVariant.SECONDARY -> Triple(
            if (isDark) GuruDarkSurface else Color.White,
            if (isDark) Color.White else GuruTextDark,
            if (isDark) GuruDarkCardBorder else GuruCardBorder
        )
        variant == GuruButtonVariant.DANGER -> Triple(GuruRed, Color.White, Color.Transparent)
        variant == GuruButtonVariant.MUTED -> Triple(
            if (isDark) GuruDarkSurfaceContainer else GuruPurpleLight,
            if (isDark) Color.White else GuruPurpleDeep,
            Color.Transparent
        )
        else -> Triple(GuruPurple, Color.White, Color.Transparent)
    }

    val bg by animateColorAsState(targetValue = targetBg, animationSpec = tween(200), label = "btnBg")
    val contentColor by animateColorAsState(targetValue = targetContentColor, animationSpec = tween(200), label = "btnFg")

    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .shadow(
                elevation = if (enabled && variant == GuruButtonVariant.PRIMARY) 4.dp else 0.dp,
                shape = shape,
                ambientColor = GuruPurple.copy(alpha = 0.25f),
                spotColor = GuruPurple.copy(alpha = 0.25f)
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
            .padding(horizontal = 24.dp),
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
