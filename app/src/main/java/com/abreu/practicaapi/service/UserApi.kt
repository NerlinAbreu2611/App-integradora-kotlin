package com.abreu.practicaapi.service

import com.abreu.practicaapi.model.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("users/{id}")
    suspend fun findUserById(
        @Path("id") id: Int
    ): User

    @GET("users")
    suspend fun findAll(): List<User>

//    @GET("users")
//    suspend fun findByCompany(
//        @Query("company.name") companyName: String
//    ): List<User>

}