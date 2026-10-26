package com.abreu.practicaapi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abreu.practicaapi.model.User
import com.abreu.practicaapi.service.client.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserVM : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    //Funcion para llamar la lista de usuarios de manera asincrona

    fun findAll(){

        viewModelScope.launch {

            try {
                _error.value = null

                val result =
                    RetrofitClient.userApi.findAll()

                _users.value = result
            }catch (e: Exception){
                _error.value = e.message?:"Error al cargar los datos"
            }

        }

    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun findById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _user.value = null

            try {
                val result = RetrofitClient.userApi.findUserById(id)
                _user.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar el usuario"
            } finally {
                _isLoading.value = false
            }
        }
    }

}