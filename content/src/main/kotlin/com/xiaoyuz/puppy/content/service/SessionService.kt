package com.xiaoyuz.puppy.content.service

import com.xiaoyuz.puppy.common.extensions.reducedUUID
import com.xiaoyuz.puppy.datastore.domains.PageEntity
import com.xiaoyuz.puppy.datastore.manager.SessionManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SessionService(@Autowired private val mSessionManager: SessionManager) {

    /**
     * Check if there is a related session in redis.
     * If no, get all items from db by ifNoSession(), store into redis as session,
     * and return the paged result.
     */
    fun getPagedSession(pageId: String?, size: Int, deviceId: String?, ifNoSession: () -> List<Any>?): PageEntity {
        var (page, sid) = parsePageId(pageId)
        val entity = PageEntity()
        if (sid == null || !mSessionManager.hasSession(sid)) {
            // If there is no session, or not query for session, get all items from db.
            // Delete old session
            deviceId?.let { mSessionManager.deleteSession(it) }
            val list = ifNoSession()
            sid = reducedUUID()
            setSessions(deviceId, sid, list)
        }
        // Page
        val sessionSize = mSessionManager.getSessionSize(sid)
        if (sessionSize > 0) {
            entity.totalCount = sessionSize
            val toIndex = (page + 1) * size
            val pagedList: MutableList<Any> = mSessionManager.getSessions(sid,
                    page * size, toIndex).toMutableList()
            if (!pagedList.isEmpty()) {
                if (pagedList.size > size) {
                    pagedList.removeAt(pagedList.size - 1)
                    entity.hasMore = true
                    entity.nextId = "${page + 1}-$sid"
                }
                entity.list = pagedList
            }
        }
        return entity
    }

    private fun setSessions(deviceId: String?, sid: String, sessions: List<Any>?) {
        mSessionManager.setSessions(deviceId, sid, sessions)
    }

    private fun parsePageId(pageId: String?): Pair<Int, String?> {
        var page = 0
        var sid: String? = null
        val pageIdSplit = pageId?.split("-")
        if (pageIdSplit?.size == 2) {
            try {
                page = pageIdSplit[0].toInt()
            } catch (e: NumberFormatException) {
            }
            sid = pageIdSplit[1]
        }
        return page to sid
    }
}