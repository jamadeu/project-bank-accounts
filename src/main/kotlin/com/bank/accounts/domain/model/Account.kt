package com.bank.accounts.domain.model

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "accounts")
data class Account(

    @Id
    var id: String? = null,

    val accountNumber: String,

    val bankBranch: String,

    val accountHolder: Person,

    val status: AccountStatus,

    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    val createdAt: LocalDate = LocalDate.now(),

    @JsonFormat(pattern = "yyyy-MM-dd")
    val updatedAt: LocalDate = LocalDate.now(),
) {}
