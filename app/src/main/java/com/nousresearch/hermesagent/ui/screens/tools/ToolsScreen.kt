package com.nousresearch.hermesagent.ui.screens.tools

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nousresearch.hermesagent.data.api.models.ToolInfo
import com.nousresearch.hermesagent.ui.components.EmptyState
import com.nousresearch.hermesagent.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    tools: List<ToolInfo>,
    isLoading: Boolean,
    onToggle: (String) -> Unit,
) {
    // Group by toolset
    val grouped = tools.groupBy { it.toolset }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Инструменты", fontWeight = FontWeight.Bold) },
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
        } else if (tools.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                EmptyState(
                    title = "Нет инструментов",
                    subtitle = "Инструменты расширяют возможности Hermes",
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(Dimens.padding_lg),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                grouped.forEach { (toolset, items) ->
                    item {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = toolset.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        )
                    }

                    items(items = items, key = { it.name }) { tool ->
                        ToolCard(
                            tool = tool,
                            onToggle = { onToggle(tool.name) },
                        )
                    }
                }

                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun ToolCard(
    tool: ToolInfo,
    onToggle: () -> Unit,
) {
    val icon = when (tool.name) {
        "web_search", "web_extract" -> Icons.Outlined.Language
        "terminal" -> Icons.Outlined.Terminal
        "read_file", "write_file", "patch", "search_files" -> Icons.Outlined.Description
        "execute_code" -> Icons.Outlined.Code
        "browser_navigate", "browser_click", "browser_type" -> Icons.Outlined.TravelExplore
        "vision_analyze" -> Icons.Outlined.Image
        "text_to_speech" -> Icons.Outlined.RecordVoiceOver
        "memory", "session_search" -> Icons.Outlined.Psychology
        "clarify" -> Icons.Outlined.QuestionAnswer
        "delegate_task" -> Icons.Outlined.AccountTree
        "cronjob" -> Icons.Outlined.Schedule
        "todo" -> Icons.Outlined.Checklist
        "skill_view", "skill_manage" -> Icons.Outlined.AutoAwesome
        else -> Icons.Outlined.Build
    }

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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.icon_size),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tool.displayName ?: tool.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )
                if (tool.description.isNotBlank()) {
                    Text(
                        text = tool.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Switch(
                checked = tool.enabled,
                onCheckedChange = { onToggle() },
            )
        }
    }
}
