package com.xiaoyuz.puppy.gatherer.service

import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.gatherer.client.ImgurClient
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class ImgurService {

    @Autowired
    private lateinit var mImgurClient: ImgurClient

    fun gatherAllPosts(tag: String, maxPage: Int = 100): List<Post> {
        val posts = mutableListOf<Post>()
        for (i in 0..maxPage) {
            val perPagePosts = mImgurClient.getGalleyTagPosts(tag, i)
            if (perPagePosts.isNotEmpty()) {
                posts.addAll(perPagePosts)
                logger.info { "[Imgur] Page $i is finished. Count is ${posts.size}" }
            } else {
                break
            }
        }
        logger.info { "[Imgur] All pages finished." }
        return posts
    }
}