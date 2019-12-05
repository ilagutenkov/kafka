package com.example.kafka.api

import com.example.kafka.kafka.utils.MATCH_EVENT_TOPIC
import com.example.kafka.kafka.utils.PLAYER_TOPIC
import com.example.kafka.kafka.utils.TEST_TOPIC
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

        sender.send(PLAYER_TOPIC, "ronaldo", Json.json().set("id","ronaldo").set("name","cristianu ronaldo"))
        sender.send(PLAYER_TOPIC, "ronaldo", Json.json().set("id","ronaldo").set("name","cristianu ronaldo"))
        sender.send(PLAYER_TOPIC, "ivanov", Json.json().set("id","ivanov").set("name","alexey ivanov"))

        sender.send(MATCH_EVENT_TOPIC, "ivanov", Json.json().set("id","event1").set("player","ivanov").set("event","goal"))
    }



    @GetMapping
    fun sendTest() = sender.send(TEST_TOPIC, "1", Json.json().set("id", "test"))

    @GetMapping("/match-event")
    fun sendMatchEvent() = sender.send(MATCH_EVENT_TOPIC, "ronaldo", Json.json().set("id","event1").set("player","ronaldo").set("event","goal"))


    @GetMapping("/send")
    fun sendWithKey(@RequestParam id: Int, @RequestParam value: Int) = sender.send(TEST_TOPIC, id.toString(), Json.json().set("id", id).set("value", value))


}