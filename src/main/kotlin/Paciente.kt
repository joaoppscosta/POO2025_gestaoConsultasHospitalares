package com.example

import java.util.Date

class Paciente(
    id: Int,
    nome: String,
    contacto: String,
    val numeroUtente: String,
    val dataNascimento: Date
) : Pessoa(id, nome, contacto)