package com.abreu.practicaapi.service.client

import com.abreu.practicaapi.service.UserApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()

    val userApi: UserApi =
        retrofit.create(UserApi::class.java)

}