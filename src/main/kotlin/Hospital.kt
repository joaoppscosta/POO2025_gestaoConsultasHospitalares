package com.example

class Hospital(
    val nome: String,
    val morada: String,
    val contacto: String,
    val listaMedicos: MutableList<Medico> = mutableListOf(),
    val listaPacientes: MutableList<Paciente> = mutableListOf(),
    val listaConsultas: MutableList<Consulta> = mutableListOf()
)