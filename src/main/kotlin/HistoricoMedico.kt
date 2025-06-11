package com.example
import kotlinx.serialization.Serializable

@Serializable
data class HistoricoMedico(
    val paciente: Paciente,
    val consultas: List<Consulta> = emptyList(),
    val prescricoes: List<Prescricao> = emptyList(),
    val exames: List<Exame> = emptyList(),
    val analises: List<Analise> = emptyList(),
    val cirurgias: List<Cirurgia> = emptyList()
)