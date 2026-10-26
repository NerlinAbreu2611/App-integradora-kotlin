package com.abreu.practicaapi.viewmodel

import androidx.lifecycle.ViewModel
import com.abreu.practicaapi.model.Company
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserEditVM: ViewModel() {

    private val _id = MutableStateFlow<Int?>(null)
    val id: StateFlow<Int?> = _id
    private val _name = MutableStateFlow<String?>(null)
    val name: StateFlow<String?> = _name

    private val _email = MutableStateFlow<String?>(null)
    val email: StateFlow<String?> = _email

    private val _company = MutableStateFlow<Company?>(null)
    val company: StateFlow<Company?> = _company

}