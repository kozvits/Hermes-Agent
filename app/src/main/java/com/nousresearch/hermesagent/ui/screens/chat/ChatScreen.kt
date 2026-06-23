package com.nousresearch.hermesagent.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nousresearch.hermesagent.ui.components.*
import com.nousresearch.hermesagent.ui.theme.Dimens
import com.nousresearch.hermesagent.ui.theme.HermesStatusColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onOpenDrawer: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val chatState by viewModel.chatState.collectAsStateWithLifecycle()
    val streamState by viewModel.streamState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    var showNewChatDialog by remember { mutableStateOf(false) }

    // Auto-scroll on new messages
    LaunchedEffect(chatState.messages.size, streamState.accumulatedContent) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Hermes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Меню")
                    }
                },
                actions = {
                    ConnectionBadge(status = chatState.connectionStatus)

                    IconButton(onClick = { showNewChatDialog = true }) {
                        Icon(Icons.Outlined.AddComment, contentDescription = "Новый чат")
                    }

                    IconButton(onClick = { clearChat(chatState, viewModel) }) {
                        Icon(Icons.Outlined.DeleteSweep, contentDescription = "Очистить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            ChatInputBar(
                value = inputText,
                onValueChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                isStreaming = chatState.isStreaming,
                onStop = { /* stop generation would need a cancel mechanism */ },
                isConnected = chatState.connectionStatus == "connected",
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (chatState.messages.isEmpty() && !chatState.isStreaming) {
                // Empty state
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = Dimens.padding_xxl,
                    ),
                ) {
                    item {
                        EmptyWelcome(
                            connectionStatus = chatState.connectionStatus,
                            serverUrl = chatState.serverUrl,
                            currentModel = chatState.currentModel,
                            onConnect = { viewModel.connectToServer() },
                        )
                    }
                }
            } else {
                // Messages list
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Dimens.padding_lg,
                        end = Dimens.padding_lg,
                        top = Dimens.padding_sm,
                        bottom = Dimens.padding_xxl,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.padding_lg),
                ) {
                    items(
                        items = chatState.messages,
                        key = { "msg_${it.id}_${it.createdAt}" },
                    ) { message ->
                        MessageBubble(
                            content = message.content,
                            role = message.role,
                        )
                    }

                    // Streaming content
                    if (streamState.isActive) {
                        item(key = "streaming") {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                // Active tools
                                streamState.activeTools.forEach { tool ->
                                    ToolExecutionCard(
                                        toolName = tool.name,
                                        status = tool.status,
                                        output = tool.output,
                                    )
                                }

                                // Thinking indicator
                                if (streamState.isThinking) {
                                    ThinkingIndicator()
                                }

                                // Accumulated content
                                if (streamState.accumulatedContent.isNotBlank()) {
                                    MessageBubble(
                                        content = streamState.accumulatedContent,
                                        role = "assistant",
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Error snackbar
            chatState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Text(error)
                }
            }
        }
    }

    // New Chat Dialog
    if (showNewChatDialog) {
        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            title = { Text("Новый чат") },
            text = { Text("Начать новую сессию? Текущая история будет сохранена.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.createNewSession()
                    showNewChatDialog = false
                }) {
                    Text("Создать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewChatDialog = false }) {
                    Text("Отмена")
                }
            },
        )
    }
}

@Composable
private fun EmptyWelcome(
    connectionStatus: String,
    serverUrl: String,
    currentModel: String,
    onConnect: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Animated logo area
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "🤖",
                fontSize = 56.sp,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Hermes Agent",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ваш AI-помощник с полным доступом к инструментам",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Status info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow("Сервер", serverUrl)
                InfoRow("Модель", currentModel.ifBlank { "По умолчанию" })
                InfoRow("Статус", when (connectionStatus) {
                    "connected" -> "Подключено"
                    "connecting" -> "Подключение…"
                    "error" -> "Ошибка"
                    else -> "Не подключено"
                })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (connectionStatus != "connected") {
            FilledTonalButton(
                onClick = onConnect,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Default.Cable, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Подключиться к серверу")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Начните вводить сообщение в поле ниже!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun clearChat(state: ChatState, viewModel: ChatViewModel) {
    if (state.messages.isNotEmpty()) {
        viewModel.clearChat()
    }
}

// ── Chat Input Bar ──
@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isStreaming: Boolean,
    onStop: () -> Unit,
    isConnected: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = Dimens.padding_sm,
                    end = Dimens.padding_sm,
                    top = Dimens.padding_sm,
                    bottom = Dimens.padding_sm,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Input field
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Сообщение Hermes…",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                ),
                maxLines = 6,
                textStyle = MaterialTheme.typography.bodyMedium,
                enabled = !isStreaming,
            )

            if (isStreaming) {
                // Stop button
                FilledIconButton(
                    onClick = onStop,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                    modifier = Modifier.size(Dimens.min_touch),
                ) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = "Остановить",
                        tint = MaterialTheme.colorScheme.onError,
                    )
                }
            } else {
                // Send button
                FilledIconButton(
                    onClick = onSend,
                    enabled = value.isNotBlank(),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    modifier = Modifier.size(Dimens.min_touch),
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Отправить",
                        tint = if (value.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
