package com.bank.accounts.adapters.input.rest

import com.bank.accounts.adapters.input.rest.dto.CreateAccountRequest
import com.bank.accounts.adapters.input.rest.exception.CustomAttributes
import com.bank.accounts.adapters.output.http.dto.FindPersonByCpfResponse
import com.bank.accounts.application.AccountService
import com.bank.accounts.domain.model.Account
import com.bank.accounts.domain.model.AccountStatus
import com.bank.accounts.domain.model.Person
import com.bank.accounts.domain.repository.AccountRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.WebProperties.Resources
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@WebFluxTest
@Import(AccountService::class, Resources::class, CustomAttributes::class)
internal class AccountControllerTest {

    @MockBean
    lateinit var accountRepository: AccountRepository

    @MockBean
    lateinit var webClient: WebClient

    @Mock
    lateinit var requestBodyUriSpec: WebClient.RequestBodyUriSpec

    @Mock
    lateinit var requestBodySpec: WebClient.RequestBodySpec

    @Mock
    lateinit var responseSpec: WebClient.ResponseSpec

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun `findById returns a mono with account when it exists`() {
        val account = getAccount()
        `when`(accountRepository.findById(account.id.toString())).thenReturn(Mono.just(account))

        webTestClient
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

        webTestClient
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

        webTestClient
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

        webTestClient
            .get()
            .uri("/accounts/cpf/500.613.310-40")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened")
            .jsonPath("$.message").isEqualTo("404 NOT_FOUND \"Account not found\"")
    }

    @Test
    fun `create returns a mono with account when successful`() {
        val person = getPerson()
        val findPersonByCpfResponse = getFindPersonByCpfResponse(person)
        val account = getAccount(person)
        val accountToCreate = Account(
            accountNumber = account.accountNumber,
            bankBranch = account.bankBranch,
            accountHolder = person,
            status = AccountStatus.ACTIVE
        )
        val request = getCreateRequest()
        `when`(webClient.get()).thenReturn(requestBodyUriSpec)
        `when`(requestBodyUriSpec.uri(anyString(), anyString())).thenReturn(requestBodySpec)
        `when`(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec)
        `when`(requestBodySpec.retrieve()).thenReturn(responseSpec)
        `when`(responseSpec.onStatus(any(), any())).thenReturn(responseSpec)
        `when`(responseSpec.bodyToMono(FindPersonByCpfResponse::class.java))
            .thenReturn(Mono.just(findPersonByCpfResponse))
        `when`(accountRepository.create(accountToCreate)).thenReturn(Mono.just(account))

        webTestClient
            .post()
            .uri("/accounts")
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isCreated
            .expectHeader()
            .location(account.id.toString())
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `create returns bad request when accountNumber is null`(accountNumber: String?) {
        val request = getCreateRequest(accountNumber = accountNumber)

        webTestClient
            .post()
            .uri("/accounts")
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened")
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    fun `create returns bad request when bankBranch is null`(bankBranch: String?) {
        val request = getCreateRequest(bankBranch = bankBranch)

        webTestClient
            .post()
            .uri("/accounts")
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened")
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = ["111.111.111-11"])
    fun `create returns bad request when cpf is null or invalid`(cpf: String?) {
        val request = getCreateRequest(accountHolderCpf = cpf)

        webTestClient
            .post()
            .uri("/accounts")
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened")
    }

    private fun getCreateRequest(
        accountNumber: String? = "1234",
        bankBranch: String? = "5678",
        accountHolderCpf: String? = "291.521.810-22"
    ) = CreateAccountRequest(
        accountNumber = accountNumber,
        bankBranch = bankBranch,
        accountHolderCpf = accountHolderCpf
    )

    private fun getFindPersonByCpfResponse(person: Person) = FindPersonByCpfResponse(
        id = person.id,
        name = person.name,
        cpf = person.cpf,
        address = person.address,
        createdAt = person.createdAt,
        updatedAt = person.updatedAt
    )

    private fun getPerson() = Person(
        id = "631cbd81e289015a8c508341",
        name = "Person",
        cpf = "291.521.810-22",
        address = "Some address",
        createdAt = LocalDate.of(2022, 9, 10),
        updatedAt = LocalDate.of(2022, 9, 10)
    )

    private fun getAccount(
        person: Person = Person(
            id = "631f20f957b766bcfd0ce77e",
            name = "Test",
            cpf = "895.389.980-06",
            address = "address",
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now()
        )
    ) = Account(
        id = "631f235357b766bcfd0ce77f",
        accountNumber = "1234",
        bankBranch = "5678",
        accountHolder = person,
        status = AccountStatus.ACTIVE,
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now()
    )
}