
package com.abreu.practicaapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abreu.practicaapi.data.local.UserEntity
import com.abreu.practicaapi.data.repository.UserRepository
import com.abreu.practicaapi.model.User
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserRoomVM(
    private val repository: UserRepository
) : ViewModel() {

    val users: StateFlow<List<UserEntity>> =
        repository.users.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addUser(user: User) {
        viewModelScope.launch {
            repository.addUser(user)
        }
    }

    fun removeUser(userId: Int) {
        viewModelScope.launch {
            repository.removeUser(userId)
        }
    }

    fun toggleFavorite(user: User) {
        viewModelScope.launch {
            if (repository.isUser(requireNotNull(user.id))) {
                repository.isUser(requireNotNull(user.id))
            } else {
                repository.addUser(user)
            }
        }
    }

    fun isFavorite(userId: Int): Boolean {
        return users.value.any { it.id == userId }
    }
}