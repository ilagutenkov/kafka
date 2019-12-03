package com.example.kafka.api

import com.example.kafka.config.utils.TEST_OUT_TOPIC
import com.example.kafka.service.StreamStateService
import org.apache.kafka.streams.kstream.KTable
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/query")
class QueryController(val stateService: StreamStateService) {

    val log = LoggerFactory.getLogger(this.javaClass)


    @GetMapping
    fun keysCountTable(@RequestParam key:String) {
        log.info("keysCountTable")

        stateService.getValueByKey(key)

    }

//    @GetMapping("/send")
//    fun tableToTopic(@RequestParam(required = false, defaultValue = TEST_OUT_TOPIC) topic: String) = countTable.toStream().to(topic)

}