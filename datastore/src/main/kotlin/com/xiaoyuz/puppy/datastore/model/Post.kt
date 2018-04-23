package com.xiaoyuz.puppy.datastore.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.xiaoyuz.puppy.common.extensions.currentTimestamp
import com.xiaoyuz.puppy.common.extensions.map
import com.xiaoyuz.puppy.common.extensions.reducedUUID
import com.xiaoyuz.puppy.datastore.domains.TagType
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
                @Column(nullable = false) var name: String = "",
                @Column(nullable = false) var link: String = "",
                @Column(name = "tag", nullable = false) var tagType: TagType = TagType.DEFAULT,
                @Column(columnDefinition = "TEXT") var thumbnails: String = "[]",
                @Column(columnDefinition = "TEXT") var description: String = "",
                @Column(name = "create_time", nullable = false) var createTime: Timestamp? = null,
                @Transient var videos: List<Video> = emptyList())

fun vimeoResult2Post(json: JSONObject, tagType: TagType): Post {
    val video = vimeoResult2Video(json)
    return Post(name = json.getString("name"), tagType = tagType, description = json.optString("description"),
            link = json.optString("link"), postId = reducedUUID()).apply {
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
    return Post(name = json.optString("title"), tagType = tagType, description = json.optString("description"),
            link = json.optString("link"), thumbnails = videos.first().thumbnails,
            postId = reducedUUID(), videos = videos, createTime = currentTimestamp())
}

fun gag9Result2Post(json: JSONObject, tagType: TagType): Post {
    val video = gag9Result2Video(json)
    return Post(name = json.optString("title"), tagType = tagType, link = json.optString("url"),
            thumbnails = video.thumbnails, postId = reducedUUID(),
            videos = listOf(video), createTime = currentTimestamp())
}