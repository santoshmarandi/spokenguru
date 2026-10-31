package com.spokenenglishguru.app.ui.screens.vocab

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.spokenenglishguru.app.data.db.entity.VocabEntity
import com.spokenenglishguru.app.ui.theme.AmberAccent
import com.spokenenglishguru.app.ui.theme.IndigoPrimary
import com.spokenenglishguru.app.ui.theme.StarYellow
import com.spokenenglishguru.app.ui.theme.TealAccent
import com.spokenenglishguru.app.util.TextToSpeechManager

@Composable
fun VocabScreen(
    navController: NavController,
    viewModel: VocabViewModel,
    ttsManager: TextToSpeechManager
) {
    val items by viewModel.itemsFlow.collectAsState()
    var selectedCat by remember { mutableStateOf("VERB") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf(
        "VERB" to "Verbs (V1, V2, V3)",
        "ANTONYM" to "Antonyms (Opposites)",
        "SYNONYM" to "Synonyms",
        "ADJECTIVE" to "Adjectives (Degrees)",
        "ADVERB" to "Adverbs (Degrees)"
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
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.search(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search word or Hindi meaning...") },
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

        // Category Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (key, label) ->
                val isSelected = selectedCat == key
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedCat = key
                        searchQuery = ""
                        viewModel.loadCategory(key)
                    },
                    label = { Text(label, fontSize = 12.sp) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IndigoPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Vocab Items List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items, key = { it.id }) { item ->
                VocabItemCard(
                    item = item,
                    onSpeak = { ttsManager.speak(item.word) },
                    onToggleFavorite = { viewModel.toggleFavorite(item.id, item.isFavorite) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun VocabItemCard(
    item: VocabEntity,
    onSpeak: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.word,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!item.hindiMeaning.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.hindiMeaning,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AmberAccent
                        )
                    }
                    if (!item.romanized.isNullOrBlank()) {
                        Text(
                            text = item.romanized,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSpeak, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speak",
                            tint = TealAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (item.isFavorite == 1) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite == 1) StarYellow else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Forms row (e.g. V2, V3 or Opposite or Comparative/Superlative)
            if (!item.form2.isNullOrBlank() || !item.form3.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    when (item.category) {
                        "VERB" -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "V1: ${item.word}", fontSize = 12.sp, color = IndigoPrimary, fontWeight = FontWeight.Bold)
                                Text(text = "V2: ${item.form2 ?: "-"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(text = "V3: ${item.form3 ?: "-"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                        "ANTONYM" -> {
                            Text(
                                text = "Opposite: ${item.form2 ?: ""} ${if (!item.form3.isNullOrBlank()) "(${item.form3})" else ""}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = IndigoPrimary
                            )
                        }
                        "SYNONYM" -> {
                            Text(
                                text = "Synonym: ${item.form2 ?: ""}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = IndigoPrimary
                            )
                        }
                        else -> {
                            Text(
                                text = item.extraInfo ?: "${item.form2} - ${item.form3}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
