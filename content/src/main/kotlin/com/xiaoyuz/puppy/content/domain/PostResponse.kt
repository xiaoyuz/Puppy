package com.xiaoyuz.puppy.content.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.xiaoyuz.puppy.datastore.model.Post

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PostResponse(var feed: FeedResponse? = null, var videos: List<VideoResponse> = emptyList()) {
    constructor(post: Post) : this(feed = FeedResponse(post), videos = post.videos.map { VideoResponse(it) })
}