package com.example
import kotlinx.serialization.Serializable

@Serializable
data class Cirurgia(
    val id: Int,
    val descricao: String,
    val data: String, // Formato "DD/MM/YYYY"
    val hora: String, // Formato "HH:mm"
    val paciente: Paciente,
    val medicoResponsavel: Medico
)