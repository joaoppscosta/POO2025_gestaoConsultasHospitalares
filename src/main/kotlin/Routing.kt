package com.example

import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.serialization.Serializable
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.configureRouting() {
    routing {
        get("/medicos") {
            val caminho = "./src/main/resources/medicos/listaMedicos.json"
            val jsonString = File(caminho).readText()
            val listaMedicos = Json.decodeFromString<List<Medico>>(jsonString)
            call.respond(listaMedicos)
        }

        get ("/pacientes") {
            val caminho = "./src/main/resources/pacientes/listaPacientes.json"
            val jsonString = File(caminho).readText()
            val listaPacientes = Json.decodeFromString<List<Paciente>>(jsonString)
            call.respond(listaPacientes)
        }
    }
}
