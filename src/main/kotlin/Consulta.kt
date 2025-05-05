package com.example

data class Consulta(
    val id: Int,
    val paciente: Paciente,
    val medico: Medico,
    val data: String,
    val observacoes: String
)