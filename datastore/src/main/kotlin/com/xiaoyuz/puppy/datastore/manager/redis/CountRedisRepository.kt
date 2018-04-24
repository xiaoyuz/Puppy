package com.xiaoyuz.puppy.datastore.manager.redis

import com.xiaoyuz.puppy.datastore.manager.redis.conf.PuppyRedisTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

private const val EXPIRE_DAY = 1L

/**
 * Common redis repo for list count.
 */
@Repository
class CountRedisRepository(@Autowired private val mObjectRedisTemplate: PuppyRedisTemplate<Any>,
                           private val mLongRedisTemplate: PuppyRedisTemplate<Long>) {

    fun exists(key: String) = mObjectRedisTemplate.hasKey(key)

    fun incr(key: String) = if (exists(key)) mObjectRedisTemplate.opsForValue().increment(key, 1) else -1

    fun descr(key: String) = if (exists(key)) mObjectRedisTemplate.opsForValue().increment(key, -1) else -1

    fun setCount(key: String, count: Int) {
        mObjectRedisTemplate.opsForValue().set(key, count)
        mObjectRedisTemplate.expire(key, EXPIRE_DAY, TimeUnit.DAYS)
    }

    fun getCount(key: String): Int? = (mObjectRedisTemplate.opsForValue().get(key))?.let { it as Int }

    fun getCountWithExpire(key: String): Int? = with(mObjectRedisTemplate) {
        opsForValue().get(key)?.let {
            expire(key, EXPIRE_DAY, TimeUnit.DAYS)
            it as Int
        }
    }

    fun setCount(key: String, count: Long) {
        mLongRedisTemplate.opsForValue().setIfAbsent(key, count)
        mLongRedisTemplate.expire(key, EXPIRE_DAY, TimeUnit.DAYS)
    }

    fun incWithExpire(key: String): Long {
        val result = mLongRedisTemplate.opsForValue().increment(key, 1)
        mLongRedisTemplate.expire(key, EXPIRE_DAY, TimeUnit.DAYS)
        return result
    }

    fun getCountForLong(key: String): Long? = mLongRedisTemplate.opsForValue().get(key)

    fun flushCount(key: String) {
        mObjectRedisTemplate.delete(key)
    }

    fun existsHashKey(key: String, hashKey: String) = mObjectRedisTemplate.opsForHash<String, Int>().hasKey(key, hashKey)

    fun incrHash(key: String, hashKey: String) = if (existsHashKey(key, hashKey)) {
        mObjectRedisTemplate.opsForHash<String, Int>().increment(key, hashKey, 1)
    } else -1

    fun descrHash(key: String, hashKey: String) = if (existsHashKey(key, hashKey)) {
        mObjectRedisTemplate.opsForHash<String, Int>().increment(key, hashKey, -1)
    } else -1

    fun setCountInHash(key: String, hashKey: String, count: Int) {
        mObjectRedisTemplate.opsForHash<String, Int>().put(key, hashKey, count)
        mObjectRedisTemplate.expire(key, EXPIRE_DAY, TimeUnit.DAYS)
    }

    fun getCountFromHash(key: String, hashKey: String): Int? = mObjectRedisTemplate.opsForHash<String, Int>().get(key, hashKey)
}
