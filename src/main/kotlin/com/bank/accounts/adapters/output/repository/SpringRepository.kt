package com.bank.accounts.adapters.output.repository

import com.bank.accounts.domain.model.Account
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface SpringRepository : ReactiveMongoRepository<Account, String> {
    fun findAllByAccountHolderCpf(cpf: String): Flux<Account>
}