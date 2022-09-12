package com.bank.accounts.adapters.input.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/accounts")
class AccountController {


    @GetMapping
    fun findById(): Mono<String> = Mono.just("Find by id")
}