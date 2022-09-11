package com.bank.accounts.domain.model

import com.fasterxml.jackson.annotation.JsonFormat
import org.bson.types.ObjectId
import java.time.LocalDate

data class Person(
    val id: ObjectId,
    val name: String,
    val cpf: String,
    val address: String,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val createdAt: LocalDate,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val updatedAt: LocalDate? = LocalDate.now()
)
