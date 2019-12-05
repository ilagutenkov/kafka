package com.example.kafka.kafka

import com.example.kafka.config.KafkaConfig
import com.example.kafka.kafka.utils.STORE_NAME
import com.example.kafka.kafka.utils.TEST_TOPIC
import com.example.kafka.kafka.utils.getStreamsConfiguration
import finch.json.Json
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class StreamsFactory(val kafkaConfig: KafkaConfig) {

    val log = LoggerFactory.getLogger(this::class.java)
    lateinit var servers: String
    lateinit var streams: KafkaStreams
    val streamsMap=HashMap<StreamTypes,KafkaStreams>()

    @PostConstruct
    fun init() {
        servers = kafkaConfig.servers

        var builder = StreamsBuilder()

        val topicStream = builder.stream<String, Json>(TEST_TOPIC)

       /* val table =*/ topicStream
                .filter { key, value -> value.get("value").asInt() > 5 }
                .peek { key, value -> log.info("processing key {}. value {}", key, value) }
                .groupByKey()
                .count(Materialized.`as`<String, Long, KeyValueStore<Bytes?, ByteArray?>>(STORE_NAME) /* state store name */
                        .withValueSerde(Serdes.Long()))

        val streams = KafkaStreams(builder.build(), getStreamsConfiguration(servers))
        streamsMap[StreamTypes.TEST] = streams

        streams.cleanUp()

        streams.start()

        // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
        Runtime.getRuntime().addShutdownHook(Thread(Runnable { streams.close() }))
    }

    fun getStreams(type: StreamTypes)=streamsMap[type]

}