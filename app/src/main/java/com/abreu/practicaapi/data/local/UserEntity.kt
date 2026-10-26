

package com.abreu.practicaapi.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val email: String,
    val companyName: String
)