package com.example.kafka.kafka.utils

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerde
import java.util.*

const val STORE_NAME = "aggregated-table-store"
const val TEST_TOPIC = "test"
const val PLAYER_TOPIC = "player"
const val MATCH_EVENT_TOPIC = "match_event"
const val PLAYER_EVENT_TOPIC = "player_event"


const val TEST_APP="test-app"
const val TEST_CLIENT="test-client"
const val PLAYER_APP="player-app"
const val PLAYER_CLIENT="player-client"

const val MATCH_APP="match-app"
const val MATCH_CLIENT="match-clie"

fun getStreamsConfiguration(bootstrapServers: String, applicationName: String, client: String): Properties {
    val streamsConfiguration = Properties()
    // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
    // against which the application is run.
    streamsConfiguration[StreamsConfig.APPLICATION_ID_CONFIG] = applicationName
    streamsConfiguration[StreamsConfig.CLIENT_ID_CONFIG] = client
    // Where to find Kafka broker(s).
    streamsConfiguration[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
    // Specify default (de)serializers for record keys and for record values.
    streamsConfiguration[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
    streamsConfiguration[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = JsonSerde::class.java
    // Records should be flushed every 10 seconds. This is less than the default
    // in order to keep this example interactive.
    streamsConfiguration[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = 10 * 1000
    // For illustrative purposes we disable record caches.
    streamsConfiguration[StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG] = 0
    // Use a temporary directory for storing state, which will be automatically removed after the test.
    streamsConfiguration[StreamsConfig.STATE_DIR_CONFIG] = "./state"
    streamsConfiguration[JsonDeserializer.TRUSTED_PACKAGES] = "*"
    streamsConfiguration[JsonDeserializer.USE_TYPE_INFO_HEADERS] = "true"
    streamsConfiguration[JsonDeserializer.VALUE_DEFAULT_TYPE] = "finch.json.Json"
    return streamsConfiguration
}