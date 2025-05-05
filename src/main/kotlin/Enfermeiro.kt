package com.example

class Enfermeiro(
    id: Int,
    nome: String,
    contacto: String,
    numeroFuncionario: String,
    val area: String
) : Funcionario(id, nome, contacto, numeroFuncionario)