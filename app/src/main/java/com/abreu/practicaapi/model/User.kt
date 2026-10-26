package com.abreu.practicaapi.model

data class User(
    val id: Int,
    val name:String,
    val email:String,
    val company: Company
)