package com.xiaoyuz.puppy.datastore.manager.redis

import com.xiaoyuz.puppy.datastore.manager.COMMON_INDEX_PREFIX
import com.xiaoyuz.puppy.datastore.manager.redis.conf.PuppyRedisTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.DefaultTypedTuple
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

private const val INDEX_EXPIRE_DAYS = 1L

@Repository
open class IndexRedisRepository(@Autowired private val mObjectRedisTemplate: PuppyRedisTemplate<Any>) {

    fun addIndexes(name: String, indexes: List<Array<Any>>, scoreConvert: (Any) -> Double) {
        mObjectRedisTemplate.opsForZSet().add(getIndexKey(name),
                indexes.map { DefaultTypedTuple(it[0], scoreConvert(it[1])) }.toSet())
        mObjectRedisTemplate.expire(getIndexKey(name), INDEX_EXPIRE_DAYS, TimeUnit.DAYS)
    }

    fun getIndexes(name: String) = mObjectRedisTemplate.opsForZSet().reverseRange(getIndexKey(name), 0, -1)

    fun getIndexes(names: List<String>) = names.associate { it to getIndexes(it) }

    fun deleteIndex(name: String, index: Any) = mObjectRedisTemplate.opsForZSet().remove(getIndexKey(name), index)

    fun addIndex(name: String, index: Any, score: Double) = mObjectRedisTemplate.opsForZSet().add(getIndexKey(name), index, score)

    fun flushIndexes(name: String) = mObjectRedisTemplate.delete(getIndexKey(name))

    fun exists(name: String) = mObjectRedisTemplate.hasKey(getIndexKey(name))

    private fun getIndexKey(name: String) = "$COMMON_INDEX_PREFIX$name"
}
