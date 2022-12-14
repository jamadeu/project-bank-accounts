package com.bank.accounts.adapters.output.repository

import com.bank.accounts.domain.model.Account
import com.bank.accounts.domain.repository.AccountRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class AccountRepositoryImpl(private val springRepository: SpringRepository) : AccountRepository {
    override fun findById(id: String): Mono<Account> {
        return springRepository.findById(id)
    }

    override fun findAllByAccountHolderCpf(cpf: String): Flux<Account> {
        return springRepository.findAllByAccountHolderCpf(cpf)
    }

    override fun create(account: Account): Mono<Account> {
        return springRepository.save(account)
    }

    override fun update(account: Account): Mono<Account> {
        return springRepository.save(account)
    }

    override fun deleteById(id: String): Mono<Unit> {
        return springRepository
            .deleteById(id)
            .then(Mono.empty())
    }
}