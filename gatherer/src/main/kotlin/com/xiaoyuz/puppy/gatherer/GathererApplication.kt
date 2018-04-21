package com.xiaoyuz.puppy.gatherer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["com.xiaoyuz.puppy"])
@EnableScheduling
@PropertySource("classpath:/application.properties")
@EnableAsync
class GathererApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(GathererApplication::class.java, *args)
        }
    }
}