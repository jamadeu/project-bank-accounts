package com.bank.accounts.adapters.output.http.dto

import com.bank.accounts.domain.model.Person
import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import javax.validation.constraints.NotBlank

data class FindPersonByCpfResponse(
    @field:NotBlank
    val id: String?,
    @field:NotBlank
    val name: String?,
    @field:NotBlank
    val cpf: String?,
    @field:NotBlank
    val address: String?,
    @field:NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    val createdAt: LocalDate?,
    @field:NotBlank
    @JsonFormat(pattern = "yyyy-MM-dd")
    val updatedAt: LocalDate?
) {
    fun toPerson(): Person =
        Person(
            id ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"),
            name ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"),
            cpf ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"),
            address ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"),
            createdAt ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"),
            updatedAt ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"),
        )
}