
package com.abreu.practicaapi.data.mapper

import com.abreu.practicaapi.data.local.UserEntity
import com.abreu.practicaapi.model.Company
import com.abreu.practicaapi.model.User

fun UserEntity.toUser(): User {
    return User(
        id = id,
        name = name,
        email = email,
        company = Company(
            name = companyName,
            catchPhrase = "",
            bs = ""
        )
    )
}