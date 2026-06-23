package com.nousresearch.hermesagent.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nousresearch.hermesagent.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(
    serverUrl: String,
    connectionStatus: String,
    onConnect: (String) -> Unit,
    onDisconnect: () -> Unit,
) {
    var urlInput by remember { mutableStateOf(serverUrl) }
    var apiKeyInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Подключение", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.padding_lg),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Connection status card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = when (connectionStatus) {
                        "connected" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        "error" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                ),
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.padding_lg),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = when (connectionStatus) {
                            "connected" -> Icons.Default.CheckCircle
                            "error" -> Icons.Default.Error
                            "connecting" -> Icons.Default.Sync
                            else -> Icons.Default.Cable
                        },
                        contentDescription = null,
                        tint = when (connectionStatus) {
                            "connected" -> MaterialTheme.colorScheme.primary
                            "error" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(40.dp),
                    )
                    Column {
                        Text(
                            text = when (connectionStatus) {
                                "connected" -> "Подключено"
                                "connecting" -> "Подключение…"
                                "error" -> "Ошибка подключения"
                                else -> "Не подключено"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        if (connectionStatus == "connected") {
                            Text(
                                text = serverUrl,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Server URL
            Text(
                "Сервер",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
            OutlinedTextField(
                value = urlInput,
                onValueChange = { urlInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("http://10.0.2.2:8080") },
                label = { Text("URL сервера") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Link, contentDescription = null)
                },
            )

            // API Key
            Text(
                "API Ключ (опционально)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
            )
            OutlinedTextField(
                value = apiKeyInput,
                onValueChange = { apiKeyInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("sk-…") },
                label = { Text("API Key") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Key, contentDescription = null)
                },
            )

            Spacer(Modifier.height(8.dp))

            // Connect / Disconnect button
            if (connectionStatus == "connected") {
                Button(
                    onClick = onDisconnect,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Отключиться")
                }
            } else {
                Button(
                    onClick = { onConnect(urlInput) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = urlInput.isNotBlank() && connectionStatus != "connecting",
                ) {
                    if (connectionStatus == "connecting") {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(Modifier.width(8.dp))
                    } else {
                        Icon(Icons.Default.Cable, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text("Подключиться")
                }
            }

            // Info
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(modifier = Modifier.padding(Dimens.padding_lg)) {
                    Text(
                        "ℹ️ Информация",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Приложение подключается к серверу Hermes Agent, " +
                                "который запущен на вашем компьютере. Убедитесь, что " +
                                "Hermes Agent запущен и доступен по указанному URL.\n\n" +
                                "Для эмулятора Android используйте http://10.0.2.2:8080\n" +
                                "Для реального устройства — IP вашего компьютера в локальной сети.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
