package com.xiaoyuz.puppy.content.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.xiaoyuz.puppy.common.extensions.map
import com.xiaoyuz.puppy.datastore.model.Post
import org.json.JSONArray
import org.json.JSONObject

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FeedResponse(var id: String, var title: String? = null, var description: String? = null,
                        var link: String? = null, var tag: String? = null,
                        var thumbnails: List<ThumbnailResponse> = emptyList(), var source: String? = null,
                        @JsonProperty("media_type") var mediaType: String? = null,
                        @JsonProperty("create_time") val createTime: Long? = null) {
    constructor(post: Post) : this(id = post.postId, title = post.name, description = post.description,
            link = post.link, tag = post.tagType.value, createTime = post.createTime?.time,
            source = post.sourceType.name, mediaType = post.meidaType.name,
            thumbnails = JSONArray(post.thumbnails).map { ThumbnailResponse(it as JSONObject) })
}