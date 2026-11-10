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
    val shape = RoundedCornerShape(22.dp)

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
                .background(if (isDark) GuruDarkSurfaceContainer else GuruSurfaceContainer)
                .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, shape)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            val tokens = sentenceWithBlank
                .replace("____", " __BLANK__ ")
                .trim()
                .split("\\s+".toRegex())
                .filter { it.isNotBlank() }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "रिक्त स्थान की पूर्ति करें (Fill in the blank):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDark) GuruDarkText.copy(alpha = 0.6f) else GuruTextMuted,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tokens.forEach { token ->
                        if (token.contains("__BLANK__")) {
                            val suffix = token.substringAfter("__BLANK__", "")
                            val prefix = token.substringBefore("__BLANK__", "")
                            if (prefix.isNotBlank()) {
                                Text(
                                    text = prefix,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDark) GuruDarkText else GuruTextDark,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                            val slotShape = RoundedCornerShape(10.dp)
                            Box(
                                modifier = Modifier
                                    .clip(slotShape)
                                    .background(if (selectedWord != null) (if (isDark) GuruDarkPillBg else GuruPurpleLight) else (if (isDark) GuruDarkSurface else GuruPillBg))
                                    .border(1.5.dp, if (selectedWord != null) GuruPurple else GuruPurple.copy(alpha = 0.4f), slotShape)
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AnimatedContent(targetState = selectedWord, label = "blankAnim") { word ->
                                    Text(
                                        text = word ?: "  ?  ",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (word != null) (if (isDark) Color.White else GuruPurpleDeep) else (if (isDark) GuruDarkText.copy(alpha = 0.5f) else GuruTextMuted)
                                    )
                                }
                            }
                            if (suffix.isNotBlank()) {
                                Text(
                                    text = suffix,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDark) GuruDarkText else GuruTextDark,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        } else {
                            Text(
                                text = token,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = FontFamily.Default,
                                color = if (isDark) GuruDarkText else GuruTextDark,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
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
                val chipShape = RoundedCornerShape(16.dp)

                val chipBg by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (isDark) GuruDarkPillBg else GuruPurpleLight
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
                        .shadow(1.5.dp, chipShape, ambientColor = Color.Black.copy(alpha = 0.06f))
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
                        color = if (isSelected) (if (isDark) Color.White else GuruPurpleDeep) else (if (isDark) GuruDarkText else GuruTextDark),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
