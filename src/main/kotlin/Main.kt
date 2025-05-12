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

    val jsonPretty = Json {
        prettyPrint = true
        prettyPrintIndent = "  " // indentação com 2 espaços
    }

    val pacientesSerializados = jsonPretty.encodeToString(pacientes)
    val medicosSerializados = jsonPretty.encodeToString(medicos)
    val caminhoBase = "./src/main/resources"
    File("${caminhoBase}/medicos/listaMedicos.json").writeText(medicosSerializados)
    File("${caminhoBase}/pacientes/listaPacientes.json").writeText(pacientesSerializados)

    val stringParaDecodeMedicos =  File("${caminhoBase}/medicos/listaMedicos.json").readText()
    val medicosRenovados = Json.decodeFromString<List<Medico>>(stringParaDecodeMedicos)
    val stringParaDecodePacientes =  File("${caminhoBase}/pacientes/listaPacientes.json").readText()
    val pacientesRenovados = Json.decodeFromString<List<Paciente>>(stringParaDecodePacientes)

    println(pacientesRenovados)
}