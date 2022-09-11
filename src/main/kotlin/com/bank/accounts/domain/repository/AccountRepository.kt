package com.bank.accounts.domain.repository

import com.bank.accounts.domain.model.Account


interface AccountRepository {

    fun findById(id: String): Account?

    fun findByPersonCpf(cpf: String): Account?

    fun findByAccountNumberAndBankBranch(accountNumber: String, bankBranch: String): Account?

    fun create(account: Account): Account

    fun update(account: Account): Account

    fun deleteById(id: String)
}