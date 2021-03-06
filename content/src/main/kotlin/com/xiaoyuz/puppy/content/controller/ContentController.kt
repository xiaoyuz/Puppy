package com.xiaoyuz.puppy.content.controller

import com.xiaoyuz.puppy.common.domain.ApiResponse
import com.xiaoyuz.puppy.content.service.ContentService
import com.xiaoyuz.puppy.content.domain.FeedResponse
import com.xiaoyuz.puppy.content.domain.PostResponse
import com.xiaoyuz.puppy.content.service.SessionService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

private const val DEFAULT_RETURN_COUNT = 10

@RestController
@RequestMapping("/content")
class ContentController {

    @Autowired
    private lateinit var mContentService: ContentService
    @Autowired
    private lateinit var mSessionService: SessionService

    @GetMapping("/feed")
    fun feed(@RequestHeader("Device-Id", required = false) deviceId: String? = null,
             @RequestParam(value = "start_id", required = false, defaultValue = "") pageId: String,
             @RequestParam(value = "count", required = false, defaultValue = "20") count: Int,
             @RequestParam(value = "qid", required = false, defaultValue = "") qid: String): ApiResponse<List<FeedResponse>> {
        logger.debug { "[Qid=$qid]feed:${pageId}_${count}_$deviceId" }
        return genPostResponsesBySessionWithPostIds(pageId, count, deviceId, { mContentService.getPostIds() })
    }

    @GetMapping("/animated")
    fun animatedFeed(@RequestHeader("Device-Id", required = false) deviceId: String? = null,
                     @RequestParam(value = "start_id", required = false, defaultValue = "") pageId: String,
                     @RequestParam(value = "count", required = false, defaultValue = "20") count: Int,
                     @RequestParam(value = "qid", required = false, defaultValue = "") qid: String): ApiResponse<List<FeedResponse>> {
        logger.debug { "[Qid=$qid]animated_feed:${pageId}_${count}_$deviceId" }
        return genPostResponsesBySessionWithPostIds(pageId, count, deviceId, { mContentService.getAnimatedPostIds() })
    }

    @GetMapping("/post/{postId}")
    fun getPost(@RequestHeader("Device-Id", required = false) deviceId: String? = null,
                @PathVariable("postId") postId: String,
                @RequestParam(value = "qid", required = false, defaultValue = "") qid: String): ApiResponse<PostResponse> {
        logger.debug { "[Qid=$qid]getPost:${postId}_$deviceId" }
        return ApiResponse.success(mContentService.getPostWithVideos(postId)?.let { PostResponse(it) })
    }

    private fun genPostResponsesBySessionWithPostIds(pageId: String,
                                                     returnCount: Int = DEFAULT_RETURN_COUNT,
                                                     deviceId: String? = null,
                                                     ifNoSession: () -> List<Any>?): ApiResponse<List<FeedResponse>> {
        val sessionEntity = mSessionService.getPagedSession(pageId, returnCount, deviceId, ifNoSession)
        val ids = sessionEntity.list?.map { it as Int }
        // Get posts by ids.
        val posts = ids?.let { mContentService.getPosts(it) } ?: emptyList()
        return ApiResponse.success(posts.map { FeedResponse(it) }, sessionEntity.genPageInfo())
    }
}