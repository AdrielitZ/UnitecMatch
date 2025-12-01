package com.example.unitecmatch.ui

sealed class Screen {
    object Registration : Screen()
    object Home : Screen() // New home screen with tabs
    data class Chat(val userId: Int) : Screen()
}
