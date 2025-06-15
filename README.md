# Sistema de Gestão de Consultas Hospitalares
**Autores:**

HÉLDER MANUEL OLIVEIRA MATOS – 127103

JOÃO PEDRO PINHO DA SILVA COSTA – 126958

**Curso:** CTeSP em Desenvolvimento de Software (ESAN - 2024/2025 - Estarreja)

---

## ℹ️ Introdução

Este foi o nosso **primeiro projeto completo** em que trabalhámos com uma **framework backend (Ktor)**, ligando-a ao **frontend com Thymeleaf**.  
Foi desenvolvido no âmbito da Unidade Curricular de **Programação Orientada a Objetos**, com o objetivo de aplicar conceitos de orientação a objetos em Kotlin, ao mesmo tempo que explorávamos o desenvolvimento web com uma arquitetura organizada.

**Durante o desenvolvimento deste projeto, ganhámos experiência em:**
- Estruturação do backend com o Ktor
- Persistência de dados com ficheiros `.json` utilizando `kotlinx.serialization`
- Separação de responsabilidades com serviços e repositórios
- Criação de páginas HTML com Thymeleaf e manipulação das mesmas através de formulários


---
## ⚠️ Dificuldades e limitações
Durante o desenvolvimento, deparámo-nos com algumas dificuldades, sobretudo por ser a primeira vez que trabalhámos com frameworks backend e ligação ao frontend. A principal complexidade surgiu na estruturação do sistema: à medida que o projeto crescia, tornava-se mais difícil manter tudo organizado e reutilizável.

Além disso, nem sempre foi fácil decidir **onde colocar a lógica** (nas rotas, nos serviços, ou nos repositórios), o que resultou numa estrutura algo confusa em certos pontos.

Apesar disso, conseguimos implementar todas as funcionalidades principais de forma funcional. O sistema **não está perfeito a nível de arquitetura**, mas cumpre os requisitos e demonstra o que aprendemos ao longo da unidade curricular.


---
## 🏥 Funcionalidades

- **Gestão de Pacientes**
    - Criar, editar, apagar
    - Consultar histórico médico completo

- **Gestão de Médicos**
    - Criar, editar, apagar
    - Consultar agenda de consultas por data

- **Consultas**
    - Criar, editar, apagar
    - Verificação automática de conflitos de horário (blocos de 30 minutos)

- **Prescrições**
    - Associadas diretamente às consultas
    - Seleção de medicamentos de uma lista

- **Exames, Análises e Cirurgias**
    - Visualização e gestão de registos clínicos
    - Associados a médicos e pacientes

- **Histórico Médico**
    - Visualização consolidada de todas as interações do paciente: consultas, exames, análises, prescrições e cirurgias

---

## 🧠 Tecnologias e bibliotecas utilizadas

- **Kotlin**
- **Ktor**
- **Thymeleaf** para frontend HTML
- **kotlinx.serialization** para guardar dados em ficheiros `.json`
- **Gradle** como sistema de build

---