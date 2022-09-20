package com.bank.accounts.adapters.input.rest

import com.bank.accounts.adapters.input.rest.dto.CreateAccountRequest
import com.bank.accounts.adapters.input.rest.dto.UpdateAccountRequest
import com.bank.accounts.application.AccountService
import com.bank.accounts.domain.model.Account
import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(AccountController::class.java)

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
    fun create(@Valid @RequestBody request: CreateAccountRequest): Mono<ResponseEntity<Unit>> {
        return accountService
            .create(request)
            .map<ResponseEntity<Unit>?> {
                ResponseEntity.created(
                    URI.create(it.id!!)
                ).build()
            }
            .log("AccountController.create", Level.INFO, SignalType.ON_COMPLETE)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: String,
        @Valid @RequestBody request: UpdateAccountRequest
    ): Mono<ResponseEntity<Unit>> {
        accountService.update(id, request)
        return Mono
            .just<ResponseEntity<Unit>?>(ResponseEntity.ok().build())
            .log("AccountController.update", Level.INFO, SignalType.ON_COMPLETE)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: String): Mono<ResponseEntity<Unit>> {
        return accountService.delete(id)
            .map<ResponseEntity<Unit>?> { ResponseEntity.ok().build() }
            .log("AccountController.delete", Level.INFO, SignalType.ON_COMPLETE)
    }
}