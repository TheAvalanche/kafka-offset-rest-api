package com.ak.kafka.rest

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.ListTopicsOptions
import org.apache.kafka.clients.admin.TopicListing
import java.util.*
import java.util.concurrent.ExecutionException


fun main(args: Array<String>) {
    // First we need to initialize Kafka properties
    val properties = Properties()
    properties["bootstrap.servers"] = "ig01-t:19092,ig02-t:19092,ig03-t:19092"
    properties["client.id"] = "java-admin-client"
    properties["key.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer";
    properties["value.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer";
    println("***** Topics *****")
    printTopicDetails(properties)
    println("***** Topics Description *****")
    printTopicDescription(properties)
}

private fun printTopicDetails(properties: Properties) {
    var listings: Collection<TopicListing>
    // Create  an AdminClient using the properties initialized earlier
    try {
        AdminClient.create(properties).use { client ->
            listings = getTopicListing(client, true)
            listings.forEach { topic -> println("Name: " + topic.name() + ", isInternal: " + topic.isInternal) }
        }
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
    } catch (e: ExecutionException) {
        e.printStackTrace();
        //LOGGER.log(Level.SEVERE, "Failed to get topic list {0}", e.cause)
    }

}

private fun printTopicDescription(properties: Properties) {
    var listings: Collection<TopicListing>
    // Create  an AdminClient using the properties initialized earlier
    try {
        AdminClient.create(properties).use { client ->
            listings = getTopicListing(client, false)
            val topics = listings.map({ it.name() })

            val result = client.describeTopics(topics)
            result.values().forEach { (key, value) ->
                try {
                    println(key + ": " + value.get())
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                } catch (e: ExecutionException) {
                    e.printStackTrace();
                    //LOGGER.log(Level.SEVERE, "Failed to execute", e.cause)
                }
            }
        }
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
    } catch (e: ExecutionException) {
        e.printStackTrace();
        //LOGGER.log(Level.SEVERE, "Failed to get topic list", e.cause)
    }

}
@Throws(InterruptedException::class, ExecutionException::class)
private fun getTopicListing(client: AdminClient, isInternal: Boolean): Collection<TopicListing> {
    val options = ListTopicsOptions()
    options.listInternal(isInternal)
    return client.listTopics(options).listings().get()
}
