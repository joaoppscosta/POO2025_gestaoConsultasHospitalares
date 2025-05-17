package com.example

import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.Serializable
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureRouting() {
    routing {
        get("/listaMedicos") {
            val caminho = "./src/main/resources/medicos/listaMedicos.json"
            val jsonString = File(caminho).readText()
            val listaMedicos = Json.decodeFromString<List<Medico>>(jsonString)
            call.respond(listaMedicos)
        }

        get ("/listaPacientes") {
            val caminho = "./src/main/resources/pacientes/listaPacientes.json"
            val jsonString = File(caminho).readText()
            val listaPacientes = Json.decodeFromString<List<Paciente>>(jsonString)
            call.respond(listaPacientes)
        }

        post("/listaPacientes") {
            // Recebe os dados do novo paciente enviados no corpo da requisição
            val novoPaciente = call.receive<Paciente>()

            // Lê a lista atual de pacientes
            val caminho = "./src/main/resources/pacientes/listaPacientes.json"
            val listaPacientes = Json.decodeFromString<MutableList<Paciente>>(File(caminho).readText())

            // Gera um ID único para o novo paciente (com base no maior ID existente + 1)
            val pacienteComId = novoPaciente.copy(id = (listaPacientes.maxOfOrNull { it.id } ?: 0) + 1)

            // Adiciona o paciente à lista
            listaPacientes.add(pacienteComId)

            // Salva a lista atualizada de pacientes no arquivo
            val jsonPretty = Json { prettyPrint = true }.encodeToString(listaPacientes)
            File(caminho).writeText(jsonPretty)

            // Responde que o paciente foi adicionado com sucesso
            call.respond(HttpStatusCode.Created, pacienteComId)
        }


        get ("/listaConsultas") {
            val caminho = "./src/main/resources/consultas/listaConsultas.json"
            val jsonString = File(caminho).readText()
            val listaConsultas = Json.decodeFromString<List<Consulta>>(jsonString)
            call.respond(listaConsultas)
        }
        post("/listaConsultas") {
            val caminho = "./src/main/resources/consultas/listaConsultas.json"

            // Receber consulta completa
            val novaConsulta = call.receive<Consulta>()

            // Ler a lista atual
            val listaConsultas = Json.decodeFromString<MutableList<Consulta>>(File(caminho).readText())

            // Criar nova consulta com ID único (ou usar o ID recebido)
            val novaComId = novaConsulta.copy(
                id = (listaConsultas.maxOfOrNull { it.id } ?: 0) + 1
            )

            // Adicionar à lista
            listaConsultas.add(novaComId)

            // Guardar no ficheiro
            val jsonPretty = Json {
                prettyPrint = true
            }.encodeToString(listaConsultas)

            File(caminho).writeText(jsonPretty)

            call.respond(HttpStatusCode.Created, novaComId)
        }

        get("/listaConsultas/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@get
            }

            val caminho = "./src/main/resources/consultas/listaConsultas.json"
            val jsonString = File(caminho).readText()
            val listaConsultas = Json.decodeFromString<List<Consulta>>(jsonString)

            val consulta = listaConsultas.find { it.id == id }

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

            val caminho = "./src/main/resources/consultas/listaConsultas.json"
            val jsonString = File(caminho).readText()
            val listaConsultas = Json.decodeFromString<MutableList<Consulta>>(jsonString)

            val consultaRemovida = listaConsultas.removeIf { it.id == id }

            if (!consultaRemovida) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
                return@delete
            }

            // Guardar a nova lista no ficheiro
            File(caminho).writeText(
                Json { prettyPrint = true }.encodeToString(listaConsultas)
            )

            call.respond(HttpStatusCode.OK, "Consulta com ID $id removida com sucesso.")
        }

        put("/listaConsultas/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@put
            }

            val caminho = "./src/main/resources/consultas/listaConsultas.json"
            val jsonString = File(caminho).readText()
            val listaConsultas = Json.decodeFromString<MutableList<Consulta>>(jsonString)

            // Encontra a consulta que será atualizada
            val consultaIndex = listaConsultas.indexOfFirst { it.id == id }
            if (consultaIndex == -1) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
                return@put
            }

            // Recebe a consulta atualizada
            val consultaAtualizada = call.receive<Consulta>()

            // Substitui a consulta existente
            listaConsultas[consultaIndex] = consultaAtualizada

            // Guarda a lista de consultas no ficheiro
            val jsonPretty = Json { prettyPrint = true }.encodeToString(listaConsultas)
            File(caminho).writeText(jsonPretty)

            call.respond(HttpStatusCode.OK, "Consulta com ID $id atualizada com sucesso.")
        }

        get("/pacientes") {
            val caminho = "./src/main/resources/pacientes/listaPacientes.json"
            val jsonString = File(caminho).readText()
            val listaPacientes = Json.decodeFromString<List<Paciente>>(jsonString)

            call.respond(ThymeleafContent("pacientes.html", mapOf("pacientes" to listaPacientes)))
        }

        get("/pacientes/novo") {
            call.respond(ThymeleafContent("novoPaciente.html", emptyMap<String, Any>()))
        }

        post("/pacientes") {
            val params = call.receiveParameters()

            val novoPaciente = Paciente(
                id = 0, // será atribuído no código
                nome = params["nome"] ?: "",
                genero = params["genero"] ?: "",
                contacto = params["contacto"] ?: "",
                numeroUtente = params["numeroUtente"] ?: "",
                dataNascimento = params["dataNascimento"] ?: ""
            )
            // Mesma lógica de guardar no JSON como antes
            val caminho = "./src/main/resources/pacientes/listaPacientes.json"
            val listaPacientes = Json.decodeFromString<MutableList<Paciente>>(File(caminho).readText())
            val pacienteComId = novoPaciente.copy(id = (listaPacientes.maxOfOrNull { it.id } ?: 0) + 1)
            listaPacientes.add(pacienteComId)

            File(caminho).writeText(Json { prettyPrint = true }.encodeToString(listaPacientes))

            call.respondRedirect("/pacientes")
        }

        // Rota para visualizar as consultas
        get("/consultas") {
            val caminho = "./src/main/resources/consultas/listaConsultas.json"
            val jsonString = File(caminho).readText()
            val listaConsultas = Json.decodeFromString<List<Consulta>>(jsonString)

            call.respond(ThymeleafContent("consultas.html", mapOf("consultas" to listaConsultas)))
        }

// Rota para adicionar uma nova consulta
        get("/consultas/novo") {
            call.respond(ThymeleafContent("novoConsulta.html", emptyMap<String, Any>()))
        }

        post("/consultas") {
            val params = call.receiveParameters()

            val pacienteId = params["pacienteId"]?.toIntOrNull() ?: 0
            val medicoId = params["medicoId"]?.toIntOrNull() ?: 0
            val data = params["data"] ?: ""
            val hora = params["hora"] ?: ""
            val motivo = params["motivo"] ?: ""

            val caminhoPacientes = "./src/main/resources/pacientes/listaPacientes.json"
            val caminhoMedicos = "./src/main/resources/medicos/listaMedicos.json"

            val listaPacientes = Json.decodeFromString<List<Paciente>>(File(caminhoPacientes).readText())
            val listaMedicos = Json.decodeFromString<List<Medico>>(File(caminhoMedicos).readText())

            val paciente = listaPacientes.find { it.id == pacienteId }
            val medico = listaMedicos.find { it.id == medicoId }

            if (paciente == null || medico == null) {
                call.respond(HttpStatusCode.NotFound, "Paciente ou médico não encontrado.")
                return@post
            }

            val novaConsulta = Consulta(
                id = 0,  // O ID será atribuído automaticamente
                paciente = paciente,
                medico = medico,
                data = data,
                hora = hora,
                motivo = motivo
            )

            // Lê as consultas existentes
            val caminho = "./src/main/resources/consultas/listaConsultas.json"
            val listaConsultas = Json.decodeFromString<MutableList<Consulta>>(File(caminho).readText())

            // Gera um novo ID
            val consultaComId = novaConsulta.copy(id = (listaConsultas.maxOfOrNull { it.id } ?: 0) + 1)
            listaConsultas.add(consultaComId)

            // Salva a lista atualizada no arquivo
            val jsonPretty = Json { prettyPrint = true }.encodeToString(listaConsultas)
            File(caminho).writeText(jsonPretty)

            // Redireciona para a lista de consultas
            call.respondRedirect("/consultas")
        }

        get("/medicos") {
            val caminho = "./src/main/resources/medicos/listaMedicos.json"
            val jsonString = File(caminho).readText()
            val listaMedicos = Json.decodeFromString<List<Medico>>(jsonString)

            call.respond(ThymeleafContent("medicos.html", mapOf("medicos" to listaMedicos)))
        }

        get("/medicos/novo") {
            val especialidades = Especialidade.values().toList()
            call.respond(
                ThymeleafContent(
                    "novoMedico.html",
                    mapOf("especialidades" to especialidades)
                )
            )
        }


        post("/medicos") {
            val params = call.receiveParameters()

            val nome = params["nome"] ?: ""
            val especialidadeStr = params["especialidade"] ?: ""

            val especialidade = try {
                Especialidade.valueOf(especialidadeStr)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Especialidade inválida.")
                return@post
            }

            val novoMedico = Medico(
                id = 0,
                nome = nome,
                especialidade = especialidade
            )

            val caminho = "./src/main/resources/medicos/listaMedicos.json"
            val listaMedicos = Json.decodeFromString<MutableList<Medico>>(File(caminho).readText())

            val medicoComId = novoMedico.copy(id = (listaMedicos.maxOfOrNull { it.id } ?: 0) + 1)
            listaMedicos.add(medicoComId)

            File(caminho).writeText(Json { prettyPrint = true }.encodeToString(listaMedicos))

            call.respondRedirect("/medicos")
        }
    }
}
