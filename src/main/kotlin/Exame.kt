package com.example

data class Exame(
    val id: Int,
    val tipo: String,
    val paciente: Paciente,
    val medicoSolicitante: Medico,
    val resultado: String
)