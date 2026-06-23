package com.nousresearch.hermesagent.ui.screens.memory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nousresearch.hermesagent.data.api.models.MemoryEntry
import com.nousresearch.hermesagent.ui.components.EmptyState
import com.nousresearch.hermesagent.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    entries: List<MemoryEntry>,
    isLoading: Boolean,
    onAddEntry: (String, String) -> Unit,
    onDeleteEntry: (String) -> Unit,
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Память", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    EmptyState(
                        title = "Память пуста",
                        subtitle = "Долговременная память позволяет Hermes помнить вас между сессиями",
                    )
                    Spacer(Modifier.height(16.dp))
                    FilledTonalButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Добавить запись")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(Dimens.padding_lg),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Text(
                        "Память хранит информацию между сессиями. Hermes использует её, чтобы помнить о вас.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

                items(entries, key = { it.id ?: it.content }) { entry ->
                    MemoryCard(
                        entry = entry,
                        onDelete = { entry.id?.let { onDeleteEntry(it) } },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        var content by remember { mutableStateOf("") }
        var selectedTarget by remember { mutableStateOf("memory") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Добавить запись") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Содержание") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedTarget == "memory",
                            onClick = { selectedTarget = "memory" },
                            label = { Text("Память") },
                        )
                        FilterChip(
                            selected = selectedTarget == "user",
                            onClick = { selectedTarget = "user" },
                            label = { Text("Профиль") },
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (content.isNotBlank()) {
                            onAddEntry(content, selectedTarget)
                            showAddDialog = false
                        }
                    },
                    enabled = content.isNotBlank(),
                ) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Отмена") }
            },
        )
    }
}

@Composable
private fun MemoryCard(
    entry: MemoryEntry,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.padding_lg),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = if (entry.target == "user") Icons.Outlined.Person
                else Icons.Outlined.Psychology,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp).padding(top = 2.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (entry.createdAt != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = entry.createdAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Удалить",
                    modifier = Modifier.size(Dimens.icon_size_sm),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
