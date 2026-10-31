package com.spokenenglishguru.app.ui.screens.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.spokenenglishguru.app.data.db.entity.LessonEntity
import com.spokenenglishguru.app.ui.navigation.Screen
import com.spokenenglishguru.app.ui.theme.AmberAccent
import com.spokenenglishguru.app.ui.theme.IndigoLight
import com.spokenenglishguru.app.ui.theme.IndigoPrimary
import com.spokenenglishguru.app.ui.theme.TealAccent

@Composable
fun LessonsScreen(
    navController: NavController,
    viewModel: LessonsViewModel
) {
    val state by viewModel.uiState.collectAsState()

    val categories = listOf(
        "All",
        "Foundation & Concepts",
        "Grammar & Sentence Mechanics",
        "Vocabulary & Word Forms",
        "Applied Practice & Correction",
        "Quizzes & Mock Exams"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search 40 lessons (e.g. Tenses, Modals, Passive)...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = IndigoPrimary,
                unfocusedBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = state.selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setCategory(cat) },
                    label = { Text(cat, fontSize = 12.sp) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IndigoPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Lesson List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.lessons, key = { it.id }) { lesson ->
                LessonItemCard(
                    lesson = lesson,
                    onClick = {
                        navController.navigate(Screen.LessonDetail.createRoute(lesson.id))
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun LessonItemCard(
    lesson: LessonEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge with Lesson Number
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (lesson.category) {
                            "Vocabulary & Word Forms" -> TealAccent.copy(alpha = 0.15f)
                            "Quizzes & Mock Exams" -> AmberAccent.copy(alpha = 0.15f)
                            else -> IndigoPrimary.copy(alpha = 0.12f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${lesson.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = when (lesson.category) {
                        "Vocabulary & Word Forms" -> TealAccent
                        "Quizzes & Mock Exams" -> AmberAccent
                        else -> IndigoPrimary
                    }
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lesson.category,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${lesson.totalItems} Exercises",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AmberAccent
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
