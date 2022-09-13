package com.bank.accounts.adapters.output.http

import com.bank.accounts.adapters.output.http.dto.FindPersonByCpfErrorResponse
import com.bank.accounts.configuration.HttpClientConfiguration
import com.bank.accounts.domain.http.PersonHttpClient
import com.bank.accounts.domain.model.Person
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType
import java.util.logging.Level

@Service
class PersonHttpClientImpl(
    @Value("\${persons.by-cpf}")
    private val byCpf: String,
    private val httpClientConfiguration: HttpClientConfiguration
) : PersonHttpClient {

    override fun getPersonByCpf(cpf: String): Mono<Person> {
        return httpClientConfiguration.webClient()
            .get()
            .uri(byCpf, cpf)
            .retrieve()
            .onStatus(HttpStatus::isError) { response ->
                response.bodyToMono(FindPersonByCpfErrorResponse::class.java)
                    .flatMap { error ->
                        Mono.error<ResponseStatusException>(ResponseStatusException(response.statusCode(), error.message))
                            .log("HttpClient.getPersonByCpf", Level.WARNING, SignalType.ON_ERROR)
                    }
            }
            .bodyToMono(Person::class.java)
            .log("HttpClient.getPersonByCpf", Level.INFO, SignalType.ON_COMPLETE)
    }
}