package com.xiaoyuz.puppy.content.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.xiaoyuz.puppy.common.extensions.map
import com.xiaoyuz.puppy.datastore.model.Video
import org.json.JSONArray
import org.json.JSONObject

@JsonInclude(JsonInclude.Include.NON_NULL)
data class VideoResponse(var id: String, var title: String? = null, var description: String? = null,
                         var link: String? = null, var duration: Int? = null,
                         var width: Int? = null, var height: Int? = null,
                         @JsonProperty("video_type") var videoType: String? = null,
                         @JsonProperty("source_type") var sourceType: String? = null,
                         var thumbnails: List<ThumbnailResponse> = emptyList(),
                         @JsonProperty("create_time") val createTime: Long? = null,
                         var core: String) {
    constructor(video: Video) : this(id = video.videoId, title = video.name, description = video.description,
            link = video.link, duration = video.duration, width = video.width, height = video.height,
            videoType = video.videoType.name, sourceType = video.videoType.name,
            thumbnails = JSONArray(video.thumbnails).map { ThumbnailResponse(it as JSONObject) },
            createTime = video.createTime?.time, core = video.core)
}