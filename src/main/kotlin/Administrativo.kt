package com.example
import kotlinx.serialization.Serializable

@Serializable
data class Administrativo (
    val id: Int,
    val nome: String,
    val username: String,
    val password: String
)