package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Consulta(
    val id: Int,
    val paciente: String,
    val medico: Medico,
    val dataHora: String,
    val motivo: String,
    val prescricao: Prescricao? = null,
    val exames: List<Exame> = emptyList()
)