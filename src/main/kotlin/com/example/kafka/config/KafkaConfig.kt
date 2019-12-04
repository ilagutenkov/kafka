package com.example.kafka.config

import finch.json.Json
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import java.util.*


@Configuration
@ConfigurationProperties(prefix = "app.kafka")
class KafkaConfig {

    val log = LoggerFactory.getLogger(this::class.java)

    lateinit var servers: String

    @Bean
    fun producerFactory(): ProducerFactory<String, Json> {
        val configProps = HashMap<String, Any>()
        configProps[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = servers
        configProps[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProps[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        configProps[ProducerConfig.MAX_BLOCK_MS_CONFIG] = 500
        return DefaultKafkaProducerFactory<String, Json>(configProps)
    }


    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Json> {
        return KafkaTemplate<String, Json>(producerFactory())
    }

}