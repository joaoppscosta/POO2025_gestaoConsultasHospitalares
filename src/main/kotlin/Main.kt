package com.example

import java.io.File
import kotlinx.serialization.json.Json


fun main() {
    val medicos = mutableListOf(
        Medico(1, "Dr. João Costa", Especialidade.CARDIOLOGIA),
        Medico(2, "Dr. Hélder Matos", Especialidade.ORTOPEDIA)
    )

    val pacientes = mutableListOf(
        Paciente(1, "Joaquim Ruela", "Masculino", "910000000", "111222333", "15/10/1990"),
        Paciente(2, "Miguel Lima", "Masculino", "920000000", "000111222", "09/05/2001")
    )

    val consultas = mutableListOf(
        Consulta(1, pacientes.first { it.id == 1}, medicos.first { it.id == 2}, "11/05/2025", "13:00", "Dor no Joelho Esquerdo"),
        Consulta(2, pacientes.first { it.id == 2}, medicos.first { it.id == 1}, "12/05/2025", "12:00", "Dor no Peito")
    )

    val jsonPretty = Json {
        prettyPrint = true
        prettyPrintIndent = "  " // indentação com 2 espaços
    }

    val pacientesSerializados = jsonPretty.encodeToString(pacientes)
    val medicosSerializados = jsonPretty.encodeToString(medicos)
    val consultasSerializadas = jsonPretty.encodeToString(consultas)
    val caminhoBase = "./src/main/resources"
    File("${caminhoBase}/medicos/listaMedicos.json").writeText(medicosSerializados)
    File("${caminhoBase}/pacientes/listaPacientes.json").writeText(pacientesSerializados)
    File("${caminhoBase}/consultas/listaConsultas.json").writeText(consultasSerializadas)

    val stringParaDecodeMedicos =  File("${caminhoBase}/medicos/listaMedicos.json").readText()
    val medicosRenovados = Json.decodeFromString<List<Medico>>(stringParaDecodeMedicos)

    val stringParaDecodePacientes =  File("${caminhoBase}/pacientes/listaPacientes.json").readText()
    val pacientesRenovados = Json.decodeFromString<List<Paciente>>(stringParaDecodePacientes)

    val stringParaDecodeConsultas = File("${caminhoBase}/consultas/listaConsultas.json").readText()
    val consultasRenovadas = Json.decodeFromString<List<Consulta>>(stringParaDecodeConsultas)
    println(consultasRenovadas)
}