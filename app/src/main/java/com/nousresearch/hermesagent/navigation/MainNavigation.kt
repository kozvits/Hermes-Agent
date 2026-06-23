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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nousresearch.hermesagent.data.local.PreferencesManager
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
import kotlinx.coroutines.CoroutineScope
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

    // Loading screen while session initializes
    if (!isInitialized) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        LaunchedEffect(Unit) { /* ChatViewModel.init runs async */ }
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
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(Dimens.drawer_width)) {
                DrawerHeader(
                    connectionStatus = chatState.connectionStatus,
                    serverUrl = chatState.serverUrl,
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = Dimens.padding_lg))
                Spacer(Modifier.height(8.dp))

                // Drawer items - explicit, no forEach to avoid lambda capture NPE
                NavDrawerItem(screen = Screen.Chat, currentRoute = currentRoute, sessionId = chatState.currentSessionId, navController = navController, drawerState = drawerState, scope = scope)
                NavDrawerItem(screen = Screen.Memory, currentRoute = currentRoute, sessionId = chatState.currentSessionId, navController = navController, drawerState = drawerState, scope = scope)
                NavDrawerItem(screen = Screen.Sessions, currentRoute = currentRoute, sessionId = chatState.currentSessionId, navController = navController, drawerState = drawerState, scope = scope)
                NavDrawerItem(screen = Screen.Skills, currentRoute = currentRoute, sessionId = chatState.currentSessionId, navController = navController, drawerState = drawerState, scope = scope)
                NavDrawerItem(screen = Screen.Cron, currentRoute = currentRoute, sessionId = chatState.currentSessionId, navController = navController, drawerState = drawerState, scope = scope)
                NavDrawerItem(screen = Screen.Providers, currentRoute = currentRoute, sessionId = chatState.currentSessionId, navController = navController, drawerState = drawerState, scope = scope)
                NavDrawerItem(screen = Screen.Tools, currentRoute = currentRoute, sessionId = chatState.currentSessionId, navController = navController, drawerState = drawerState, scope = scope)
                NavDrawerItem(screen = Screen.Connection, currentRoute = currentRoute, sessionId = chatState.currentSessionId, navController = navController, drawerState = drawerState, scope = scope)

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
                        // Bottom nav items - inline to keep RowScope
                        val barCurrentRoute = currentRoute
                        val barSessionId = chatState.currentSessionId

                        // Chat
                        val isChatSelected = barCurrentRoute == Screen.Chat.route ||
                                (barCurrentRoute?.startsWith("chat/") == true)
                        NavigationBarItem(
                            icon = { BottomNavIcon(Screen.Chat, isChatSelected) },
                            label = { Text(Screen.Chat.label) },
                            selected = isChatSelected,
                            onClick = { navigateToScreen(navController, Screen.Chat, barSessionId) },
                        )

                        // Memory
                        val isMemorySelected = barCurrentRoute == Screen.Memory.route
                        NavigationBarItem(
                            icon = { BottomNavIcon(Screen.Memory, isMemorySelected) },
                            label = { Text(Screen.Memory.label) },
                            selected = isMemorySelected,
                            onClick = { navigateToScreen(navController, Screen.Memory, barSessionId) },
                        )

                        // Sessions
                        val isSessionsSelected = barCurrentRoute == Screen.Sessions.route
                        NavigationBarItem(
                            icon = { BottomNavIcon(Screen.Sessions, isSessionsSelected) },
                            label = { Text(Screen.Sessions.label) },
                            selected = isSessionsSelected,
                            onClick = { navigateToScreen(navController, Screen.Sessions, barSessionId) },
                        )

                        // Settings
                        val isSettingsSelected = barCurrentRoute == Screen.Settings.route
                        NavigationBarItem(
                            icon = { BottomNavIcon(Screen.Settings, isSettingsSelected) },
                            label = { Text(Screen.Settings.label) },
                            selected = isSettingsSelected,
                            onClick = { navigateToScreen(navController, Screen.Settings, barSessionId) },
                        )
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
                    composable(
                        route = Screen.Chat.route,
                        arguments = listOf(navArgument("sessionId") { type = NavType.StringType }),
                    ) {
                        ChatScreen(
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            viewModel = chatViewModel,
                        )
                    }

                    composable(route = Screen.Memory.route) {
                        MemoryScreen(entries = emptyList(), isLoading = false, onAddEntry = { _, _ -> }, onDeleteEntry = { })
                    }

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

                    composable(route = Screen.Settings.route) {
                        SettingsScreen(
                            preferencesManager = preferencesManager,
                            onNavigateToConnection = { navController.navigate(Screen.Connection.route) },
                            onNavigateToProviders = { navController.navigate(Screen.Providers.route) },
                            onNavigateToTools = { navController.navigate(Screen.Tools.route) },
                            onNavigateToAbout = { navController.navigate(Screen.About.route) },
                        )
                    }

                    composable(route = Screen.Skills.route) {
                        SkillsScreen(skills = emptyList(), isLoading = false, onToggleSkill = { }, onInstallSkill = { }, onUninstallSkill = { })
                    }

                    composable(route = Screen.Cron.route) {
                        CronScreen(jobs = emptyList(), isLoading = false, onToggle = { }, onDelete = { }, onCreate = { })
                    }

                    composable(route = Screen.Providers.route) {
                        ProvidersScreen(providers = emptyList(), isLoading = false, onActivate = { })
                    }

                    composable(route = Screen.Tools.route) {
                        ToolsScreen(tools = emptyList(), isLoading = false, onToggle = { })
                    }

                    composable(route = Screen.Connection.route) {
                        ConnectionScreen(
                            serverUrl = chatState.serverUrl,
                            connectionStatus = chatState.connectionStatus,
                            onConnect = { url -> chatViewModel.connectToServer(url) },
                            onDisconnect = { chatViewModel.disconnectServer() },
                        )
                    }

                    composable(route = Screen.About.route) {
                        AboutScreen()
                    }
                }
            }
        }
    }
}

// ── Drawer Item ──

@Composable
fun NavDrawerItem(
    screen: Screen,
    currentRoute: String?,
    sessionId: String,
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
) {
    val isSelected = currentRoute == screen.route ||
            (screen == Screen.Chat && currentRoute?.startsWith("chat/") == true)

    NavigationDrawerItem(
        icon = { DrawerItemIcon(screen, isSelected) },
        label = {
            Text(
                text = screen.label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )
        },
        selected = isSelected,
        onClick = {
            scope.launch { drawerState.close() }
            navigateToScreen(navController, screen, sessionId)
        },
        modifier = Modifier.padding(horizontal = Dimens.padding_sm),
    )
}

// ── Drawer Item Icon ──

@Composable
fun DrawerItemIcon(screen: Screen, isSelected: Boolean) {
    Icon(
        imageVector = if (isSelected) {
            screen.selectedIcon ?: screen.icon!!
        } else {
            screen.icon!!
        },
        contentDescription = null,
    )
}

// ── Bottom Nav Icon ──

@Composable
fun BottomNavIcon(screen: Screen, isSelected: Boolean) {
    Icon(
        imageVector = if (isSelected) {
            screen.selectedIcon ?: screen.icon!!
        } else {
            screen.icon!!
        },
        contentDescription = screen.label,
    )
}

// ── Navigation Helpers ──

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

// ── UI Components ──

@Composable
private fun DrawerHeader(connectionStatus: String, serverUrl: String) {
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
            Text(text = "🤖", fontSize = 28.sp)
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
private fun DrawerFooter(onAbout: () -> Unit) {
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
