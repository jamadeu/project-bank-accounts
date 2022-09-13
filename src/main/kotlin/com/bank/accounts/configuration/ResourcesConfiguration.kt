package com.bank.accounts.configuration

import org.springframework.boot.autoconfigure.web.WebProperties.Resources
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ResourcesConfiguration {

    @Bean
    fun resources(): Resources = Resources()
}