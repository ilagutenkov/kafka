package com.example.kafka.service

import com.example.kafka.config.KafkaConfig
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class StreamStateService(val kafkaConfig: KafkaConfig, val countTable: KTable<String, Long>) {

    @PostConstruct
    fun init(){
        var streams=kafkaConfig.streams()
        streams.cleanUp()

        streams.start()

        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { streams.close() }))
    }

    fun getValueByKey(key: String) {
        val streams=kafkaConfig.streams()

        val queryableStoreName = countTable.queryableStoreName() // returns null if KTable is not queryable
        val view = streams.store(queryableStoreName, QueryableStoreTypes.keyValueStore<String, Long>())
        view.get(key)
    }

}