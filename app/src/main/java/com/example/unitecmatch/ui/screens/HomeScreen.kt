package com.example.unitecmatch.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabItem(val title: String, val icon: ImageVector) {
    object Profiles : TabItem("Perfiles", Icons.Default.AccountCircle)
    object Matches : TabItem("Chats", Icons.Default.Favorite)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToChat: (Int) -> Unit) {
    var selectedTab by remember { mutableStateOf<TabItem>(TabItem.Profiles) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedTab.title, style = MaterialTheme.typography.headlineLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val tabs = listOf(TabItem.Profiles, TabItem.Matches)
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                TabItem.Profiles -> ProfileSwipeScreen(
                    onNavigateToMatches = { selectedTab = TabItem.Matches },
                    onNavigateToChat = onNavigateToChat
                )
                TabItem.Matches -> MatchesScreen(onNavigateToChat = onNavigateToChat)
            }
        }
    }
}
