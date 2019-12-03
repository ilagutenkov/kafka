package com.example.kafka.api

import com.example.kafka.config.utils.TEST_TOPIC
import com.example.kafka.service.KafkaSendingService
import finch.json.Json
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.annotation.PostConstruct

@RestController
@RequestMapping("/command")
class CommandController(val sender: KafkaSendingService) {


    @PostConstruct
    fun init() {
        sender.send(TEST_TOPIC, "1", Json.json().set("id", 1).set("value", 1))
        sender.send(TEST_TOPIC, "2", Json.json().set("id", 2).set("value", 10))
        sender.send(TEST_TOPIC, "3", Json.json().set("id", 3).set("value", 100))
    }

    @GetMapping
    fun sendTest() = sender.send(TEST_TOPIC, "1", Json.json().set("id", "test"))


    @GetMapping("/send")
    fun sendWithKey(@RequestParam id: Int, @RequestParam value: Int) = sender.send(TEST_TOPIC, id.toString(), Json.json().set("id", id).set("value", value))


}