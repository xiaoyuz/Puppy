package com.xiaoyuz.puppy.gatherer.service

import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.gatherer.client.VimeoClient
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class VimeoService {

    @Autowired
    private lateinit var mVimeoClient: VimeoClient

    fun gatherAllPosts(category: String, maxPage: Int = 100): List<Post> {
        val posts = mutableListOf<Post>()
        for (i in 1 until maxPage) {
            val perPagePosts = mVimeoClient.getCategoryPosts(category, i, 25)
            if (perPagePosts.isNotEmpty()) {
                posts.addAll(perPagePosts)
                logger.info { "[Vimeo] Page $i is finished." }
            } else {
                break
            }
        }
        logger.info { "[Vimeo] All pages finished." }
        return posts
    }
}