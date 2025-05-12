package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Medicamento(
    val id: Int,
    val nome: String,
    val dosagem: String
)