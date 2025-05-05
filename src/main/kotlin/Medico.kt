package com.example
import java.util.Date

class Medico(
    id: Int,
    nome: String,
    contacto: String,
    numeroFuncionario: String,
    val especialidade: String
) : Funcionario(id, nome, contacto, numeroFuncionario)