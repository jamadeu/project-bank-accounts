package com.bank.accounts.application

import com.bank.accounts.adapters.input.rest.dto.CreateAccountRequest
import com.bank.accounts.domain.http.PersonHttpClient
import com.bank.accounts.domain.model.Account
import com.bank.accounts.domain.model.AccountStatus
import com.bank.accounts.domain.repository.AccountRepository
import kotlinx.coroutines.reactor.awaitSingle
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

    suspend fun create(request: CreateAccountRequest): Mono<String> {
        createRequestIsValid(request)
        val person = personHttpClient
            .getPersonByCpf(request.accountHolderCpf!!)
            .switchIfEmpty(Mono.error {
                ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "AccountService - create : person is null"
                )
            })
            .log("AccountService.create", Level.INFO, SignalType.ON_COMPLETE)
        val accountToCreate = Account(
            accountNumber = request.accountNumber!!,
            bankBranch = request.bankBranch!!,
            accountHolder = person.awaitSingle(),
            status = AccountStatus.ACTIVE
        )
        return accountRepository
            .create(accountToCreate)
            .log("AccountService.create", Level.INFO, SignalType.ON_COMPLETE)
            .map { it.id }
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