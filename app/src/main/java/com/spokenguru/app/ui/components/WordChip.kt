package com.spokenguru.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
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
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleBorder
import com.spokenguru.app.ui.theme.GuruTextDark

/**
 * Word Chip in Answer Slot: Clean, modern pill.
 * Tapping removes it back to the bank.
 */
@Composable
fun SlotWordChip(
    text: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(12.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "chipScale")

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(1.dp, shape, ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(shape)
            .background(if (isDark) GuruDarkSurface else Color.White)
            .border(
                width = 1.dp,
                color = if (isDark) GuruPurple.copy(alpha = 0.5f) else GuruPurpleBorder,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onRemove
            )
            .padding(horizontal = 14.dp, vertical = 9.dp),
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
 * Word Tile in Word Bank: Pure English word tile with clean tactile feel.
 */
@Composable
fun BankWordTile(
    word: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val shape = RoundedCornerShape(12.dp)

    if (isSelected) {
        // Disabled placeholder state inside bank
        Box(
            modifier = modifier
                .defaultMinSize(minWidth = 64.dp, minHeight = 44.dp)
                .clip(shape)
                .background(if (isDark) Color(0xFF1E293B).copy(alpha = 0.4f) else Color(0xFFF3F4F6))
                .border(
                    width = 1.dp,
                    color = if (isDark) Color(0xFF334155).copy(alpha = 0.4f) else Color(0xFFE5E7EB),
                    shape = shape
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Transparent
            )
        }
    } else {
        // Active clickable word tile
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "tileScale")

        Box(
            modifier = modifier
                .scale(scale)
                .shadow(1.5.dp, shape, ambientColor = Color.Black.copy(alpha = 0.06f))
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
                .padding(horizontal = 14.dp, vertical = 10.dp),
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
