
package com.abreu.practicaapi.data.repository

import com.abreu.practicaapi.data.local.UserDao
import com.abreu.practicaapi.data.local.UserEntity
import com.abreu.practicaapi.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {

    val users: Flow<List<UserEntity>> =
        userDao.getAllUsers()

    suspend fun addUser(user: User) {
        val user = UserEntity(
            id = requireNotNull(user.id),
            name = requireNotNull(user.name),
            email = requireNotNull(user.email),
            companyName = user.company?.name.orEmpty()
        )

        userDao.insertUser(user)
    }

    suspend fun removeUser(userId: Int) {
        userDao.deleteUserById(userId)
    }

    suspend fun isUser(userId: Int): Boolean {
        return userDao.isUser(userId)
    }
}