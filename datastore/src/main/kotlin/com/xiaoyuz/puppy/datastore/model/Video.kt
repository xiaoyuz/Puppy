package com.xiaoyuz.puppy.datastore.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.xiaoyuz.puppy.common.extensions.currentTimestamp
import com.xiaoyuz.puppy.common.extensions.reducedUUID
import com.xiaoyuz.puppy.datastore.domains.VideoSource
import com.xiaoyuz.puppy.datastore.domains.VideoType
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "video")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Video(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int = 0,
                 @Column(name = "video_id", nullable = false) var videoId: String = "",
                 @Column(nullable = false, columnDefinition = "TEXT") var name: String = "",
                 @Column(columnDefinition = "TEXT") var description: String = "",
                 @Column(nullable = false) var link: String = "", var duration: Int = 0,
                 var width: Int = 0, var height: Int = 0,
                 @Column(name = "video_type", nullable = false) var videoType: VideoType = VideoType.DEFAULT,
                 @Column(name = "source_type", nullable = false) var sourceType: VideoSource = VideoSource.DEFAULT,
                 @Column(columnDefinition = "TEXT") var thumbnails: String = "[]",
                 @Column(name = "create_time", nullable = false) var createTime: Timestamp? = null,
                 @Column(nullable = false) var core: String = "")

fun vimeoResult2Video(json: JSONObject) = Video(videoId = reducedUUID(),
        name = json.getString("name"), link = json.optString("link"),
        description = json.optString("description"), duration = json.getInt("duration"),
        width = json.getInt("width"), height = json.getInt("height"),
        videoType = VideoType.VIDEO, sourceType = VideoSource.VIMEO).apply {
    val createTimeStr = json.getString("created_time").split("+").first()
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    createTime = Timestamp(df.parse(createTimeStr).time)
    val iFrameCode = json.getJSONObject("embed").optString("html")
    core = Jsoup.parse(iFrameCode).select("iframe").attr("src")
    thumbnails = JSONArray(parseVimeoThumbnails(json)).toString()
}

fun imgurResult2Video(json: JSONObject) = Video(videoId = reducedUUID(), name = json.optString("title"),
        description = json.optString("description"), link = json.optString("link"),
        width = json.getInt("width"), height = json.getInt("height"),
        sourceType = VideoSource.IMGUR, createTime = Timestamp(json.getLong("datetime") * 1000),
        core = json.getString("link")).apply {
    videoType = when {
        !json.getBoolean("animated") -> VideoType.IMAGE
        json.getString("type") == "image/gif" -> VideoType.GIF
        else -> VideoType.DEFAULT
    }
    thumbnails = JSONArray(parseImgurThumbnails(json)).toString()
}

fun gag9Result2Video(json: JSONObject) = Video(videoId = reducedUUID(), name = json.optString("title"),
        link = json.optString("url"), createTime = currentTimestamp(), sourceType = VideoSource.GAG9).apply {
    videoType = when {
        json.getString("type") == "Photo" -> VideoType.IMAGE
        else -> VideoType.VIDEO
    }
    val images = json.getJSONObject("images")
    thumbnails = JSONArray(parse9GagThumbnails(images)).toString()
    if (videoType == VideoType.IMAGE) {
        val image700 = images.optJSONObject("image700")
        core = image700.optString("url")
        width = image700.optInt("width")
        height = image700.optInt("height")
    } else {
        val image460sv = images.optJSONObject("image460sv") ?: images.optJSONObject("image460")
        core = image460sv.optString("url")
        if (image460sv.optInt("hasAudio") == 0) {
            videoType = VideoType.GIF
        }
        width = image460sv.optInt("width")
        height = image460sv.optInt("height")
        duration = image460sv.optInt("duration")
    }
}