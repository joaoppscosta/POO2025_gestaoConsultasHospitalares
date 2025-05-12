package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Prescricao(
    val id: Int,
    val medico: Medico,
    val paciente: Paciente,
    val listaMedicamentos: List<Medicamento>
)