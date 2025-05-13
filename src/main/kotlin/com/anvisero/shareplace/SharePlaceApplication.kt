package com.anvisero.shareplace

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.web.client.RestTemplate

@EnableMongoAuditing
@SpringBootApplication
class SharePlaceApplication {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}

fun main(args: Array<String>) {
    runApplication<SharePlaceApplication>(*args)
}

