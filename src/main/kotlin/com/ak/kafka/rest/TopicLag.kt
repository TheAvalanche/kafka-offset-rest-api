package com.ak.kafka.rest

data class TopicLag(val topic: String, val lag: Long)
