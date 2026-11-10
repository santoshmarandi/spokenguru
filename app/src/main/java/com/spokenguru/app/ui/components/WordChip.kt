package com.spokenguru.app.ui.components

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkSurfaceContainer
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruPillBg
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleBorder
import com.spokenguru.app.ui.theme.GuruTextDark

/**
 * Word Chip in Answer Slot: Clean, modern soft-squircle pill.
 * Tapping removes it back to the bank.
 */
@Composable
fun SlotWordChip(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val enterScale = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) {
        enterScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chipScale"
    )

    Box(
        modifier = modifier
            .scale(enterScale.value * pressScale)
            .shadow(1.5.dp, shape, ambientColor = Color.Black.copy(alpha = 0.06f))
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(
                width = 1.dp,
                color = if (isDark) GuruPurple.copy(alpha = 0.55f) else GuruPurpleBorder,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onRemove
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = if (isDark) GuruDarkText else GuruTextDark
        )
    }
}

/**
 * Word Tile in Word Bank: Pure English word tile matching clean M3 Reference style.
 */
@Composable
fun BankWordTile(
    word: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(16.dp)

    if (isSelected) {
        // Soft tinted placeholder state inside bank
        Box(
            modifier = modifier
                .defaultMinSize(minWidth = 60.dp, minHeight = 46.dp)
                .clip(shape)
                .background(if (isDark) GuruDarkSurfaceContainer.copy(alpha = 0.6f) else GuruPillBg)
                .border(
                    width = 1.dp,
                    color = if (isDark) GuruDarkCardBorder.copy(alpha = 0.5f) else GuruCardBorder.copy(alpha = 0.6f),
                    shape = shape
                )
                .padding(horizontal = 18.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Transparent
            )
        }
    } else {
        // Active clickable white pill-squircle word tile
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.93f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
            label = "tileScale"
        )

        Box(
            modifier = modifier
                .scale(scale)
                .defaultMinSize(minWidth = 56.dp, minHeight = 46.dp)
                .shadow(2.dp, shape, ambientColor = Color.Black.copy(alpha = 0.08f))
                .clip(shape)
                .background(if (isDark) GuruDarkSurface else Color.White)
                .border(
                    width = 1.dp,
                    color = if (isDark) GuruDarkCardBorder else GuruCardBorder,
                    shape = shape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(horizontal = 18.dp, vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (isDark) GuruDarkText else GuruTextDark
            )
        }
    }
}
