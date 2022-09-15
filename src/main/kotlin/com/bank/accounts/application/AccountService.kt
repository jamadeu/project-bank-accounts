package com.bank.accounts.application

import com.bank.accounts.adapters.input.rest.dto.CreateAccountRequest
import com.bank.accounts.domain.http.PersonHttpClient
import com.bank.accounts.domain.model.Account
import com.bank.accounts.domain.model.AccountStatus
import com.bank.accounts.domain.repository.AccountRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType
import java.util.logging.Level

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val personHttpClient: PersonHttpClient
) {
    fun findById(id: String): Mono<Account> =
        accountRepository
            .findById(id)
            .switchIfEmpty(Mono.error { ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found") })
            .log("AccountService.findById", Level.INFO, SignalType.ON_COMPLETE)

    fun findAllByAccountHolderCpf(cpf: String): Flux<Account> =
        accountRepository
            .findAllByAccountHolderCpf(cpf)
            .switchIfEmpty(Flux.error { ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found") })
            .log("AccountService.findAllByAccountHolderCpf", Level.INFO, SignalType.ON_COMPLETE)

    fun create(request: CreateAccountRequest): Mono<Account> {
        createRequestIsValid(request)
        return personHttpClient
            .getPersonByCpf(request.accountHolderCpf!!)
            .switchIfEmpty(Mono.error {
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "AccountService - create.getPersonByCpf : Person not found"
                )
            })
            .flatMap { Mono.just(it.toPerson()) }
            .flatMap {
                accountRepository
                    .create(
                        Account(
                            accountNumber = request.accountNumber!!,
                            bankBranch = request.bankBranch!!,
                            accountHolder = it,
                            status = AccountStatus.ACTIVE
                        )
                    )
            }
            .log("AccountService.personHttpClient.getPersonByCpf", Level.INFO, SignalType.ON_COMPLETE)
    }

    private fun createRequestIsValid(request: CreateAccountRequest) {
        if (request.accountHolderCpf.isNullOrBlank() ||
            request.accountNumber.isNullOrBlank() ||
            request.bankBranch.isNullOrBlank()
        ) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST, "AccountService - create : request is invalid - $request"
            )
        }
        accountRepository
            .findByAccountNumberAndBankBranch(request.accountNumber, request.bankBranch)
            .doOnNext {
                if (it != null) throw ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "AccountService - create : account already exists - $request"
                )
            }
    }

}