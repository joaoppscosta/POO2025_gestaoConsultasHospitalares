package com.example

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.server.thymeleaf.ThymeleafContent
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter


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

        // Adicionar novo médico GET
        get("/medicos/novo") {
            val especialidades = Especialidade.values().toList()
            call.respond(ThymeleafContent("novoMedico.html", mapOf("especialidades" to especialidades)))
        }

        // Adicionar novo médico POST
        post("/medicos") {
            val params = call.receiveParameters()

            val resultado = criarMedico(params)

            if (resultado.isSuccess) {
                call.respondRedirect("/medicos")
            } else {
                call.respond(HttpStatusCode.BadRequest, resultado.exceptionOrNull()?.message ?: "Erro ao criar médico")
            }
        }

        // Agenda dos Médicos (em JSON)
        get("/medicos/{id}/agenda") {
            val medicoId = call.parameters["id"]?.toIntOrNull()
            val dataISO = call.request.queryParameters["data"]
            val data = try {
                LocalDate.parse(dataISO).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Data inválida.")
                return@get
            }

            if (medicoId == null || data.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "ID ou data inválida.")
                return@get
            }

            val listaConsultas = Repositorio.lerConsultas()

            val agenda = gerarAgendaDoMedico(medicoId, data, listaConsultas)

            call.respond(agenda)
        }

        // Editar médico GET
        get("/medicos/editar/{id}") {
            val medicoId = call.parameters["id"]?.toIntOrNull()

            if (medicoId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID do médico inválido.")
                return@get
            }

            val listaMedicos = Repositorio.lerMedicos()

            val medico = listaMedicos.find { it.id == medicoId }
            if (medico == null) {
                call.respond(HttpStatusCode.NotFound, "Médico não encontrado.")
            } else {
                call.respond(
                    ThymeleafContent(
                        "editarMedico.html",
                        mapOf(
                            "medico" to medico,
                            "especialidades" to Especialidade.values().toList()
                        )
                    )
                )
            }
        }

        // Editar médico POST
        post("/medicos/editar/{id}") {
            val params = call.receiveParameters()
            val listaMedicos = Repositorio.lerMedicos()

            val medicoId = call.parameters["id"]?.toIntOrNull()
            if (medicoId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@post
            }

            val index = listaMedicos.indexOfFirst { it.id == medicoId }
            if (index == -1) {
                call.respond(HttpStatusCode.NotFound, "Médico não encontrado.")
                return@post
            }

            val especialidadeStr = params["especialidade"] ?: ""
            val especialidade = try {
                Especialidade.valueOf(especialidadeStr)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Especialidade inválida.")
                return@post
            }

            val medicoAtualizado = Medico(
                id = medicoId,
                nome = params["nome"] ?: "",
                especialidade = especialidade
            )

            listaMedicos[index] = medicoAtualizado
            Repositorio.guardarMedicos(listaMedicos)

            call.respondRedirect("/medicos")
        }

        //TESTE ---------------------- TESTE (mostrar consultas do médico pelo ID)
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

        // Apagar Médico
        post("/medicos/apagar/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@post
            }

            val listaConsultas = Repositorio.lerConsultas()
            val estaEmConsulta = listaConsultas.any { it.medico.id == id }

            if (estaEmConsulta) {
                call.respond(
                    ThymeleafContent(
                        "medicos.html",
                        mapOf(
                            "medicos" to Repositorio.lerMedicos(),
                            "erro" to "Não é possível apagar: este médico tem consultas associadas."
                        )
                    )
                )
                return@post
            }

            val lista = Repositorio.lerMedicos()
            val removido = lista.removeIf { it.id == id }

            if (!removido) {
                call.respond(HttpStatusCode.NotFound, "Médico não encontrado.")
            } else {
                Repositorio.guardarMedicos(lista)
                call.respondRedirect("/medicos")
            }
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

        get("/pacientes/editar/{id}") {
            val pacienteId = call.parameters["id"]?.toIntOrNull()

            if (pacienteId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID do paciente inválido.")
                return@get
            }

            val listaPacientes = Repositorio.lerPacientes()

            val paciente = listaPacientes.find { it.id == pacienteId }
            if (paciente == null) {
                call.respond(HttpStatusCode.NotFound, "Paciente não encontrado.")
            } else {
                call.respond(
                    ThymeleafContent("editarPaciente.html", mapOf("paciente" to paciente))
                )
            }
        }

        post("/pacientes/editar/{id}") {
            val params = call.receiveParameters()

            val listaPacientes = Repositorio.lerPacientes()

            val pacienteId = call.parameters["id"]?.toIntOrNull()
            if (pacienteId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID Inválido.")
                return@post
            }

            val index = listaPacientes.indexOfFirst { it.id == pacienteId }
            if (index == -1) {
                call.respond(HttpStatusCode.NotFound, "Paciente não encontrado.")
                return@post
            }

            val pacienteAtualizado = Paciente(
                id = pacienteId,
                nome = params["nome"] ?: "",
                genero = params["genero"] ?: "",
                contacto = params["contacto"] ?: "",
                numeroUtente = params["numeroUtente"] ?: "",
                dataNascimento = params["dataNascimento"] ?: ""
            )

            listaPacientes[index] = pacienteAtualizado
            Repositorio.guardarPacientes(listaPacientes)

            call.respondRedirect("/pacientes")
        }

        // Apagar Pacientes

        post("/pacientes/apagar/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@post
            }

            val listaConsultas = Repositorio.lerConsultas()
            val estaEmConsulta = listaConsultas.any { it.paciente.id == id }

            if (estaEmConsulta) {
                call.respond(
                    ThymeleafContent(
                        "pacientes.html",
                        mapOf(
                            "pacientes" to Repositorio.lerPacientes(),
                            "erro" to "Não é possível apagar: este paciente tem consultas associadas."
                        )
                    )
                )
                return@post // 🔥 Impede que continue
            }

            val listaPacientes = Repositorio.lerPacientes()
            val removido = listaPacientes.removeIf { it.id == id }

            if (!removido) {
                call.respond(HttpStatusCode.NotFound, "Paciente não encontrado.")
            } else {
                Repositorio.guardarPacientes(listaPacientes)
                call.respondRedirect("/pacientes")
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

        get("/consultas/editar/{id}") {
            val consultaId = call.parameters["id"]?.toIntOrNull()

            if (consultaId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@get
            }

            val listaConsultas = Repositorio.lerConsultas()
            val consulta = listaConsultas.find { it.id == consultaId }

            if (consulta == null) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
                return@get
            }

            val listaPacientes = Repositorio.lerPacientes()
            val listaMedicos = Repositorio.lerMedicos()

            call.respond(
                ThymeleafContent(
                    "editarConsulta.html",
                    mapOf(
                        "consulta" to consulta,
                        "pacientes" to listaPacientes,
                        "medicos" to listaMedicos
                    )
                )
            )
        }


        post("/consultas/editar/{id}") {
            val consultaId = call.parameters["id"]?.toIntOrNull()
            if (consultaId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@post
            }

            val params = call.receiveParameters()

            val pacienteId = params["pacienteId"]?.toIntOrNull() ?: 0
            val medicoId = params["medicoId"]?.toIntOrNull() ?: 0
            val data = params["data"] ?: ""
            val hora = params["hora"] ?: ""
            val motivo = params["motivo"] ?: ""

            val listaPacientes = Repositorio.lerPacientes()
            val listaMedicos = Repositorio.lerMedicos()
            val listaConsultas = Repositorio.lerConsultas()

            val paciente = listaPacientes.find { it.id == pacienteId }
            val medico = listaMedicos.find { it.id == medicoId }

            if (paciente == null || medico == null) {
                call.respond(HttpStatusCode.BadRequest, "Paciente ou médico inválido.")
                return@post
            }

            val index = listaConsultas.indexOfFirst { it.id == consultaId }
            if (index == -1) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
                return@post
            }

            val consultaAtualizada = Consulta(
                id = consultaId,
                paciente = paciente,
                medico = medico,
                data = data,
                hora = hora,
                motivo = motivo
            )

            listaConsultas[index] = consultaAtualizada
            Repositorio.guardarConsultas(listaConsultas)

            call.respondRedirect("/consultas")
        }

         //Apagar consultas
        post("/consultas/apagar/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@post
            }

            val lista = Repositorio.lerConsultas()
            val removido = lista.removeIf { it.id == id }

            if (!removido) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
            } else {
                Repositorio.guardarConsultas(lista)
                call.respondRedirect("/consultas")
            }
        }


        // ------------ MEDICAMENTOS ------------
        get("/medicamentos") {
            val lista = Repositorio.lerMedicamentos()
            call.respond(ThymeleafContent("medicamentos.html", mapOf("medicamentos" to lista)))
        }

        get ("/medicamentos/novo") {
            call.respond(ThymeleafContent("novoMedicamento.html", emptyMap()))
        }

        post("/medicamentos") {
            val params = call.receiveParameters()

            val nome = params["nome"] ?: ""
            val dosagem = params["dosagem"] ?: ""

            if (nome.isBlank() || dosagem.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Nome e dosagem são obrigatórios.")
                return@post
            }

            val listaMedicamentos = Repositorio.lerMedicamentos()
            val novoMedicamento = Medicamento(
                id = (listaMedicamentos.maxOfOrNull { it.id } ?: 0) + 1,
                nome = nome,
                dosagem = dosagem
            )

            listaMedicamentos.add(novoMedicamento)
            Repositorio.guardarMedicamentos(listaMedicamentos)

            call.respondRedirect("/medicamentos")
        }

        // ------------ PRESCRIÇÕES ------------
        get("/prescricoes/nova") {
            val consultaId = call.request.queryParameters["consultaId"]?.toIntOrNull()
            val consultas = Repositorio.lerConsultas()
            val medicamentos = Repositorio.lerMedicamentos()

            call.respond(
                ThymeleafContent(
                    "novaPrescricao.html",
                    mapOf(
                        "consultas" to consultas,
                        "medicamentos" to medicamentos,
                        "consultaIdSelecionada" to (consultaId ?: -1)
                    )
                )
            )
        }

        get("/prescricoes/nova/{id}") {
            val consultaId = call.parameters["id"]?.toIntOrNull()
            if (consultaId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID de consulta inválido.")
                return@get
            }

            val consulta = Repositorio.lerConsultas().find { it.id == consultaId }
            if (consulta == null) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
                return@get
            }

            val medicamentosSelecionadosIds = consulta.prescricao?.listaMedicamentos?.map { it.id }?.toSet() ?: emptySet()

            val medicamentos = Repositorio.lerMedicamentos()
            call.respond(
                ThymeleafContent(
                    "novaPrescricao.html",
                    mapOf(
                        "consulta" to consulta,
                        "medicamentos" to medicamentos,
                        "medicamentosSelecionados" to medicamentosSelecionadosIds
                    )
                )
            )
        }

        post("/prescricoes/apagar/{id}") {
            val consultaId = call.parameters["id"]?.toIntOrNull()
            if (consultaId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@post
            }

            val listaConsultas = Repositorio.lerConsultas()
            val index = listaConsultas.indexOfFirst { it.id == consultaId }

            if (index == -1) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
                return@post
            }

            val consulta = listaConsultas[index]
            val consultaAtualizada = consulta.copy(prescricao = null)
            listaConsultas[index] = consultaAtualizada

            Repositorio.guardarConsultas(listaConsultas)
            call.respondRedirect("/consultas")
        }

        post("/prescricoes") {
            val params = call.receiveParameters()
            val consultaId = params["consultaId"]?.toIntOrNull()
            val medsIds = params.getAll("medicamentos")?.mapNotNull { it.toIntOrNull() } ?: emptyList()

            val consultas = Repositorio.lerConsultas()
            val consultaIndex = consultas.indexOfFirst { it.id == consultaId }

            if (consultaIndex == -1) {
                call.respond(HttpStatusCode.NotFound, "Consulta não encontrada.")
                return@post
            }

            val medicamentosSelecionados = Repositorio.lerMedicamentos().filter { it.id in medsIds }

            val consulta = consultas[consultaIndex]
            val novaPrescricao = Prescricao(
                id = consultaId ?: 0,
                medico = consulta.medico,
                paciente = consulta.paciente,
                listaMedicamentos = medicamentosSelecionados
            )

            // Atualiza consulta com nova prescrição
            val consultaAtualizada = consulta.copy(prescricao = novaPrescricao)
            consultas[consultaIndex] = consultaAtualizada
            Repositorio.guardarConsultas(consultas)

            call.respondRedirect("/consultas")
        }

        // ------------------- HISTÓRICO MÉDICO -------------------

        get("/pacientes/{id}/historico") {
            val pacienteId = call.parameters["id"]?.toIntOrNull()

            if (pacienteId == null) {
                call.respond(HttpStatusCode.BadRequest, "ID inválido.")
                return@get
            }

            val paciente = Repositorio.lerPacientes().find { it.id == pacienteId }
            if (paciente == null) {
                call.respond(HttpStatusCode.NotFound, "Paciente não encontrado.")
                return@get
            }

            val consultas = Repositorio.lerConsultas().filter { it.paciente.id == pacienteId }
            val prescricoes = consultas.mapNotNull { it.prescricao }
            val exames = Repositorio.lerExames().filter { it.paciente.id == pacienteId }
            val analises = Repositorio.lerAnalises().filter { it.paciente.id == pacienteId }
            val cirurgias = Repositorio.lerCirurgias().filter { it.paciente.id == pacienteId }

            val historico = HistoricoMedico(
                paciente = paciente,
                consultas = consultas,
                prescricoes = prescricoes,
                exames = exames,
                analises = analises,
                cirurgias = cirurgias
            )

            call.respond(
                ThymeleafContent("historicoMedico.html", mapOf("historico" to historico))
            )
        }

        // ------------------- CIRURGIAS -------------------

        get("/cirurgias") {
            call.respond(ThymeleafContent("cirurgias.html", mapOf("cirurgias" to Repositorio.lerCirurgias())))
        }


        // ------------------- EXAMES -------------------

        get("/exames") {
            call.respond(ThymeleafContent("exames.html", mapOf("exames" to Repositorio.lerExames())))
        }

        // ------------------- ANALISES -------------------

        get("/analises") {
            call.respond(ThymeleafContent("analises.html", mapOf("analises" to Repositorio.lerAnalises())))
        }
    }
}