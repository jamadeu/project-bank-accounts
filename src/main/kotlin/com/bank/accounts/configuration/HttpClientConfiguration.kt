package com.bank.accounts.configuration

import com.bank.accounts.domain.model.Person
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class HttpClientConfiguration(
    @Value("\${persons.base-url}")
    private val baseUrl: String
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun webClient(): WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .filter(logRequestFilter())
        .filter(logResponseFilter())
        .build()

    @Bean
    fun logRequestFilter(): ExchangeFilterFunction = ExchangeFilterFunction.ofRequestProcessor {
        run {
            logger.info(
                "method={}, uri={}, headers={}",
                it.method(), it.url(), it.headers()
            )
        }
        Mono.just(it)
    }

    @Bean
    fun logResponseFilter(): ExchangeFilterFunction = ExchangeFilterFunction.ofResponseProcessor {
        run {
            logger.info(
                "body={}, status={}, headers={}",
                it.bodyToMono(Person::class.java).single(), it.statusCode(), it.headers().asHttpHeaders()
            )
        }
        Mono.just(it)
    }
}