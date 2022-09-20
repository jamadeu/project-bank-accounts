package com.bank.accounts.domain.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class Person(
    val id: String,
    var name: String,
    val cpf: String,
    var address: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val createdAt: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val updatedAt: LocalDate
)
