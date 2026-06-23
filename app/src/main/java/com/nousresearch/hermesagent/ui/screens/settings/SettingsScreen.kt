package com.nousresearch.hermesagent.ui.screens.settings

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
import com.nousresearch.hermesagent.data.local.PreferencesManager
import com.nousresearch.hermesagent.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferencesManager: PreferencesManager?,
    onNavigateToConnection: () -> Unit = {},
    onNavigateToProviders: () -> Unit = {},
    onNavigateToTools: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
) {
    var themeMode by remember { mutableStateOf("system") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Настройки",
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(Dimens.padding_lg),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // ── Server Section ──
            item {
                SectionHeader("Сервер")
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.Cable,
                    title = "Подключение",
                    subtitle = "Настройка URL сервера и API ключа",
                    onClick = onNavigateToConnection,
                )
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.Cloud,
                    title = "Провайдеры",
                    subtitle = "Управление AI провайдерами и моделями",
                    onClick = onNavigateToProviders,
                )
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.Build,
                    title = "Инструменты",
                    subtitle = "Включение/отключение инструментов",
                    onClick = onNavigateToTools,
                )
            }

            // ── Appearance Section ──
            item {
                Spacer(Modifier.height(8.dp))
                SectionHeader("Внешний вид")
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(modifier = Modifier.padding(Dimens.padding_lg)) {
                        Text(
                            "Тема оформления",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            listOf("system" to "Системная", "light" to "Светлая", "dark" to "Тёмная")
                                .forEach { (key, label) ->
                                    FilterChip(
                                        selected = themeMode == key,
                                        onClick = {
                                            themeMode = key
                                        },
                                        label = { Text(label) },
                                    )
                                }
                        }
                    }
                }
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.Palette,
                    title = "Динамические цвета",
                    subtitle = "Использовать цвета Material You",
                    trailing = {
                        Switch(
                            checked = true,
                            onCheckedChange = { },
                        )
                    },
                    onClick = {},
                )
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.ViewCompactAlt,
                    title = "Компактный режим",
                    subtitle = "Более плотное отображение сообщений",
                    trailing = {
                        Switch(
                            checked = false,
                            onCheckedChange = { },
                        )
                    },
                    onClick = {},
                )
            }

            // ── Chat Section ──
            item {
                Spacer(Modifier.height(8.dp))
                SectionHeader("Чат")
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.Stream,
                    title = "Потоковый режим",
                    subtitle = "Постепенная генерация ответа",
                    trailing = {
                        Switch(
                            checked = true,
                            onCheckedChange = { },
                        )
                    },
                    onClick = {},
                )
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.Mic,
                    title = "Голосовой ввод",
                    subtitle = "Отправка голосовых сообщений",
                    trailing = {
                        Switch(
                            checked = false,
                            onCheckedChange = { },
                        )
                    },
                    onClick = {},
                )
            }

            // ── Model Section ──
            item {
                Spacer(Modifier.height(8.dp))
                SectionHeader("Модель")
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.Thermostat,
                    title = "Температура",
                    subtitle = "Креативность ответов (0.0 - 2.0)",
                    onClick = {},
                )
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.Token,
                    title = "Max токенов",
                    subtitle = "Максимальная длина ответа",
                    onClick = {},
                )
            }

            // ── Data Section ──
            item {
                Spacer(Modifier.height(8.dp))
                SectionHeader("Данные")
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.Psychology,
                    title = "Память",
                    subtitle = "Управление долговременной памятью",
                    onClick = { },
                )
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.DeleteForever,
                    title = "Очистить данные",
                    subtitle = "Удалить все локальные сессии и сообщения",
                    onClick = { },
                    tintColor = MaterialTheme.colorScheme.error,
                )
            }

            // ── About Section ──
            item {
                Spacer(Modifier.height(8.dp))
                SectionHeader("О приложении")
            }

            item {
                SettingsCard(
                    icon = Icons.Outlined.Info,
                    title = "О программе",
                    subtitle = "Версия 1.0.0",
                    onClick = onNavigateToAbout,
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
    )
}

@Composable
fun SettingsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null,
    tintColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.padding_lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(Dimens.icon_size),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (trailing != null) {
                trailing()
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
