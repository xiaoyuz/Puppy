package com.xiaoyuz.puppy.content.service

import com.xiaoyuz.puppy.datastore.manager.DataManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ContentService {

    @Autowired
    private lateinit var mDataManager: DataManager

    fun getPostIds() = mDataManager.getPostIds()

    fun getPosts(ids: List<Int>) = mDataManager.getPostMapByIds(ids).let { map -> ids.mapNotNull { map[it] } }
}