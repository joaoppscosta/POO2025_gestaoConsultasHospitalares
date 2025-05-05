package com.example

class Administrativo (
    id: Int,
    nome: String,
    contacto: String,
    numeroFuncionario: String,
    val funcao: String
) : Funcionario(id, nome, contacto, numeroFuncionario)
