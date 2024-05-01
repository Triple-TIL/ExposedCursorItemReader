package com.example.kotlinbatch.domain

import org.jetbrains.exposed.dao.id.LongIdTable

object UsersEntity: LongIdTable("users", "id") {
    val name = varchar("name", 50)
    val age = integer("age")
}
