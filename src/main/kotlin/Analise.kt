package com.example
import kotlinx.serialization.Serializable

@Serializable
data class Analise(
    val id: Int,
    val tipo: String,
    val paciente: Paciente,
    val medicoSolicitante: Medico,
    val data: String, // Formato "DD/MM/YYYY"
    val hora: String, // Formato "HH:mm"
    val resultado: String
)