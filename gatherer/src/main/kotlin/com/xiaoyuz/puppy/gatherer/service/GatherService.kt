package com.xiaoyuz.puppy.gatherer.service

import com.xiaoyuz.puppy.datastore.domains.TagType
import com.xiaoyuz.puppy.datastore.manager.DataManager
import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.gatherer.client.Gag9Client
import com.xiaoyuz.puppy.gatherer.client.ImgurClient
import com.xiaoyuz.puppy.gatherer.client.VimeoClient
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class GatherService {

    @Autowired
    private lateinit var mGag9Client: Gag9Client
    @Autowired
    private lateinit var mImgurClient: ImgurClient
    @Autowired
    private lateinit var mVimeoClient: VimeoClient
    @Autowired
    private lateinit var mDataManager: DataManager

    private fun newPost(post: Post) {
        val videoList = mDataManager.storeVideoInfos(post.videos)
        val storedPost = mDataManager.addPost(post)
        storedPost?.let { mDataManager.savePostVideoRelation(storedPost, videoList) }
    }

    /**
     * Something is wrong when paging, so only craw first page.
     */
    fun gather9GagAllPosts(tagConfigPair: Pair<String, TagType>): List<Post> {
        val posts = mutableListOf<Post>()
        val page = mGag9Client.getGroupPosts(tagConfigPair)
        if (page.first.isNotEmpty()) {
            page.first.forEach { newPost(it) }
            posts.addAll(page.first)
        }
        logger.info { "[9Gag] First page finished." }
        return posts
    }

    fun gatherImgurAllPosts(tagConfigPair: Pair<String, TagType>, maxPage: Int = 100): List<Post> {
        val posts = mutableListOf<Post>()
        for (i in 0..maxPage) {
            val perPagePosts = mImgurClient.getGalleyTagPosts(tagConfigPair, i)
            if (perPagePosts.isNotEmpty()) {
                perPagePosts.forEach { newPost(it) }
                posts.addAll(perPagePosts)
                logger.info { "[Imgur] Page $i is finished. Count is ${posts.size}" }
            } else {
                break
            }
        }
        logger.info { "[Imgur] All pages finished." }
        return posts
    }

    fun gatherVimeoAllPosts(tagConfigPair: Pair<String, TagType>, maxPage: Int = 100): List<Post> {
        val posts = mutableListOf<Post>()
        for (i in 1 until maxPage) {
            val perPagePosts = mVimeoClient.getCategoryPosts(tagConfigPair, i, 25)
            if (perPagePosts.isNotEmpty()) {
                perPagePosts.forEach { newPost(it) }
                posts.addAll(perPagePosts)
                logger.info { "[Vimeo] Page $i is finished. Count is ${posts.size}" }
            } else {
                break
            }
        }
        logger.info { "[Vimeo] All pages finished." }
        return posts
    }
}