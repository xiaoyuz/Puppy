package com.xiaoyuz.puppy.gatherer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.PropertySource

@SpringBootApplication(scanBasePackages = ["com.xiaoyuz.puppy"])
@PropertySource("classpath:/application.properties")
class GathererApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(GathererApplication::class.java, *args)
        }
    }
}