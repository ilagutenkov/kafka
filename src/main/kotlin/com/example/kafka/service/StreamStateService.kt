package com.example.kafka.service

import com.example.kafka.kafka.StreamTypes
import com.example.kafka.kafka.StreamsFactory
import org.springframework.stereotype.Service


@Service
class StreamStateService(val streamsFactory: StreamsFactory) {

    fun getValueByKeyFromTest(key: String): Long? {
        val view = streamsFactory.getView(StreamTypes.TEST)
        return view?.get(key)
    }

}