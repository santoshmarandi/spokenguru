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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.audio.SoundEffectManager
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruMint
import com.spokenguru.app.ui.theme.GuruMintDark
import com.spokenguru.app.ui.theme.GuruMintLight
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruRedLight
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle
import com.spokenguru.app.ui.theme.isAppDarkTheme
import kotlinx.coroutines.delay

@Composable
fun WordMatchQuizView(
    matchPairs: Map<String, String>,
    isDark: Boolean = isAppDarkTheme(),
    onMatchSuccess: () -> Unit,
    onMismatch: (userLeft: String, userRight: String, expectedRight: String) -> Unit
) {
    val leftItems = remember(matchPairs) { matchPairs.keys.toList() }
    val rightItems = remember(matchPairs) { matchPairs.values.shuffled() }

    var selectedLeft by remember(matchPairs) { mutableStateOf<String?>(null) }
    var selectedRight by remember(matchPairs) { mutableStateOf<String?>(null) }
    val matchedKeys = remember(matchPairs) { mutableStateListOf<String>() }

    var wrongPair by remember(matchPairs) { mutableStateOf<Pair<String, String>?>(null) }

    LaunchedEffect(wrongPair) {
        if (wrongPair != null) {
            delay(800)
            wrongPair = null
            selectedLeft = null
            selectedRight = null
        }
    }

    fun handleSelection(left: String?, right: String?) {
        val currLeft = left ?: selectedLeft
        val currRight = right ?: selectedRight

        if (currLeft != null && currRight != null) {
            val expectedRight = matchPairs[currLeft]
            if (expectedRight.equals(currRight, ignoreCase = true)) {
                // Correct match!
                matchedKeys.add(currLeft)
                selectedLeft = null
                selectedRight = null
                SoundEffectManager.playSuccess()

                if (matchedKeys.size == matchPairs.size) {
                    onMatchSuccess()
                }
            } else {
                // Wrong match!
                wrongPair = Pair(currLeft, currRight)
                SoundEffectManager.playError()
                onMismatch(currLeft, currRight, expectedRight ?: "")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "सही जोड़ों पर टैप करें (Tap matching pairs)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) GuruDarkText.copy(alpha = 0.7f) else GuruTextMuted,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val rowCount = maxOf(leftItems.size, rightItems.size)
        for (i in 0 until rowCount) {
            val leftItem = leftItems.getOrNull(i)
            val rightItem = rightItems.getOrNull(i)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left Column Card
                Box(modifier = Modifier.weight(1f)) {
                    if (leftItem != null) {
                        val isMatched = matchedKeys.contains(leftItem)
                        val isSelected = selectedLeft == leftItem
                        val isWrong = wrongPair?.first == leftItem

                        MatchTile(
                            text = leftItem,
                            isSelected = isSelected,
                            isMatched = isMatched,
                            isWrong = isWrong,
                            isDark = isDark,
                            onClick = {
                                if (!isMatched && wrongPair == null) {
                                    selectedLeft = if (selectedLeft == leftItem) null else leftItem
                                    handleSelection(selectedLeft, null)
                                }
                            }
                        )
                    }
                }

                // Right Column Card
                Box(modifier = Modifier.weight(1f)) {
                    if (rightItem != null) {
                        val matchedKeyForThisRight = matchPairs.entries.firstOrNull {
                            it.value.equals(rightItem, ignoreCase = true) && matchedKeys.contains(it.key)
                        }?.key
                        val isMatched = matchedKeyForThisRight != null
                        val isSelected = selectedRight == rightItem
                        val isWrong = wrongPair?.second == rightItem

                        MatchTile(
                            text = rightItem,
                            isSelected = isSelected,
                            isMatched = isMatched,
                            isWrong = isWrong,
                            isDark = isDark,
                            onClick = {
                                if (!isMatched && wrongPair == null) {
                                    selectedRight = if (selectedRight == rightItem) null else rightItem
                                    handleSelection(null, selectedRight)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchTile(
    text: String,
    isSelected: Boolean,
    isMatched: Boolean,
    isWrong: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(),
        label = "tileScale"
    )

    val bgColor by animateColorAsState(
        targetValue = when {
            isMatched -> if (isDark) Color(0xFF0F3824) else GuruMintLight
            isWrong -> if (isDark) Color(0xFF3F1318) else GuruRedLight
            isSelected -> if (isDark) Color(0xFF2E234D) else GuruPurpleLight
            else -> if (isDark) GuruDarkSurface else Color.White
        },
        label = "bgColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isMatched -> GuruMint
            isWrong -> GuruRed
            isSelected -> GuruPurple
            else -> if (isDark) GuruDarkCardBorder else GuruCardBorder
        },
        label = "borderColor"
    )

    val textColor = when {
        isMatched -> if (isDark) GuruMint else GuruMintDark
        isWrong -> GuruRed
        isSelected -> GuruPurple
        else -> if (isDark) GuruDarkText else GuruTextDark
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = if (isSelected || isMatched) 2.dp else 1.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(shape)
            .background(bgColor)
            .border(
                width = if (isSelected || isMatched || isWrong) 1.5.dp else 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                enabled = !isMatched,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected || isMatched) FontWeight.Bold else FontWeight.SemiBold,
            fontFamily = FontFamily.Default,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 3
        )
    }
}
