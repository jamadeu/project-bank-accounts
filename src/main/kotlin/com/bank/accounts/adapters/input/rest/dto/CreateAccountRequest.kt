package com.bank.accounts.adapters.input.rest.dto

import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.NotBlank

data class CreateAccountRequest(
    @field:NotBlank
    val accountNumber: String?,
    @field:NotBlank
    val bankBranch: String?,
    @field:NotBlank
    @field:CPF
    val accountHolderCpf: String?
)
