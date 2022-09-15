package com.bank.accounts.domain.http

import com.bank.accounts.adapters.output.http.dto.FindPersonByCpfResponse
import reactor.core.publisher.Mono

interface PersonHttpClient {
    fun getPersonByCpf(cpf: String): Mono<FindPersonByCpfResponse>
}