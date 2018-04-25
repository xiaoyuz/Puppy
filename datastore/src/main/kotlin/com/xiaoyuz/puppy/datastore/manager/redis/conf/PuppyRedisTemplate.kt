package com.xiaoyuz.puppy.datastore.manager.redis.conf

import com.fasterxml.jackson.databind.ObjectMapper
import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.datastore.model.Video
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

class PuppyRedisTemplate<T>(jsonRedisSerializer: Jackson2JsonRedisSerializer<T>) : RedisTemplate<String, T>() {
    init {
        val stringSerializer = StringRedisSerializer()
        keySerializer = stringSerializer
        valueSerializer = jsonRedisSerializer

        hashKeySerializer = stringSerializer
        hashValueSerializer = jsonRedisSerializer
    }

    constructor(jsonRedisSerializer: Jackson2JsonRedisSerializer<T>,
                redisConnectionFactory: RedisConnectionFactory) : this(jsonRedisSerializer) {
        connectionFactory = redisConnectionFactory
        afterPropertiesSet()
    }
}

object PuppyRedisSerializer {
    val integer = Jackson2JsonRedisSerializer(Int::class.java)
    val long = Jackson2JsonRedisSerializer(Long::class.java)
    val boolean = Jackson2JsonRedisSerializer(Boolean::class.java)
    val objects = Jackson2JsonRedisSerializer(Any::class.java)
    val post = Jackson2JsonRedisSerializer(Post::class.java)
    var videoList = Jackson2JsonRedisSerializer<List<Video>>(ObjectMapper().typeFactory
            .constructParametricType(List::class.java, Video::class.java))
}