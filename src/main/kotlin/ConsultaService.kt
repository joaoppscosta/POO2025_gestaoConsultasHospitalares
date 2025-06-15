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

// Função que verifica se dois horários de consulta se sobrepõem.
// Cada consulta tem uma duração de 30 minutos.
// Recebe dois horários de início (inicio1 e inicio2) e calcula os seus respetivos finais.
// Devolve true se houver sobreposição, ou false caso contrário.
fun horariosSobrepostos(inicio1: LocalDateTime, inicio2: LocalDateTime): Boolean {

    // Calcula o fim da primeira consulta (30 minutos após o início)
    val fim1 = inicio1.plusMinutes(30)

    // Calcula o fim da segunda consulta (30 minutos após o início)
    val fim2 = inicio2.plusMinutes(30)

    // Verifica se NÃO há sobreposição:
    // fim1 <= inicio2 - a primeira termina antes da segunda começar
    // inicio1 >= fim2 - a primeira começa depois da segunda acabar
    // Se nenhuma destas condições for verdadeira, então há sobreposição.
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

// Função que cria uma nova consulta a partir dos parâmetros recebidos via formulário.
// Devolve um Result<Consulta>, que pode representar sucesso ou falha com uma mensagem de erro.
fun criarConsulta(params: Parameters): Result<Consulta> {

    // Tenta converter o ID do paciente, se falhar devolve erro.
    val pacienteId = params["pacienteId"]?.toIntOrNull() ?: return Result.failure(Exception("ID do paciente inválido."))

    // Tenta converter o ID do médico, se falhar devolve erro.
    val medicoId = params["medicoId"]?.toIntOrNull() ?: return Result.failure(Exception("ID do médico inválido."))

    // Verifica se o campo data foi preenchido.
    val data = params["data"] ?: return Result.failure(Exception("Data obrigatória."))

    // Verifica se o campo hora foi preenchido.
    val hora = params["hora"] ?: return Result.failure(Exception("Hora obrigatória."))

    // Verifica se o campo motivo foi preenchido (não pode estar vazio ou só com espaços).
    val motivo = params["motivo"]?.takeIf { it.isNotBlank() } ?: return Result.failure(Exception("Motivo obrigatório."))

    // Lê listas de pacientes, médicos e consultas do repositório.
    val listaPacientes = Repositorio.lerPacientes()
    val listaMedicos = Repositorio.lerMedicos()
    val listaConsultas = Repositorio.lerConsultas()

    // Procura o paciente pelo ID; se não existir, devolve erro.
    val paciente = listaPacientes.find { it.id == pacienteId }
        ?: return Result.failure(Exception("Paciente com ID $pacienteId não encontrado."))

    // Procura o médico pelo ID; se não existir, devolve erro.
    val medico = listaMedicos.find { it.id == medicoId }
        ?: return Result.failure(Exception("Médico com ID $medicoId não encontrado."))

    // Verifica se já existe uma consulta no mesmo horário com o mesmo médico.
    val conflito = try {
        medicoTemConflito(medicoId, data, hora, listaConsultas)
    } catch (e: Exception) {
        return Result.failure(Exception("Data ou hora inválida."))
    }

    // Se houver conflito de horário, não permite marcar nova consulta.
    if (conflito) {
        return Result.failure(Exception("Este médico já tem uma consulta marcada nesse horário."))
    }

    // Constrói uma nova consulta válida.
    val novaConsulta = construirConsulta(paciente, medico, data, hora, motivo, listaConsultas)

    // Adiciona à lista de consultas e guarda no ficheiro.
    listaConsultas.add(novaConsulta)
    Repositorio.guardarConsultas(listaConsultas)

    // Devolve sucesso com a nova consulta criada.
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

// Função que devolve o estado de uma consulta com base na data e hora fornecidas.
// - Se a data/hora da consulta for no futuro, devolve "AGENDADA".
// - Se a data/hora já tiver passado, devolve "CONCLUÍDA".
// - Se ocorrer um erro ao converter a data/hora (formato inválido, por exemplo), devolve "DESCONHECIDO".

fun estadoConsulta(data: String, hora: String): String {
    return try {
        val dataHoraConsulta = converterParaLocalDateTime(data, hora)
        if (dataHoraConsulta.isAfter(LocalDateTime.now())) "AGENDADA" else "CONCLUÍDA"
    } catch (e: Exception) {
        "DESCONHECIDO"
    }
}