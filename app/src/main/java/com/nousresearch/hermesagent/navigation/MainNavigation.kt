package com.nousresearch.hermesagent.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nousresearch.hermesagent.data.local.PreferencesManager
import com.nousresearch.hermesagent.data.local.SessionEntity
import com.nousresearch.hermesagent.ui.screens.chat.ChatScreen
import com.nousresearch.hermesagent.ui.screens.chat.ChatViewModel
import com.nousresearch.hermesagent.ui.screens.cron.CronScreen
import com.nousresearch.hermesagent.ui.screens.memory.MemoryScreen
import com.nousresearch.hermesagent.ui.screens.providers.ProvidersScreen
import com.nousresearch.hermesagent.ui.screens.sessions.SessionsScreen
import com.nousresearch.hermesagent.ui.screens.settings.*
import com.nousresearch.hermesagent.ui.screens.skills.SkillsScreen
import com.nousresearch.hermesagent.ui.screens.tools.ToolsScreen
import com.nousresearch.hermesagent.ui.theme.Dimens
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainNavigation(
    preferencesManager: PreferencesManager,
    chatViewModel: ChatViewModel,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val chatState by chatViewModel.chatState.collectAsStateWithLifecycle()
    val isInitialized = chatState.currentSessionId.isNotEmpty()

    // Пока сессия не инициализирована — показываем лоадер
    if (!isInitialized) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        // Запускаем инициализацию (если ещё не запущена)
        LaunchedEffect(Unit) {
            // init в ChatViewModel уже запущен
        }
        return
    }

    // Determine if we should show bottom bar
    val showBottomBar = currentRoute in listOf(
        Screen.Chat.route,
        Screen.Memory.route,
        Screen.Sessions.route,
        Screen.Settings.route,
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(Dimens.drawer_width),
            ) {
                DrawerHeader(
                    connectionStatus = chatState.connectionStatus,
                    serverUrl = chatState.serverUrl,
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = Dimens.padding_lg))

                Spacer(Modifier.height(8.dp))

                // Navigation items
                Screen.drawerItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route ||
                            (screen == Screen.Chat && currentRoute?.startsWith("chat/") == true)

                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon ?: screen.icon!!
                                else screen.icon!!,
                                contentDescription = null,
                            )
                        },
                        label = { Text(screen.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        selected = isSelected,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navigateToScreen(navController, screen, chatState.currentSessionId)
                        },
                        modifier = Modifier.padding(horizontal = Dimens.padding_sm),
                    )
                }

                Spacer(Modifier.weight(1f))

                HorizontalDivider(modifier = Modifier.padding(horizontal = Dimens.padding_lg))

                DrawerFooter(
                    onAbout = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.About.route)
                    },
                )
            }
        },
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        Screen.bottomNavItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route ||
                                    (screen == Screen.Chat && currentRoute?.startsWith("chat/") == true)

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon ?: screen.icon!!
                                        else screen.icon!!,
                                        contentDescription = screen.label,
                                    )
                                },
                                label = { Text(screen.label) },
                                selected = isSelected,
                                onClick = {
                                    navigateToScreen(navController, screen, chatState.currentSessionId)
                                },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.chatRoute(chatState.currentSessionId),
                    enterTransition = { fadeIn(animationSpec = tween(300)) },
                    exitTransition = { fadeOut(animationSpec = tween(300)) },
                ) {
                    // Chat
                    composable(
                        route = Screen.Chat.route,
                        arguments = listOf(navArgument("sessionId") { type = NavType.StringType }),
                    ) {
                        ChatScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            viewModel = chatViewModel,
                        )
                    }

                    // Memory
                    composable(route = Screen.Memory.route) {
                        MemoryScreen(
                            entries = emptyList(),
                            isLoading = false,
                            onAddEntry = { _, _ -> },
                            onDeleteEntry = { },
                        )
                    }

                    // Sessions
                    composable(route = Screen.Sessions.route) {
                        val sessions by chatViewModel.chatState.collectAsStateWithLifecycle()
                        SessionsScreen(
                            sessions = emptyList(),
                            currentSessionId = sessions.currentSessionId,
                            onSessionClick = { sessionId ->
                                chatViewModel.switchSession(sessionId)
                                navController.navigate(Screen.chatRoute(sessionId)) {
                                    popUpTo(Screen.Sessions.route) { inclusive = true }
                                }
                            },
                            onDeleteSession = { chatViewModel.deleteSession(it) },
                            onRenameSession = { id, title -> chatViewModel.renameSession(id, title) },
                            onNewSession = {
                                chatViewModel.createNewSession()
                                navController.navigate(Screen.chatRoute(chatState.currentSessionId)) {
                                    popUpTo(Screen.Sessions.route) { inclusive = true }
                                }
                            },
                        )
                    }

                    // Settings
                    composable(route = Screen.Settings.route) {
                        SettingsScreen(
                            preferencesManager = preferencesManager,
                            onNavigateToConnection = {
                                navController.navigate(Screen.Connection.route)
                            },
                            onNavigateToProviders = {
                                navController.navigate(Screen.Providers.route)
                            },
                            onNavigateToTools = {
                                navController.navigate(Screen.Tools.route)
                            },
                            onNavigateToAbout = {
                                navController.navigate(Screen.About.route)
                            },
                        )
                    }

                    // Skills
                    composable(route = Screen.Skills.route) {
                        SkillsScreen(
                            skills = emptyList(),
                            isLoading = false,
                            onToggleSkill = { },
                            onInstallSkill = { },
                            onUninstallSkill = { },
                        )
                    }

                    // Cron
                    composable(route = Screen.Cron.route) {
                        CronScreen(
                            jobs = emptyList(),
                            isLoading = false,
                            onToggle = { },
                            onDelete = { },
                            onCreate = { },
                        )
                    }

                    // Providers
                    composable(route = Screen.Providers.route) {
                        ProvidersScreen(
                            providers = emptyList(),
                            isLoading = false,
                            onActivate = { },
                        )
                    }

                    // Tools
                    composable(route = Screen.Tools.route) {
                        ToolsScreen(
                            tools = emptyList(),
                            isLoading = false,
                            onToggle = { },
                        )
                    }

                    // Connection
                    composable(route = Screen.Connection.route) {
                        ConnectionScreen(
                            serverUrl = chatState.serverUrl,
                            connectionStatus = chatState.connectionStatus,
                            onConnect = { url -> chatViewModel.connectToServer(url) },
                            onDisconnect = { chatViewModel.disconnectServer() },
                        )
                    }

                    // About
                    composable(route = Screen.About.route) {
                        AboutScreen()
                    }
                }
            }
        }
    }
}

private fun navigateToScreen(
    navController: NavController,
    screen: Screen,
    sessionId: String?,
) {
    val route = if (screen == Screen.Chat && sessionId != null) {
        Screen.chatRoute(sessionId)
    } else {
        screen.route
    }
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun DrawerHeader(
    connectionStatus: String,
    serverUrl: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.padding_xl),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "🤖",
                fontSize = 28.sp,
            )
        }

        Spacer(Modifier.height(Dimens.padding_lg))

        Text(
            text = "Hermes Agent",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(
                        when (connectionStatus) {
                            "connected" -> MaterialTheme.colorScheme.primary
                            "error" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    ),
            )
            Text(
                text = when (connectionStatus) {
                    "connected" -> "Подключено"
                    "connecting" -> "Подключение…"
                    "error" -> "Ошибка"
                    else -> "Не подключено"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (connectionStatus == "connected" && serverUrl.isNotBlank()) {
            Text(
                text = serverUrl,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DrawerFooter(
    onAbout: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAbout)
            .padding(Dimens.padding_lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.padding_sm),
    ) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = null,
            modifier = Modifier.size(Dimens.icon_size_sm),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "О программе",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = "v1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}


