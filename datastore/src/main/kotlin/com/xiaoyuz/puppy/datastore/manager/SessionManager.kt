package com.xiaoyuz.puppy.datastore.manager

import com.xiaoyuz.puppy.datastore.manager.redis.SessionRedisRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class SessionManager {

    @Autowired
    private lateinit var mSessionRedisRepository: SessionRedisRepository

    fun setSessions(deviceId: String?, sid: String, sessions: List<Any>?) {
        mSessionRedisRepository.addSessions(deviceId, sid, sessions)
    }

    fun getSessionSize(sid: String) = mSessionRedisRepository.getSessionSize(sid).toInt()

    fun getSessions(sid: String, start: Int, end: Int): List<Any> {
        return mSessionRedisRepository.getSessions(sid, start, end)
    }

    fun deleteSession(deviceId: String) {
        mSessionRedisRepository.deleteSession(deviceId)
    }

    fun hasSession(sid: String) = mSessionRedisRepository.hasSession(sid)
}