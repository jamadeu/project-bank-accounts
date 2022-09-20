package com.bank.accounts.adapters.input.rest.dto

import javax.validation.constraints.NotBlank

data class UpdateAccountRequest(
    @field:NotBlank
    val accountHolderName: String?,
    @field:NotBlank
    val accountHolderAddress: String?,
)
