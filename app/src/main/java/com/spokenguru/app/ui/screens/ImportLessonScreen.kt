package com.spokenguru.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.spokenguru.app.ui.theme.isAppDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.components.GuruButton
import com.spokenguru.app.ui.components.GuruButtonVariant
import com.spokenguru.app.ui.theme.GuruBg
import com.spokenguru.app.ui.theme.GuruBlue
import com.spokenguru.app.ui.theme.GuruCardBorder
import com.spokenguru.app.ui.theme.GuruDarkBg
import com.spokenguru.app.ui.theme.GuruDarkCardBorder
import com.spokenguru.app.ui.theme.GuruDarkSurface
import com.spokenguru.app.ui.theme.GuruDarkText
import com.spokenguru.app.ui.theme.GuruPurple
import com.spokenguru.app.ui.theme.GuruPurpleLight
import com.spokenguru.app.ui.theme.GuruRed
import com.spokenguru.app.ui.theme.GuruTextDark
import com.spokenguru.app.ui.theme.GuruTextMuted
import com.spokenguru.app.ui.theme.GuruTextSubtle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportLessonScreen(
    repository: LessonRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isDark = isAppDarkTheme()

    var dbStats by remember { mutableStateOf("Loading database stats...") }
    var lessonNumberText by remember { mutableStateOf("2") }
    var jsonInput by remember { mutableStateOf("") }
    var isImporting by remember { mutableStateOf(false) }

    fun refreshStats() {
        scope.launch {
            dbStats = repository.getDbStats()
        }
    }

    LaunchedEffect(Unit) {
        refreshStats()
    }

    val sampleLesson2Json = """[
  {
    "questionNumber": 1,
    "hindiText": "क्या समय हुआ है?",
    "romanText": "Kya samay hua hai?",
    "primaryAnswer": "What is the time?",
    "alternateAnswers": ["What's the time?"],
    "distractors": ["where", "how", "this", "that"],
    "hint": "Use 'What is the time?' or 'What's the time?'",
    "ruleTitle": "Time Expressions",
    "grammarRule": "Time poochne ke liye 'What is the time?' ka prayog hota hai.",
    "tagKeyword": "Time (समय)",
    "categoryTag": "WH Question",
    "wordGlosses": {
      "what": "क्या",
      "is": "है",
      "the": "-",
      "time": "समय",
      "where": "कहाँ",
      "how": "कैसे"
    }
  }
]"""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Manage & Import Lessons",
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) GuruDarkSurface else Color.White
                )
            )
        },
        containerColor = if (isDark) GuruDarkBg else GuruBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val cardShape = RoundedCornerShape(22.dp)

            // Database stats
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, cardShape)
                    .clip(cardShape)
                    .background(if (isDark) GuruDarkSurface else Color.White)
                    .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ROOM SQLITE STATUS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GuruPurple,
                            letterSpacing = 1.sp
                        )
                        IconButton(onClick = { refreshStats() }, modifier = Modifier.size(24.dp)) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = GuruPurple)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = dbStats,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                }
            }

            // Reset progress
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, cardShape)
                    .clip(cardShape)
                    .background(if (isDark) GuruDarkSurface else Color.White)
                    .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reset Lesson 1 Progress",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        Text(
                            text = "Clear completed questions count to restart fresh",
                            fontSize = 12.sp,
                            color = GuruTextMuted
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    GuruButton(
                        text = "RESET",
                        variant = GuruButtonVariant.SECONDARY,
                        height = 38.dp,
                        onClick = {
                            scope.launch {
                                repository.resetLesson(1)
                                refreshStats()
                                Toast.makeText(context, "Lesson 1 progress reset", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // JSON Import Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, cardShape)
                    .clip(cardShape)
                    .background(if (isDark) GuruDarkSurface else Color.White)
                    .border(1.dp, if (isDark) GuruDarkCardBorder else GuruCardBorder, cardShape)
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "ADD / UPDATE LESSON CONTENT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GuruPurple,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Target Lesson Number (1 – 40):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) GuruDarkText else GuruTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = lessonNumberText,
                        onValueChange = { lessonNumberText = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Questions JSON:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) GuruDarkText else GuruTextDark
                        )
                        TextButton(onClick = { jsonInput = sampleLesson2Json }) {
                            Text(text = "Paste Sample JSON", color = GuruPurple, fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = jsonInput,
                        onValueChange = { jsonInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(14.dp),
                        placeholder = { Text(text = "Paste JSON array here...") },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GuruButton(
                        text = if (isImporting) "IMPORTING..." else "SAVE TO DATABASE",
                        enabled = !isImporting && jsonInput.isNotBlank() && lessonNumberText.isNotBlank(),
                        onClick = {
                            val targetLesson = lessonNumberText.toIntOrNull() ?: 1
                            isImporting = true
                            scope.launch {
                                val result = repository.importQuestionsJson(targetLesson, jsonInput)
                                isImporting = false
                                if (result.isSuccess) {
                                    val count = result.getOrNull() ?: 0
                                    Toast.makeText(
                                        context,
                                        "Successfully imported $count questions into Lesson $targetLesson!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    refreshStats()
                                    jsonInput = ""
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Import error: ${result.exceptionOrNull()?.localizedMessage}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
