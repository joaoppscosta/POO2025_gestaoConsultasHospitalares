package com.example

data class Prescricao(
    val id: Int,
    val medico: Medico,
    val paciente: Paciente,
    val listaMedicamentos: List<Medicamento>
)