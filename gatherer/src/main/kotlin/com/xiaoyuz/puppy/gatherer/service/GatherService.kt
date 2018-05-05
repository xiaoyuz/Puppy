package com.xiaoyuz.puppy.gatherer.service

import com.xiaoyuz.puppy.common.extensions.map
import com.xiaoyuz.puppy.datastore.domains.PostMediaType
import com.xiaoyuz.puppy.datastore.domains.TagType
import com.xiaoyuz.puppy.datastore.manager.DataManager
import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.datastore.model.Thumbnail
import com.xiaoyuz.puppy.gatherer.client.Gag9Client
import com.xiaoyuz.puppy.gatherer.client.ImgurClient
import com.xiaoyuz.puppy.gatherer.client.VimeoClient
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.net.URL

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
    @Value("\${post.resource.path}")
    private lateinit var mPostResourcePath: String

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
            val animatedPosts = page.first.filterNot { it.meidaType == PostMediaType.IMAGE
                    || it.meidaType == PostMediaType.MIXED || mDataManager.checkPostExists(it) }
            animatedPosts.forEach { newPost(downloadResources(it)) }
            posts.addAll(animatedPosts)
        }
        logger.info { "[9Gag] First page finished." }
        val deletedCount = removeEarliestPosts()
        logger.info { "Deleted post count: $deletedCount" }
        return posts
    }

    fun gatherImgurAllPosts(tagConfigPair: Pair<String, TagType>, maxPage: Int = 100): List<Post> {
        val posts = mutableListOf<Post>()
        for (i in 0..maxPage) {
            val perPagePosts = mImgurClient.getGalleyTagPosts(tagConfigPair, i)
            if (perPagePosts.isNotEmpty()) {
                val animatedPosts = perPagePosts.filterNot { it.meidaType == PostMediaType.IMAGE
                        || it.meidaType == PostMediaType.MIXED || mDataManager.checkPostExists(it) }
                animatedPosts.forEach { newPost(downloadResources(it)) }
                posts.addAll(animatedPosts)
                logger.info { "[Imgur] Page $i is finished. Count is ${posts.size}" }
            } else {
                break
            }
        }
        logger.info { "[Imgur] All pages finished." }
        val deletedCount = removeEarliestPosts()
        logger.info { "Deleted post count: $deletedCount" }
        return posts
    }

    fun gatherVimeoAllPosts(tagConfigPair: Pair<String, TagType>, maxPage: Int = 100): List<Post> {
        val posts = mutableListOf<Post>()
        for (i in 1 until maxPage) {
            val perPagePosts = mVimeoClient.getCategoryPosts(tagConfigPair, i, 25)
            if (perPagePosts.isNotEmpty()) {
                val animatedPosts = perPagePosts.filterNot { it.meidaType == PostMediaType.IMAGE
                        || it.meidaType == PostMediaType.MIXED || mDataManager.checkPostExists(it) }
                animatedPosts.forEach { newPost(it) }
                posts.addAll(animatedPosts)
                logger.info { "[Vimeo] Page $i is finished. Count is ${posts.size}" }
            } else {
                break
            }
        }
        logger.info { "[Vimeo] All pages finished." }
        val deletedCount = removeEarliestPosts()
        logger.info { "Deleted post count: $deletedCount" }
        return posts
    }

    fun removeEarliestPosts() = mDataManager.deleteEarliestPosts()

    fun switch(name: String, isOn: Boolean) = mDataManager.switch(name, isOn)

    fun getSwitch(name: String) = mDataManager.getSwitch(name)

    private fun guessFileTypeByUrl(url: String) = url.split(".").last()

    private fun downloadResources(post: Post): Post {
        post.videos.forEachIndexed { index, video ->
            // Download video core file.
            val videoCorePath = "/p_${post.postId}/videos/$index/video.${guessFileTypeByUrl(video.core)}"
            val videoCoreFile = File("$mPostResourcePath$videoCorePath")
            FileUtils.copyURLToFile(URL(video.core), videoCoreFile)
            video.core = videoCorePath

            // Download video's thumbnails.
            val videoThumbnails = JSONArray(video.thumbnails).map { Thumbnail(it as JSONObject) }
            videoThumbnails.forEachIndexed { thumbIndex, it ->
                val thumbnailPath = "/p_${post.postId}/videos/$index/thumbnails/$thumbIndex.${guessFileTypeByUrl(it.url)}"
                val thumbnailFile = File("$mPostResourcePath$thumbnailPath")
                FileUtils.copyURLToFile(URL(it.url), thumbnailFile)
                it.url = thumbnailPath
            }
            video.thumbnails = JSONArray(videoThumbnails).toString()
        }
        // Download post's thumbnails.
        val postThumbnails = JSONArray(post.thumbnails).map { Thumbnail(it as JSONObject) }
        postThumbnails.forEachIndexed { index, it ->
            val thumbnailPath = "/p_${post.postId}/thumbnails/$index.${guessFileTypeByUrl(it.url)}"
            val thumbnailFile = File("$mPostResourcePath$thumbnailPath")
            FileUtils.copyURLToFile(URL(it.url), thumbnailFile)
            it.url = thumbnailPath
        }
        post.thumbnails = JSONArray(postThumbnails).toString()
        logger.info { "Post resources downloading complete: ${post.postId}" }
        return post
    }
}