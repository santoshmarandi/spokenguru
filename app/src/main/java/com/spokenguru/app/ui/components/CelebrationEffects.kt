package com.spokenguru.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val angle: Float,
    val speed: Float,
    val size: Float,
    val color: Color
)

/**
 * Returns an animated horizontal pixel offset that shakes briefly whenever [triggerCount] > 0 increments.
 */
@Composable
fun rememberShakeOffset(triggerCount: Int): Float {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(triggerCount) {
        if (triggerCount > 0) {
            val sequence = listOf(-18f, 18f, -14f, 14f, -8f, 8f, 0f)
            for (target in sequence) {
                anim.animateTo(target, animationSpec = tween(durationMillis = 42))
            }
        }
    }
    return anim.value
}

/**
 * Lightweight 60fps Canvas confetti burst + floating XP badge triggered on correct answers.
 */
@Composable
fun CelebrationBurstOverlay(
    triggerCount: Int,
    xpText: String = "+10 XP",
    modifier: Modifier = Modifier
) {
    if (triggerCount <= 0) return

    val colors = remember {
        listOf(
            Color(0xFF10B981), // Mint
            Color(0xFF6366F1), // Purple
            Color(0xFFF59E0B), // Amber
            Color(0xFF3B82F6), // Blue
            Color(0xFFEC4899)  // Pink
        )
    }

    val particles = remember(triggerCount) {
        val rng = Random(triggerCount * 31 + 7)
        List(26) {
            ConfettiParticle(
                angle = rng.nextFloat() * 360f,
                speed = 260f + rng.nextFloat() * 440f,
                size = 6f + rng.nextFloat() * 7f,
                color = colors[it % colors.size]
            )
        }
    }

    val progress = remember(triggerCount) { Animatable(0f) }
    var visible by remember(triggerCount) { mutableStateOf(true) }

    LaunchedEffect(triggerCount) {
        visible = true
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
        )
        visible = false
    }

    if (visible) {
        val p = progress.value
        val fadeAlpha = if (p < 0.7f) 1f else ((1f - p) / 0.3f).coerceIn(0f, 1f)

        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val origin = Offset(size.width * 0.5f, size.height * 0.42f)
                particles.forEach { particle ->
                    val rad = Math.toRadians(particle.angle.toDouble())
                    val dist = particle.speed * p
                    val gravity = 280f * p * p
                    val x = origin.x + (cos(rad) * dist).toFloat()
                    val y = origin.y + (sin(rad) * dist).toFloat() + gravity
                    drawCircle(
                        color = particle.color.copy(alpha = fadeAlpha),
                        radius = particle.size * (1f - 0.35f * p),
                        center = Offset(x, y)
                    )
                }
            }

            // Floating XP pop pill
            val badgeScale = if (p < 0.2f) 0.6f + (p / 0.2f) * 0.55f else 1.15f - ((p - 0.2f) * 0.18f)
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, (-120 * p).roundToInt()) }
                    .scale(badgeScale)
                    .alpha(fadeAlpha)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF10B981))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "✨ $xpText",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
