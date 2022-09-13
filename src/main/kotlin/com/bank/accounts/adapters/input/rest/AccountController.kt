package com.bank.accounts.adapters.input.rest

import com.bank.accounts.adapters.input.rest.dto.CreateAccountRequest
import com.bank.accounts.application.AccountService
import com.bank.accounts.domain.model.Account
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType
import java.net.URI
import java.util.logging.Level
import javax.validation.Valid


@RestController
@RequestMapping("/accounts")
class AccountController(private val accountService: AccountService) {

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: String): Mono<ResponseEntity<Account>> {
        val account = accountService.findById(id)
        return account
            .map { ResponseEntity.ok(it) }
            .log("AccountController.findById", Level.INFO, SignalType.ON_COMPLETE)
    }

    @GetMapping("/cpf/{cpf}")
    fun findAllByAccountHolderCpf(@PathVariable("cpf") cpf: String): Flux<Account> =
        accountService
            .findAllByAccountHolderCpf(cpf)
            .log("AccountController.findAllByAccountHolderCpf", Level.INFO, SignalType.ON_COMPLETE)

    @PostMapping
    suspend fun create(@Valid @RequestBody request: CreateAccountRequest): Mono<ResponseEntity<Unit>> =
        accountService
            .create(request)
            .map {
                ResponseEntity
                    .created(URI.create(it))
                    .body(Unit)
            }
            .log("AccountController.create", Level.INFO, SignalType.ON_COMPLETE)

}