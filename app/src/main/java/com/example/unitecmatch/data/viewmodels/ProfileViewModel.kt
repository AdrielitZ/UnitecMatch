package com.example.unitecmatch.data.viewmodels

import androidx.lifecycle.ViewModel
import com.example.unitecmatch.data.models.User
import com.example.unitecmatch.data.sampleUsers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val _users = MutableStateFlow(sampleUsers)

    private val _currentUserIndex = MutableStateFlow(0)
    val currentUserIndex = _currentUserIndex.asStateFlow()

    // Simulating users who have liked the current user. IDs: 2, 4, 6, 8, 10
    private val usersWhoLikedMe = setOf(2, 4, 6, 8, 10)

    private val _likedUsers = MutableStateFlow<List<User>>(emptyList())

    private val _matches = MutableStateFlow<List<User>>(emptyList())
    val matches = _matches.asStateFlow()

    // Holds the latest match to trigger a dialog in the UI
    private val _newMatch = MutableStateFlow<User?>(null)
    val newMatch = _newMatch.asStateFlow()

    fun onLike() {
        val currentUser = _users.value[_currentUserIndex.value]
        _likedUsers.value += currentUser

        if (usersWhoLikedMe.contains(currentUser.id)) {
            _matches.value += currentUser
            _newMatch.value = currentUser // Set the new match!
        }

        goToNextUser()
    }

    fun onDiscard() {
        goToNextUser()
    }

    // Called from the UI to dismiss the new match dialog
    fun clearNewMatch() {
        _newMatch.value = null
    }

    private fun goToNextUser() {
        if (_currentUserIndex.value < _users.value.size - 1) {
            _currentUserIndex.value++
        } else {
            _currentUserIndex.value = _users.value.size // Signal that we're out of users
        }
    }

    fun getCurrentUser(): User? {
        return if (_currentUserIndex.value < _users.value.size) {
            _users.value[_currentUserIndex.value]
        } else {
            null
        }
    }
}
