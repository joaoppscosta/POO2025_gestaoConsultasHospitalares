package com.example

import kotlinx.serialization.Serializable

@Serializable
data class Consulta(
    val id: Int,
    val paciente: Paciente,
    val medico: Medico,
    val dataHora: String,
    val motivo: String,
    val prescricao: Prescricao? = null,
    val exames: List<Exame> = emptyList()
) {
    override fun toString(): String {
        return "ID: $id | Paciente: $paciente | Médico: $medico | Data e Hora: $dataHora | Motivo: $motivo | Prescrição: $prescricao | Exames: $exames"
    }
}