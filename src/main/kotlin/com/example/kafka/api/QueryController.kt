package com.example.kafka.api

import com.example.kafka.service.StreamStateService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/query")
class QueryController(val stateService: StreamStateService) {

    val log = LoggerFactory.getLogger(this.javaClass)


    @GetMapping("/test")
    fun keysCountTable(@RequestParam key: String): Long? {
        return stateService.getValueByKeyFromTest(key)
    }


}