package com.xiaoyuz.puppy.datastore.manager.redis.conf

import com.xiaoyuz.puppy.datastore.model.Post
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.RedisSerializer
import java.time.Duration

const val DEFAULT_EXPIRE_DAY: Long = 2 // TWO DAY

@Configuration
class RedisConf {

    @Bean
    fun stringRedisTemplate(factory: RedisConnectionFactory) = StringRedisTemplate(factory)

    @Bean
    fun intRedisTemplate(factory: RedisConnectionFactory) = PuppyRedisTemplate(PuppyRedisSerializer.integer, factory)

    @Bean
    fun longRedisTemplate(factory: RedisConnectionFactory) = PuppyRedisTemplate(PuppyRedisSerializer.long, factory)

    @Bean
    fun booleanRedisTemplate(factory: RedisConnectionFactory) = PuppyRedisTemplate(PuppyRedisSerializer.boolean,
            factory)

    @Bean
    fun objectRedisTemplate(factory: RedisConnectionFactory) = PuppyRedisTemplate<Any>(PuppyRedisSerializer.objects,
            factory)

    @Bean
    fun postRedisTemplate(factory: RedisConnectionFactory) = PuppyRedisTemplate<Post>(PuppyRedisSerializer.post,
            factory)

    @Bean("postCacheManager")
    @Primary
    fun postCacheManager(factory: RedisConnectionFactory): RedisCacheManager
            = genCacheManager(factory, PuppyRedisSerializer.post, Duration.ofDays(DEFAULT_EXPIRE_DAY))

    private fun genCacheManager(factory: RedisConnectionFactory, valueSerializer: RedisSerializer<*>, ttl: Duration)
            = RedisCacheManager.builder(factory).cacheDefaults(defaultCacheConfig().disableKeyPrefix()
            .serializeValuesWith(SerializationPair.fromSerializer(valueSerializer))
            .entryTtl(ttl).disableCachingNullValues()).build()

}