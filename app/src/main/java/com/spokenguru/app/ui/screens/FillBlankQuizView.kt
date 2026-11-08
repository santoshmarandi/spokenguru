package com.spokenguru.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.isAppDarkTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FillBlankQuizView(
    sentenceWithBlank: String,
    options: List<String>,
    selectedWord: String?,
    onWordSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = isAppDarkTheme()
) {
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sentence Display Card with highlighted Blank Slot
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, shape, ambientColor = Color.Black.copy(alpha = 0.04f))
                .clip(shape)
                .background(if (isDark) GuruDarkSurface else Color.White)
                .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            val parts = sentenceWithBlank.split("____")
            val beforeBlank = parts.getOrNull(0) ?: ""
            val afterBlank = parts.getOrNull(1) ?: ""

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "रिक्त स्थान की पूर्ति करें (Fill in the blank):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) GuruDarkText.copy(alpha = 0.6f) else GuruTextMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (beforeBlank.isNotBlank()) {
                        Text(
                            text = beforeBlank,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Default,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                    }

                    // Blank Pill Slot
                    val slotShape = RoundedCornerShape(8.dp)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clip(slotShape)
                            .background(if (selectedWord != null) (if (isDark) Color(0xFF2E234D) else GuruPurpleLight) else (if (isDark) Color(0xFF1E293B) else Color(0xFFF1F2F6)))
                            .border(1.5.dp, if (selectedWord != null) GuruPurple else GuruPurple.copy(alpha = 0.4f), slotShape)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(targetState = selectedWord, label = "blankAnim") { word ->
                            Text(
                                text = word ?: "  ?  ",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (word != null) GuruPurple else (if (isDark) GuruDarkText.copy(alpha = 0.5f) else GuruTextMuted)
                            )
                        }
                    }

                    if (afterBlank.isNotBlank()) {
                        Text(
                            text = afterBlank,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Default,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "नीचे दिए गए विकल्पों में से चुनें:",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) GuruDarkText.copy(alpha = 0.7f) else GuruTextMuted,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Option Chips FlowRow
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            options.forEach { option ->
                val isSelected = selectedWord == option
                val chipShape = RoundedCornerShape(12.dp)

                val chipBg by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (isDark) Color(0xFF2E234D) else GuruPurpleLight
                    } else {
                        if (isDark) GuruDarkSurface else Color.White
                    },
                    label = "chipBg"
                )

                val chipBorder by animateColorAsState(
                    targetValue = if (isSelected) GuruPurple else (if (isDark) GuruDarkCardBorder else GuruCardBorder),
                    label = "chipBorder"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(chipShape)
                        .background(chipBg)
                        .border(if (isSelected) 2.dp else 1.dp, chipBorder, chipShape)
                        .clickable { onWordSelected(option) }
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) GuruPurple else (if (isDark) GuruDarkText else GuruTextDark),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
