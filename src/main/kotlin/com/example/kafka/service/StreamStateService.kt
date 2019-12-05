package com.example.kafka.service

import com.example.kafka.kafka.StreamTypes
import com.example.kafka.kafka.StreamsFactory
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.springframework.stereotype.Service


@Service
class StreamStateService(val streamsFactory: StreamsFactory) {

    fun getValueByKeyFromTest(key: String): Long? {
        return getValueByKey(StreamTypes.TEST, key)
    }

    private fun getValueByKey(streamType: StreamTypes, key: String): Long? {
        val streams = streamsFactory.getStreams(streamType)
        val view = streams?.store(streamType.storeName, QueryableStoreTypes.keyValueStore<String, Long>())

        return view?.get(key)
    }

}