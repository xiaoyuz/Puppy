package com.xiaoyuz.puppy.datastore.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.xiaoyuz.puppy.common.extensions.currentTimestamp
import com.xiaoyuz.puppy.common.extensions.map
import com.xiaoyuz.puppy.common.extensions.reducedUUID
import com.xiaoyuz.puppy.datastore.domains.PostMediaType
import com.xiaoyuz.puppy.datastore.domains.TagType
import com.xiaoyuz.puppy.datastore.domains.VideoSource
import com.xiaoyuz.puppy.datastore.domains.VideoType
import org.apache.commons.text.StringEscapeUtils
import org.json.JSONObject
import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "post")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Post(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int = 0,
                @Column(name = "post_id", nullable = false, length = 32) var postId: String = "",
                @Column(nullable = false, columnDefinition = "TEXT") var name: String = "",
                @Column(nullable = false) var link: String = "",
                @Column(name = "tag", nullable = false) var tagType: TagType = TagType.DEFAULT,
                @Column(columnDefinition = "TEXT") var thumbnails: String = "[]",
                @Column(columnDefinition = "TEXT") var description: String = "",
                @Column(name = "source_type", nullable = false) var sourceType: VideoSource = VideoSource.DEFAULT,
                @Column(name = "media_type", nullable = false) var meidaType: PostMediaType = PostMediaType.DEFAULT,
                @Column(name = "create_time", nullable = false) var createTime: Timestamp? = null,
                @Transient var videos: List<Video> = emptyList())

fun vimeoResult2Post(json: JSONObject, tagType: TagType): Post {
    val video = vimeoResult2Video(json)
    return Post(name = StringEscapeUtils.unescapeHtml4(json.getString("name")), tagType = tagType,
            description = StringEscapeUtils.unescapeHtml4(json.optString("description")),
            link = json.optString("link"), postId = reducedUUID(), sourceType = video.sourceType,
            meidaType = getPostMediaType(video.videoType)).apply {
        thumbnails = video.thumbnails
        videos = listOf(video)
        createTime = currentTimestamp()
    }
}

fun imgurResult2Post(json: JSONObject, tagType: TagType): Post {
    val videos = json.optJSONArray("images")?.map {
        it as JSONObject
        imgurResult2Video(it)
    }?: listOf(imgurResult2Video(json))
    return Post(name = StringEscapeUtils.unescapeHtml4(json.optString("title")), tagType = tagType,
            description = StringEscapeUtils.unescapeHtml4(json.optString("description")),
            link = json.optString("link"), thumbnails = videos.first().thumbnails,
            postId = reducedUUID(), videos = videos, createTime = currentTimestamp(),
            sourceType = videos.firstOrNull()?.sourceType ?: VideoSource.DEFAULT,
            meidaType = if (videos.size > 1) {
                PostMediaType.MIXED
            } else {
                videos.firstOrNull()?.videoType?.let { getPostMediaType(it) } ?: PostMediaType.DEFAULT
            })
}

fun gag9Result2Post(json: JSONObject, tagType: TagType): Post {
    val video = gag9Result2Video(json)
    return Post(name = StringEscapeUtils.unescapeHtml4(json.optString("title")), tagType = tagType,
            link = json.optString("url"), thumbnails = video.thumbnails, postId = reducedUUID(),
            videos = listOf(video), createTime = currentTimestamp(),
            sourceType = video.sourceType, meidaType = getPostMediaType(video.videoType))
}

private fun getPostMediaType(videoType: VideoType) = when(videoType) {
    VideoType.GIF -> PostMediaType.GIF
    VideoType.IMAGE -> PostMediaType.IMAGE
    VideoType.VIDEO -> PostMediaType.VIDEO
    else -> PostMediaType.DEFAULT
}