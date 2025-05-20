package com.example

import io.ktor.http.Parameters

fun criarPaciente(params: Parameters): Result<Paciente> {
    val novoPaciente = Paciente(
        id = 0,
        nome = params["nome"] ?: "",
        genero = params["genero"] ?: "",
        contacto = params["contacto"] ?: "",
        numeroUtente = params["numeroUtente"] ?: "",
        dataNascimento = params["dataNascimento"] ?: ""
    )

    val erros = validarPaciente(novoPaciente)
    return if (erros.isEmpty()) {
        val lista = Repositorio.lerPacientes()
        val pacienteComId = novoPaciente.copy(id = (lista.maxOfOrNull { it.id } ?: 0) + 1)
        lista.add(pacienteComId)
        Repositorio.guardarPacientes(lista)
        Result.success(pacienteComId)
    } else {
        Result.failure(Exception(erros.joinToString("\n")))
    }
}


fun validarPaciente(paciente: Paciente): List<String> {
    val erros = mutableListOf<String>()

    if (paciente.nome.isBlank()) erros.add("O nome é obrigatório.")
    if (paciente.numeroUtente.isBlank()) erros.add("O número de utente é obrigatório.")
    if (paciente.contacto.isBlank()) erros.add("O contacto é obrigatório.")
    if (paciente.dataNascimento.isBlank()) erros.add("A data de nascimento é obrigatória.")

    return erros
}

