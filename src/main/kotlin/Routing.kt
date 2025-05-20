package com.example

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.server.thymeleaf.ThymeleafContent
import java.time.LocalDateTime

fun Application.configureRouting() {
    routing {

        // Página inicial
        get("/") {
            call.respond(ThymeleafContent("index.html", emptyMap<String, Any>()))
        }

        // ------------ MÉDICOS ------------

        get("/listaMedicos") {
            call.respond(Repositorio.lerMedicos())
        }

        get("/medicos") {
            val listaMedicos = Repositorio.lerMedicos()
            call.respond(ThymeleafContent("medicos.html", mapOf("medicos" to listaMedicos)))
        }

        get("/medicos/novo") {
            val especialidades = Especialidade.values().toList()
            call.respond(ThymeleafContent("novoMedico.html", mapOf("especialidades" to especialidades)))
        }

        post("/medicos") {
            val params = call.receiveParameters()

            val resultado = criarMedico(params)

            if (resultado.isSuccess) {
                call.respondRedirect("/medicos")
            } else {
                call.respond(HttpStatusCode.BadRequest, resultado.exceptionOrNull()?.message ?: "Erro ao criar médico")
            }
        }

        get("/medicos/{id}/agenda") {
            val medicoId = call.parameters["id"]?.toIntOrNull()
            val data = call.request.queryParameters["data"]

            if (medicoId == null || data.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "ID ou data inválida.")
                return@get
            }

            val listaConsultas = Repositorio.lerConsultas()

            val agenda = gerarAgendaDoMedico(medicoId, data, listaConsultas)

            call.respond(agenda)
        }

        //TESTE ---------------------- TESTE
        get("/medicos/{id}/consultas"){
            val medicoId = call.parameters["id"]?.toIntOrNull()

            if (medicoId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID do médico inválido.")
                return@get
            }

            val listaConsultas = Repositorio.lerConsultas()
            val consultasDoMedico = listaConsultas.filter { it.medico.id == medicoId }
            call.respond(consultasDoMedico)


        }
        // ------------ PACIENTES ------------

        get("/listaPacientes") {
            call.respond(Repositorio.lerPacientes())
        }

        get("/pacientes") {
            val listaPacientes = Repositorio.lerPacientes()
            call.respond(ThymeleafContent("pacientes.html", mapOf("pacientes" to listaPacientes)))
        }

        get("/pacientes/novo") {
            call.respond(ThymeleafContent("novoPaciente.html", emptyMap<String, Any>()))
        }

        post("/pacientes") {
            val params = call.receiveParameters()

            val resultado = criarPaciente(params)

            if (resultado.isSuccess) {
                call.respondRedirect("/pacientes")
            } else {
                call.respond(HttpStatusCode.BadRequest, resultado.exceptionOrNull()?.message ?: "Erro ao criar paciente")
            }
        }

        // ------------ CONSULTAS ------------

        get("/listaConsultas") {
            call.respond(Repositorio.lerConsultas())
        }

        get("/consultas") {
            val listaConsultas = Repositorio.lerConsultas()
            call.respond(ThymeleafContent("consultas.html", mapOf("consultas" to listaConsultas)))
        }

        get("/consultas/novo") {
            val listaMedicos = Repositorio.lerMedicos()
            call.respond(ThymeleafContent("novoConsulta.html", mapOf("medicos" to listaMedicos)))
        }

        post("/consultas") {
            val params = call.receiveParameters()

            val resultado = criarConsulta(params)

            if (resultado.isSuccess) {
                call.respondRedirect("/consultas")
            } else {
                call.respond(HttpStatusCode.BadRequest, resultado.exceptionOrNull()?.message ?: "Erro ao criar consulta")
            }
        }

        get("/listaConsultas/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@get
            }

            val consulta = Repositorio.lerConsultas().find { it.id == id }

            if (consulta == null) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
            } else {
                call.respond(consulta)
            }
        }

        delete("/listaConsultas/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@delete
            }

            val listaConsultas = Repositorio.lerConsultas()
            val consultaRemovida = listaConsultas.removeIf { it.id == id }

            if (!consultaRemovida) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
                return@delete
            }

            Repositorio.guardarConsultas(listaConsultas)
            call.respond(HttpStatusCode.OK, "Consulta com ID $id removida com sucesso.")
        }

        put("/listaConsultas/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@put
            }

            val listaConsultas = Repositorio.lerConsultas()
            val consultaIndex = listaConsultas.indexOfFirst { it.id == id }

            if (consultaIndex == -1) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
                return@put
            }

            val consultaAtualizada = call.receive<Consulta>()
            listaConsultas[consultaIndex] = consultaAtualizada
            Repositorio.guardarConsultas(listaConsultas)

            call.respond(HttpStatusCode.OK, "Consulta com ID $id atualizada com sucesso.")
        }
    }
}