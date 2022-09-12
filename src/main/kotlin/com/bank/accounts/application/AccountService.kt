package com.bank.accounts.application

import com.bank.accounts.domain.model.Account
import com.bank.accounts.domain.repository.AccountRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Service
class AccountService(private val accountRepository: AccountRepository) {

    fun findById(id: String): Mono<Account> =
        accountRepository.findById(id)
            .switchIfEmpty(Mono.error { ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found") })
            .log()
}