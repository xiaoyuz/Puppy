package com.xiaoyuz.puppy.datastore.manager

import com.xiaoyuz.puppy.datastore.model.Post
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EntityHelper(@Autowired private val mDataManager: DataManager) {

    fun getPostWithVideo(post: Post) {

    }
}