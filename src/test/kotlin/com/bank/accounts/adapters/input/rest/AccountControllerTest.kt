package com.bank.accounts.adapters.input.rest

import com.bank.accounts.adapters.input.rest.exception.CustomAttributes
import com.bank.accounts.application.AccountService
import com.bank.accounts.domain.model.Account
import com.bank.accounts.domain.model.AccountStatus
import com.bank.accounts.domain.model.Person
import com.bank.accounts.domain.repository.AccountRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.WebProperties.Resources
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@WebFluxTest
@Import(AccountService::class, Resources::class, CustomAttributes::class)
internal class AccountControllerTest {

    @MockBean
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var webClient: WebTestClient

    @Test
    fun `findById returns a mono with account when it exists`() {
        val account = getAccount()
        `when`(accountRepository.findById(account.id)).thenReturn(Mono.just(account))

        webClient
            .get()
            .uri("/accounts/${account.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody(Account::class.java)
            .isEqualTo<BodySpec<Account, *>>(account)
    }

    @Test
    fun `findById returns not found when account does not exists`() {
        `when`(accountRepository.findById(anyString())).thenReturn(Mono.empty())

        webClient
            .get()
            .uri("/accounts/631f235a57b766bcfd0ce780")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened")
            .jsonPath("$.message").isEqualTo("404 NOT_FOUND \"Account not found\"")
    }

    @Test
    fun `findAllByAccountHolderCpf returns a flux with account when it exists`() {
        val account = getAccount()
        `when`(accountRepository.findAllByAccountHolderCpf(account.accountHolder.cpf)).thenReturn(Flux.just(account))

        webClient
            .get()
            .uri("/accounts/cpf/${account.accountHolder.cpf}")
            .exchange()
            .expectStatus().isOk
            .expectBody(Array<Account>::class.java)
            .isEqualTo<BodySpec<Array<Account>, *>>(arrayOf(account))
    }

    @Test
    fun `findAllByAccountHolderCpf returns not found when account does not exists`() {
        `when`(accountRepository.findAllByAccountHolderCpf(anyString())).thenReturn(Flux.empty())

        webClient
            .get()
            .uri("/accounts/cpf/500.613.310-40")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened")
            .jsonPath("$.message").isEqualTo("404 NOT_FOUND \"Account not found\"")
    }

    private fun getAccount() = Account(
        id = "631f235357b766bcfd0ce77f",
        accountNumber = "1234",
        bankBranch = "5678",
        accountHolder = Person(
            id = "631f20f957b766bcfd0ce77e",
            name = "Test",
            cpf = "895.389.980-06",
            address = "address",
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now()
        ),
        status = AccountStatus.ACTIVE,
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now()
    )
}