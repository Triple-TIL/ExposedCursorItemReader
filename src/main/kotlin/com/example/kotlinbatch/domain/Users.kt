package com.example.kotlinbatch.domain

data class Users(
    val id: Long,
    val name: String,
    val age: Int
) {
    constructor(): this(0, "", 0)
}
