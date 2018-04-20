package com.xiaoyuz.puppy.gatherer.service

import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.gatherer.client.Gag9Client
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class Gag9Service {

    @Autowired
    private lateinit var mGag9Client: Gag9Client

    fun gatherAllPosts(group: String, maxPage: Int = 100): List<Post> {
        val posts = mutableListOf<Post>()
        var nextCursor: String? = null
        for (i in 0..maxPage) {
            val page = mGag9Client.getGroupPosts(group, nextCursor)
            if (page.first.isNotEmpty()) {
                posts.addAll(page.first)
                logger.info { "[9Gag] Page $i is finished. Count is ${posts.size}" }
            } else {
                break
            }
            nextCursor = page.second
        }
        logger.info { "[9Gag] All pages finished." }
        return posts
    }
}