package com.spokenguru.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.spokenguru.app.ui.theme.GuruDarkPillBg
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkSurfaceContainer
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruPillBg
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleDeep
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruSurfaceContainer
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.isAppDarkTheme

@Composable
fun McqQuizView(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = isAppDarkTheme()
) {
    val labels = listOf("A", "B", "C", "D", "E", "F")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEachIndexed { index, optionText ->
            val label = labels.getOrElse(index) { "${index + 1}" }
            val isSelected = selectedOption == optionText

            McqOptionCard(
                label = label,
                text = optionText,
                isSelected = isSelected,
                isDark = isDark,
                onClick = { onOptionSelected(optionText) }
            )
        }
    }
}

@Composable
fun McqOptionCard(
    label: String,
    text: String,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(),
        label = "mcqScale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDark) GuruDarkPillBg else GuruPurpleLight
        } else {
            if (isDark) GuruDarkSurface else Color.White
        },
        label = "mcqBg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) GuruPurple else (if (isDark) GuruDarkCardBorder else GuruCardBorder),
        label = "mcqBorder"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 3.dp else 1.5.dp,
                shape = shape,
                ambientColor = if (isSelected) GuruPurple.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.05f)
            )
            .clip(shape)
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Letter Badge (A, B, C, D)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) GuruPurple else (if (isDark) GuruDarkSurfaceContainer else GuruPillBg)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else (if (isDark) GuruDarkText else GuruPurpleDeep)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Option Content Text
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontFamily = FontFamily.Default,
                color = if (isSelected) (if (isDark) Color.White else GuruPurpleDeep) else (if (isDark) GuruDarkText else GuruTextDark),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
