package com.example.kafka.service

import finch.json.Json
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaSendingService(val sender: KafkaTemplate<String, Json>) {

    val log = LoggerFactory.getLogger(this::class.java)

    fun send(topic: String, key: String, json: Json) {
        log.info("sending {}", json)
        sender.send(topic, key, json)
    }

}