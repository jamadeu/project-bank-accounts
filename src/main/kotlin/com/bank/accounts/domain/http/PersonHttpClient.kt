package com.bank.accounts.domain.http

import com.bank.accounts.domain.model.Person
import reactor.core.publisher.Mono

interface PersonHttpClient {
    fun getPersonByCpf(cpf: String): Mono<Person>
}