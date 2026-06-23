package com.nousresearch.hermesagent.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Navigation routes for the Hermes Agent app.
 * Each screen is defined as a sealed class route with icon, label and path.
 */
sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector? = null,
    val selectedIcon: ImageVector? = null,
) {
    // ── Bottom nav destinations ──
    data object Chat : Screen(
        route = "chat/{sessionId}",
        label = "Чат",
        icon = Icons.Default.Chat,
        selectedIcon = Icons.Default.Forum,
    )

    data object Memory : Screen(
        route = "memory",
        label = "Память",
        icon = Icons.Default.Psychology,
        selectedIcon = Icons.Default.AutoAwesome,
    )

    data object Sessions : Screen(
        route = "sessions",
        label = "Сессии",
        icon = Icons.Default.History,
        selectedIcon = Icons.Default.HistoryToggleOff,
    )

    data object Settings : Screen(
        route = "settings",
        label = "Настройки",
        icon = Icons.Default.Settings,
        selectedIcon = Icons.Default.Tune,
    )

    // ── Drawer destinations ──
    data object Skills : Screen(
        route = "skills",
        label = "Навыки",
        icon = Icons.Default.AutoAwesome,
    )

    data object Cron : Screen(
        route = "cron",
        label = "Cron",
        icon = Icons.Default.Schedule,
    )

    data object Providers : Screen(
        route = "providers",
        label = "Провайдеры",
        icon = Icons.Default.Cloud,
    )

    data object Tools : Screen(
        route = "tools",
        label = "Инструменты",
        icon = Icons.Default.Build,
    )

    data object About : Screen(
        route = "about",
        label = "О программе",
        icon = Icons.Default.Info,
    )

    data object Connection : Screen(
        route = "connection",
        label = "Подключение",
        icon = Icons.Default.Cable,
    )

    companion object {
        val bottomNavItems = listOf(Chat, Memory, Sessions, Settings)

        val drawerItems = listOf(
            Chat, Memory, Sessions, Skills, Cron, Providers, Tools, Connection, About,
        )

        fun chatRoute(sessionId: String = "{sessionId}") = "chat/$sessionId"
    }
}
