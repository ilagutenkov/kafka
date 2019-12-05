package com.example.kafka.kafka

import com.example.kafka.config.KafkaConfig
import com.example.kafka.kafka.utils.*
import finch.json.Json
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct


@Component
class StreamsFactory(val kafkaConfig: KafkaConfig) {

    val log = LoggerFactory.getLogger(this::class.java)
    lateinit var servers: String
    val streamsMap = HashMap<StreamTypes, KafkaStreams>()

    @PostConstruct
    fun init() {
        servers = kafkaConfig.servers

        var builder = StreamsBuilder()

        val topicStream = builder.stream<String, Json>(TEST_TOPIC)

        topicStream
                .filter { key, value -> value.get("value").asInt() > 5 }
                .peek { key, value -> log.info("processing key {}. value {}", key, value) }
                .groupByKey()
                .count(Materialized.`as`<String, Long, KeyValueStore<Bytes?, ByteArray?>>(StreamTypes.TEST.storeName) /* state store name */
                        .withValueSerde(Serdes.Long()))

        var playerKStream = builder.stream<String, Json>(PLAYER_TOPIC)
        playerKStream.peek { _, value -> log.info("process player {}", value) }


        var matchEventKStream = builder.stream<String, Json>(MATCH_EVENT_TOPIC)
        matchEventKStream.peek { _, value -> log.info("process match_event {}", value) }


        log.info("start joining player-match_event")
        val joined = playerKStream.join(matchEventKStream,
                { playerValue, matchValue -> Json.json().set("id", playerValue.get("id")).set("player", playerValue.get("name")).set("event", matchValue?.get("event")) }, /* ValueJoiner */
                JoinWindows.of(TimeUnit.MINUTES.toMillis(5))
//                , Joined.with<String, Json, Json>(
//                        Serdes.String(), /* key */
//                        JsonSerde(), /* left value */
//                        JsonSerde())  /* right value */
        )
                .peek { _, item -> log.info("process player_event {}", item) }
                .to(PLAYER_EVENT_TOPIC)

        val streams = KafkaStreams(builder.build(), getStreamsConfiguration(servers, TEST_APP, TEST_CLIENT))
        streamsMap[StreamTypes.TEST] = streams


        streamsMap.forEach { (_, stream) ->
            stream.cleanUp()
            stream.start()
            // Add shutdown hook to respond to SIGTERM and gracefully close the Streams application.
            Runtime.getRuntime().addShutdownHook(Thread(Runnable { stream.close() }))
            log.info("stream started {}", stream)
        }


    }

    fun getStreams(type: StreamTypes) = streamsMap[type]

}