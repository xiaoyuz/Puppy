package com.xiaoyuz.puppy.gatherer.client.config

import com.clickntap.vimeo.Vimeo
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class ClientsConfig {

    @Value("\${third_party.vimeo.token}")
    private lateinit var mToken: String

    @Bean
    fun webClient() = WebClient(BrowserVersion.CHROME).apply {
        options.isUseInsecureSSL = true
        options.isJavaScriptEnabled = true
        options.isCssEnabled = false
        options.isThrowExceptionOnScriptError = false
        options.timeout = 10000
        options.isDoNotTrackEnabled = false
    }

    @Bean
    fun vimeo() = Vimeo(mToken)
}