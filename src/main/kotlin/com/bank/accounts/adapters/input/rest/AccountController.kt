package com.bank.accounts.adapters.input.rest

import com.bank.accounts.application.AccountService
import com.bank.accounts.domain.model.Account
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/accounts")
class AccountController(private val accountService: AccountService) {

    @GetMapping("/{id}")
    fun findById(@PathVariable("id") id: String): Mono<Account> = accountService.findById(id).log()

    @GetMapping("/cpf/{cpf}")
    fun findAllByAccountHolderCpf(@PathVariable("cpf") cpf: String): Flux<Account> =
        accountService.findAllByAccountHolderCpf(cpf).log()
}