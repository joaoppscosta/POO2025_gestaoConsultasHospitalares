package com.example

open class Funcionario(
    id: Int,
    nome: String,
    contacto: String,
    val numeroFuncionario: String
) : Pessoa(id, nome, contacto)