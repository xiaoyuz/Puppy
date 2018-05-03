package com.xiaoyuz.puppy.datastore.manager.redis

import com.xiaoyuz.puppy.datastore.manager.SESSION_DEVICE_KEY
import com.xiaoyuz.puppy.datastore.manager.SESSION_SID_KEY
import com.xiaoyuz.puppy.datastore.manager.redis.conf.PuppyRedisTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

private const val SESSION_EXPIRE_HOURS = 1L

@Repository
open class SessionRedisRepository(@Autowired private val mObjectRedisTemplate: PuppyRedisTemplate<Any>) {

    fun addSessions(deviceId: String?, sid: String, sessions: List<Any>?) {
        if (sessions != null && !sessions.isEmpty()) {
            mObjectRedisTemplate.opsForList().rightPushAll(getSessionKey(sid), *(sessions.toTypedArray()))
            mObjectRedisTemplate.expire(getSessionKey(sid), SESSION_EXPIRE_HOURS, TimeUnit.HOURS)
            deviceId?.let {
                mObjectRedisTemplate.opsForValue().set(getSessionDeviceKey(it), sid, SESSION_EXPIRE_HOURS, TimeUnit.HOURS)
            }
        }
    }

    fun getSessionSize(sid: String) = mObjectRedisTemplate.opsForList().size(getSessionKey(sid))

    fun getSessions(sid: String, start: Int, end: Int) = with(mObjectRedisTemplate) {
        expire(getSessionKey(sid), SESSION_EXPIRE_HOURS, TimeUnit.HOURS)
        opsForList().range(getSessionKey(sid), start.toLong(), end.toLong())
    }

    fun deleteSession(deviceId: String) {
        val sessionDeviceKey = getSessionDeviceKey(deviceId)
        val oldSid = mObjectRedisTemplate.opsForValue().get(sessionDeviceKey)?.toString()
        oldSid?.let {
            mObjectRedisTemplate.delete(getSessionKey(it))
        }
        mObjectRedisTemplate.delete(sessionDeviceKey)
    }

    fun hasSession(sid: String) = mObjectRedisTemplate.hasKey(getSessionKey(sid))

    private fun getSessionKey(sid: String) = "$SESSION_SID_KEY$sid"

    private fun getSessionDeviceKey(deviceId: String) = "$SESSION_DEVICE_KEY$deviceId"
}
