package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Paciente(
    val id: Int,
    val nome: String,
    val genero: String,
    val contacto: String,
    val numeroUtente: String,
    val dataNascimento: String
){
    override fun toString(): String {
        return "ID: $id | Nome: $nome | Género: $genero | Contacto: $contacto | Nº Utente: $numeroUtente | Data Nascimento: $dataNascimento"
    }
}