package com.example.unitecmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.unitecmatch.ui.Screen
import com.example.unitecmatch.ui.screens.*
import com.example.unitecmatch.ui.theme.UnitecMatchTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnitecMatchTheme {
                var showSplashScreen by remember { mutableStateOf(true) }
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Registration) }

                if (showSplashScreen) {
                    SplashScreen()
                } else {
                    when (val screen = currentScreen) {
                        is Screen.Registration -> {
                            RegistrationScreen(onRegistrationSuccess = { currentScreen = Screen.Home })
                        }
                        is Screen.Home -> {
                            HomeScreen(onNavigateToChat = { userId -> currentScreen = Screen.Chat(userId) })
                        }
                        is Screen.Chat -> {
                            ChatScreen(
                                userId = screen.userId,
                                onNavigateBack = { currentScreen = Screen.Home } // Go back to Home
                            )
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    delay(3000) // Wait for 3 seconds
                    showSplashScreen = false
                }
            }
        }
    }
}
