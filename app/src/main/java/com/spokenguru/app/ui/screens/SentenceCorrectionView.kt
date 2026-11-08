package com.spokenguru.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruRedLight
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.isAppDarkTheme

@Composable
fun SentenceCorrectionView(
    incorrectSentence: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = isAppDarkTheme()
) {
    val shape = RoundedCornerShape(18.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Erroneous Sentence Warning Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, shape, ambientColor = GuruRed.copy(alpha = 0.08f))
                .clip(shape)
                .background(if (isDark) Color(0xFF2E1318) else GuruRedLight)
                .border(1.dp, GuruRed.copy(alpha = 0.4f), shape)
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = GuruRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "अशुद्ध वाक्य (Spot the Error):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuruRed
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = incorrectSentence,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Default,
                    color = if (isDark) GuruDarkText else GuruTextDark
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "सही वाक्य का चुनाव करें (Choose the correct version):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) GuruDarkText.copy(alpha = 0.7f) else GuruTextMuted,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // 4 Option Cards
        val labels = listOf("A", "B", "C", "D")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEachIndexed { index, option ->
                val label = labels.getOrElse(index) { "${index + 1}" }
                val isSelected = selectedOption == option

                McqOptionCard(
                    label = label,
                    text = option,
                    isSelected = isSelected,
                    isDark = isDark,
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}
