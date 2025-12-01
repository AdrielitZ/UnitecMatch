package com.example.unitecmatch.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unitecmatch.data.models.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val myMessage = Message(text, isFromMe = true)
        _messages.value += myMessage

        // Simulate a reply after a short delay
        viewModelScope.launch {
            delay(1500)
            val reply = Message("Haha, you said '$text'! That's cool.", isFromMe = false)
            _messages.value += reply
        }
    }
}
