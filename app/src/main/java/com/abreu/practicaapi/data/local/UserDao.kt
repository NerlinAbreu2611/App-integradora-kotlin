package com.abreu.practicaapi.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM User ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM User WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(favorite: UserEntity)

    @Delete
    suspend fun deleteUser(favorite: UserEntity)

    @Query("DELETE FROM User WHERE id = :userId")
    suspend fun deleteUserById(userId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE id = :userId)")
    suspend fun isUser(userId: Int): Boolean
}