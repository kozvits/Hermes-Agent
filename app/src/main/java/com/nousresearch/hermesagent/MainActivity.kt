package com.nousresearch.hermesagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.nousresearch.hermesagent.data.local.PreferencesManager
import com.nousresearch.hermesagent.navigation.MainNavigation
import com.nousresearch.hermesagent.ui.screens.chat.ChatViewModel
import com.nousresearch.hermesagent.ui.theme.HermesAgentTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HermesAgentTheme(dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val chatViewModel: ChatViewModel = hiltViewModel()
                    MainNavigation(
                        preferencesManager = preferencesManager,
                        chatViewModel = chatViewModel,
                    )
                }
            }
        }
    }
}
