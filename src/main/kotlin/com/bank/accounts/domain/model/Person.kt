package com.bank.accounts.domain.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class Person(
    val id: String,
    val name: String,
    val cpf: String,
    val address: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val createdAt: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val updatedAt: LocalDate
)
