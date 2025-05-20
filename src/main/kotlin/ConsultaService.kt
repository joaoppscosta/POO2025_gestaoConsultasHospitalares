package com.example

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import io.ktor.http.Parameters

fun converterParaLocalDateTime(data: String, hora: String): LocalDateTime {
    val formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val dataFormatada = LocalDate.parse(data, formatoData)
    val horaFormatada = LocalTime.parse(hora)
    return LocalDateTime.of(dataFormatada, horaFormatada)
}

fun horariosSobrepostos(inicio1: LocalDateTime, inicio2: LocalDateTime): Boolean {
    val fim1 = inicio1.plusMinutes(30)
    val fim2 = inicio2.plusMinutes(30)
    return !(fim1 <= inicio2 || inicio1 >= fim2)
}

fun medicoTemConflito(
    medicoId: Int,
    data: String,
    hora: String,
    listaConsultas: List<Consulta>
): Boolean {
    val novaConsulta = converterParaLocalDateTime(data, hora)
    return listaConsultas.any {
        it.medico.id == medicoId &&
                horariosSobrepostos(novaConsulta, converterParaLocalDateTime(it.data, it.hora))
    }
}

fun gerarIdConsulta(listaConsultas: List<Consulta>): Int {
    return (listaConsultas.maxOfOrNull { it.id } ?: 0) + 1
}

fun construirConsulta(
    paciente: Paciente,
    medico: Medico,
    data: String,
    hora: String,
    motivo: String,
    listaConsultas: List<Consulta>
): Consulta {
    return Consulta(
        id = gerarIdConsulta(listaConsultas),
        paciente = paciente,
        medico = medico,
        data = data,
        hora = hora,
        motivo = motivo
    )
}

fun criarConsulta(params: Parameters): Result<Consulta> {
    val pacienteId = params["pacienteId"]?.toIntOrNull() ?: return Result.failure(Exception("ID do paciente inválido."))
    val medicoId = params["medicoId"]?.toIntOrNull() ?: return Result.failure(Exception("ID do médico inválido."))
    val data = params["data"] ?: return Result.failure(Exception("Data obrigatória."))
    val hora = params["hora"] ?: return Result.failure(Exception("Hora obrigatória."))
    val motivo = params["motivo"]?.takeIf { it.isNotBlank() } ?: return Result.failure(Exception("Motivo obrigatório."))

    val listaPacientes = Repositorio.lerPacientes()
    val listaMedicos = Repositorio.lerMedicos()
    val listaConsultas = Repositorio.lerConsultas()

    val paciente = listaPacientes.find { it.id == pacienteId }
        ?: return Result.failure(Exception("Paciente com ID $pacienteId não encontrado."))

    val medico = listaMedicos.find { it.id == medicoId }
        ?: return Result.failure(Exception("Médico com ID $medicoId não encontrado."))

    val conflito = try {
        medicoTemConflito(medicoId, data, hora, listaConsultas)
    } catch (e: Exception) {
        return Result.failure(Exception("Data ou hora inválida."))
    }

    if (conflito) {
        return Result.failure(Exception("Este médico já tem uma consulta marcada nesse horário."))
    }

    val novaConsulta = construirConsulta(paciente, medico, data, hora, motivo, listaConsultas)
    listaConsultas.add(novaConsulta)
    Repositorio.guardarConsultas(listaConsultas)

    return Result.success(novaConsulta)
}

// Agenda
fun gerarAgendaDoMedico(
    medicoId: Int,
    data: String,
    listaConsultas: List<Consulta>,
    inicio: String = "08:00",
    fim: String = "19:00"
): List<Pair<String, Boolean>> {
    val formatoHora = DateTimeFormatter.ofPattern("HH:mm")
    val horaInicio = LocalTime.parse(inicio, formatoHora)
    val horaFim = LocalTime.parse(fim, formatoHora)

    val blocos = mutableListOf<Pair<String, Boolean>>()
    var horaAtual = horaInicio

    while (horaAtual < horaFim) {
        val ocupado = listaConsultas.any {
            it.medico.id == medicoId &&
                    it.data == data &&
                    it.hora == horaAtual.toString()
        }
        blocos.add(horaAtual.toString() to ocupado)
        horaAtual = horaAtual.plusMinutes(30)
    }

    return blocos
}