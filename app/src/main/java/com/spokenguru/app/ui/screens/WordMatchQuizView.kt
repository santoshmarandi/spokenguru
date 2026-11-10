package com.spokenguru.app.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import com.spokenguru.app.ui.theme.isAppDarkTheme
import kotlinx.coroutines.delay

private data class MatchColumnItem(
    val pairIndex: Int,
    val text: String
)

@Composable
fun WordMatchQuizView(
    matchPairs: Map<String, String>,
    isDark: Boolean = isAppDarkTheme(),
    onPairMatched: ((left: String, right: String) -> Unit)? = null,
    onMatchSuccess: () -> Unit,
    onMismatch: (userLeft: String, userRight: String, expectedRight: String) -> Unit
) {
    val pairEntries = remember(matchPairs) { matchPairs.entries.toList() }
    val leftItems = remember(pairEntries) {
        pairEntries.mapIndexed { idx, entry -> MatchColumnItem(idx, entry.key) }
    }
    val rightItems = remember(pairEntries) {
        pairEntries.mapIndexed { idx, entry -> MatchColumnItem(idx, entry.value) }.shuffled()
    }

    var selectedLeft by remember(matchPairs) { mutableStateOf<MatchColumnItem?>(null) }
    var selectedRight by remember(matchPairs) { mutableStateOf<MatchColumnItem?>(null) }
    val matchedLeftIndices = remember(matchPairs) { mutableStateListOf<Int>() }
    val matchedRightIndices = remember(matchPairs) { mutableStateListOf<Int>() }

    var wrongPair by remember(matchPairs) { mutableStateOf<Pair<MatchColumnItem, MatchColumnItem>?>(null) }

    LaunchedEffect(wrongPair) {
        if (wrongPair != null) {
            delay(800)
            wrongPair = null
            selectedLeft = null
            selectedRight = null
        }
    }

    fun handleSelection(left: MatchColumnItem?, right: MatchColumnItem?) {
        val currLeft = left ?: selectedLeft
        val currRight = right ?: selectedRight

        if (currLeft != null && currRight != null) {
            val expectedRight = pairEntries[currLeft.pairIndex].value
            val isSamePair = currLeft.pairIndex == currRight.pairIndex ||
                expectedRight.equals(currRight.text, ignoreCase = true)

            if (isSamePair) {
                matchedLeftIndices.add(currLeft.pairIndex)
                matchedRightIndices.add(currRight.pairIndex)
                selectedLeft = null
                selectedRight = null
                SoundEffectManager.playSuccess()
                onPairMatched?.invoke(currLeft.text, currRight.text)

                if (matchedLeftIndices.size >= pairEntries.size) {
                    onMatchSuccess()
                }
            } else {
                wrongPair = Pair(currLeft, currRight)
                SoundEffectManager.playError()
                onMismatch(currLeft.text, currRight.text, expectedRight)
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
            text = "सही जोड़ों पर टैप करें (${matchedLeftIndices.size}/${pairEntries.size} matched)",
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
                        val isMatched = matchedLeftIndices.contains(leftItem.pairIndex)
                        val isSelected = selectedLeft == leftItem
                        val isWrong = wrongPair?.first == leftItem

                        MatchTile(
                            text = leftItem.text,
                            isSelected = isSelected,
                            isMatched = isMatched,
                            isWrong = isWrong,
                            isDark = isDark,
                            onClick = {
                                if (!isMatched && wrongPair == null) {
                                    val nextLeft = if (selectedLeft == leftItem) null else leftItem
                                    selectedLeft = nextLeft
                                    handleSelection(nextLeft, selectedRight)
                                }
                            }
                        )
                    }
                }

                // Right Column Card
                Box(modifier = Modifier.weight(1f)) {
                    if (rightItem != null) {
                        val isMatched = matchedRightIndices.contains(rightItem.pairIndex)
                        val isSelected = selectedRight == rightItem
                        val isWrong = wrongPair?.second == rightItem

                        MatchTile(
                            text = rightItem.text,
                            isSelected = isSelected,
                            isMatched = isMatched,
                            isWrong = isWrong,
                            isDark = isDark,
                            onClick = {
                                if (!isMatched && wrongPair == null) {
                                    val nextRight = if (selectedRight == rightItem) null else rightItem
                                    selectedRight = nextRight
                                    handleSelection(selectedLeft, nextRight)
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
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tileScale"
    )
    val matchBounce = remember { Animatable(1f) }
    LaunchedEffect(isMatched) {
        if (isMatched) {
            matchBounce.animateTo(1.07f, animationSpec = tween(110))
            matchBounce.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }
    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(isWrong) {
        if (isWrong) {
            for (offset in listOf(-14f, 14f, -10f, 10f, -6f, 0f)) {
                shakeAnim.animateTo(offset, animationSpec = tween(42))
            }
        }
    }

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
            .offset { IntOffset(shakeAnim.value.roundToInt(), 0) }
            .scale(pressScale * matchBounce.value)
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
            .padding(horizontal = 10.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected || isMatched) FontWeight.Bold else FontWeight.SemiBold,
            color = textColor,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
