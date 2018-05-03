package com.xiaoyuz.puppy.common.rest

import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {
    @Bean
    @Primary
    fun restTemplate(): RestTemplate {
        return RestTemplate(httpRequestFactory())
    }

    @Bean
    @Primary
    fun httpRequestFactory(): ClientHttpRequestFactory {
        return HttpComponentsClientHttpRequestFactory(httpClient()).apply {
            setReadTimeout(2000)
            setConnectTimeout(2000)
        }
    }

    @Bean
    fun longTimeHttpRequestFactory(): ClientHttpRequestFactory {
        return HttpComponentsClientHttpRequestFactory(httpClient()).apply {
            setReadTimeout(120000)
            setConnectTimeout(30000)
        }
    }

    @Bean
    @Primary
    fun httpClient(): HttpClient {
        val connectionManager = PoolingHttpClientConnectionManager()
        connectionManager.maxTotal = 500
        connectionManager.defaultMaxPerRoute = 50

        return HttpClientBuilder
                .create()
                .setConnectionManager(connectionManager)
                .build()
    }
}