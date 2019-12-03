package com.example.kafka.config

import com.example.kafka.config.utils.TEST_OUT_TOPIC
import com.example.kafka.config.utils.TEST_TOPIC
import com.example.kafka.config.utils.getStreamsConfiguration
import finch.json.Json
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.processor.StateStore
import org.apache.kafka.streams.state.KeyValueStore
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

    @Bean
    fun streams() = KafkaStreams(builder().build(), getStreamsConfiguration(servers))

    @Bean
    fun builder(): StreamsBuilder {
        var builder = StreamsBuilder()
        return builder
    }

    @Bean
    fun countTable(): KTable<String, Long> {
        val topicStream = builder().stream<String, Json>(TEST_TOPIC)

        val table = topicStream
                .filter { key, value -> this.filter(key, value) }
                .peek { key, value -> log.info("processing key {}. value {}", key, value) }
//                .groupBy { key, value -> value.get("value").asString() }
//                .aggregate(Materialized.`as`("custom-state-store")) {key:, value, aggregate->
//                    0L
//                }
                .groupByKey()
                .count(Materialized.`as`<String, Long, KeyValueStore<Bytes?,ByteArray?>>("aggregated-table-store") /* state store name */
                        .withValueSerde(Serdes.Long()))
//
//        table.toStream().to(TEST_OUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()))

        return table
    }

    fun filter(key: String, value: Json):Boolean {
        return value.get("value").asInt() > 5
    }


}