package com.ak.kafka.rest

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import java.util.*

fun Application.main() {
    val properties = Properties()
    properties["bootstrap.servers"] = "ig01-p:19092,ig02-p:19092,ig03-p:19092"
    properties["client.id"] = "java-admin-client"
    properties["key.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer";
    properties["value.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer";

    val kafkaClient = KafkaClient(properties);

    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
        }
    }
    install(Routing) {
        get("/") {
            call.respond(mapOf("status" to "ok"))
        }
        get("/lags") {
            call.respond(kafkaClient.topicLags())
        }
        get("/lags/filtered") {
            call.respond(kafkaClient.topicLags().filter { it.lag > 0 })
        }
    }
}
