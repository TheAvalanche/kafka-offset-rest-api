package com.ak.kafka.rest

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import java.util.*

class KafkaClient(properties: Properties) {

    var client: AdminClient = AdminClient.create(properties)
    var consumer: KafkaConsumer<String, String> = KafkaConsumer(properties)

    fun topicLags(): List<TopicLag> {
        val groupIds = client.listConsumerGroups().all().get().map { s -> s.groupId() }
        val topicMap = mutableMapOf<TopicPartition, OffsetAndMetadata>()
        groupIds.forEach() { topicMap.putAll(client.listConsumerGroupOffsets(it).partitionsToOffsetAndMetadata().get()) }

        val endOffsets = consumer.endOffsets(topicMap.keys);

        return topicMap.map { e ->
            TopicLag(
                e.key.topic(),
                (endOffsets[e.key] ?: 0) - e.value.offset()
            )
        }

    }

}
