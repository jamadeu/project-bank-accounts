package com.bank.accounts.domain.repository

import com.bank.accounts.domain.model.Account
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface AccountRepository {
    fun findById(id: String): Mono<Account>
    fun findAllByAccountHolderCpf(cpf: String): Flux<Account>
    fun create(account: Account): Mono<Account>
    fun update(account: Account): Mono<Account>
    fun deleteById(id: String): Mono<Unit>
}