package com.bank.accounts.adapters.output.http.dto

import java.time.LocalDateTime

data class FindPersonByCpfErrorResponse(
    val timestamp: LocalDateTime,
    val path: String,
    val status: Int,
    val error: String,
    val requestId: String,
    val message: String,
    val developerMessage: String
)