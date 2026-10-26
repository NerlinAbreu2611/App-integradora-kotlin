package com.abreu.practicaapi.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abreu.practicaapi.data.repository.UserRepository

class UserRoomVMFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(UserRoomVM::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserRoomVM(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}