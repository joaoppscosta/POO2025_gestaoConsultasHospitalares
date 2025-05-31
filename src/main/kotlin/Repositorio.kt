package com.example

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.io.File

object Repositorio {
    private val json = Json { prettyPrint = true }

    // ----------- MÉDICOS -----------
    private const val caminhoMedicos = "./src/main/resources/medicos/listaMedicos.json"

    fun lerMedicos(): MutableList<Medico> {
        if (!File(caminhoMedicos).exists()) {
            File(caminhoMedicos).writeText("[]")
        }
        return json.decodeFromString(File(caminhoMedicos).readText())
    }

    fun guardarMedicos(lista: List<Medico>) {
        File(caminhoMedicos).writeText(json.encodeToString(lista))
    }

    // ----------- PACIENTES -----------
    private const val caminhoPacientes = "./src/main/resources/pacientes/listaPacientes.json"

    fun lerPacientes(): MutableList<Paciente> {
        if (!File(caminhoPacientes).exists()) {
            File(caminhoPacientes).writeText("[]")
        }
        return json.decodeFromString(File(caminhoPacientes).readText())
    }

    fun guardarPacientes(lista: List<Paciente>) {
        File(caminhoPacientes).writeText(json.encodeToString(lista))
    }

    // ----------- CONSULTAS -----------
    private const val caminhoConsultas = "./src/main/resources/consultas/listaConsultas.json"

    fun lerConsultas(): MutableList<Consulta> {
        if(!File(caminhoConsultas).exists()) {
            File(caminhoConsultas).writeText("[]")
        }
        return json.decodeFromString(File(caminhoConsultas).readText())
    }

    fun guardarConsultas(lista: List<Consulta>) {
        File(caminhoConsultas).writeText(json.encodeToString(lista))
    }

    // ----------- MEDICAMENTOS -----------
    private const val caminhoMedicamentos = "./src/main/resources/medicamentos/listaMedicamentos.json"

    fun lerMedicamentos(): MutableList<Medicamento> {
        if (!File(caminhoMedicamentos).exists()) {
            File(caminhoMedicamentos).writeText("[]")
        }
        return json.decodeFromString(File(caminhoMedicamentos).readText())
    }

    fun guardarMedicamentos(lista: List<Medicamento>) {
        File(caminhoMedicamentos).writeText(json.encodeToString(lista))
    }
}



