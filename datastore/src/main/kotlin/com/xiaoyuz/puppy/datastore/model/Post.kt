package com.xiaoyuz.puppy.datastore.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.xiaoyuz.puppy.common.extensions.map
import org.json.JSONObject

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Post(var id: Int = 0, var name: String = "", var link: String = "",
                var thumbnails: String = "[]", var description: String = "",
                var videos: List<Video> = emptyList())

fun vimeoResult2Post(json: JSONObject): Post {
    val video = vimeoResult2Video(json)
    return Post(name = json.getString("name"), description = json.optString("description"),
            link = json.optString("link")).apply {
        thumbnails = video.thumbnails
        videos = listOf(video)
    }
}

fun imgurResult2Post(json: JSONObject): Post {
    val videos = json.optJSONArray("images")?.map {
        it as JSONObject
        imgurResult2Video(it)
    }?: listOf(imgurResult2Video(json))
    return Post(name = json.optString("title"), description = json.optString("description"),
            link = json.optString("link"), thumbnails = videos.first().thumbnails, videos = videos)
}

fun gag9Result2Post(json: JSONObject): Post {
    val video = gag9Result2Video(json)
    return Post(name = json.optString("title"), link = json.optString("url"),
            thumbnails = video.thumbnails, videos = listOf(video))
}