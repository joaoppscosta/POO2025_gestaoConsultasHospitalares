package com.example

import io.ktor.http.Parameters


fun criarMedico(params: Parameters): Result<Medico> {
    val nome = params["nome"] ?: ""
    val especialidadeStr = params["especialidade"] ?: ""

    // Assume-se que o valor recebido é sempre válido, pois vem de um <select> do HTML
    val especialidade = Especialidade.valueOf(especialidadeStr)

    val novoMedico = Medico(
        id = 0,
        nome = nome,
        especialidade = especialidade
    )

    val erros = validarMedico(novoMedico)
    return if (erros.isEmpty()) {
        val lista = Repositorio.lerMedicos()
        val medicoComId = novoMedico.copy(id = (lista.maxOfOrNull { it.id } ?: 0) + 1)
        lista.add(medicoComId)
        Repositorio.guardarMedicos(lista)
        Result.success(medicoComId)
    } else {
        Result.failure(Exception(erros.joinToString("\n")))
    }
}



fun validarMedico(medico: Medico): List<String> {
    val erros = mutableListOf<String>()

    if (medico.nome.isBlank()) erros.add("O nome do médico é obrigatório.")

    return erros
}