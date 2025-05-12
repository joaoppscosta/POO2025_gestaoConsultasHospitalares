package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Medico(
    val id: Int,
    val nome: String,
    val especialidade: Especialidade
) {
    override fun toString(): String {
        return "ID: $id | Nome: $nome | Especialidade: $especialidade"
    }
}
