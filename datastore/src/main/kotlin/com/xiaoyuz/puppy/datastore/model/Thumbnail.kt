package com.xiaoyuz.puppy.datastore.model

import com.xiaoyuz.puppy.common.extensions.map
import org.json.JSONObject

data class Thumbnail(var width: Int = 0, var height: Int = 0, var url: String = "") {
    constructor(json: JSONObject) : this(width = json.optInt("width"), height = json.optInt("height"),
            url = json.optString("url"))
}

fun parseVimeoThumbnails(json: JSONObject) = json.getJSONObject("pictures").getJSONArray("sizes").map {
    it as JSONObject
    Thumbnail(width = it.getInt("width"), height = it.getInt("height"),
            url = it.getString("link"))
}

fun parseImgurThumbnails(json: JSONObject) = listOf(Thumbnail(width = json.getInt("width"),
        height = json.getInt("height"), url = json.getString("link")))

fun parse9GagThumbnails(json: JSONObject) = mutableListOf<Thumbnail>().apply {
    if (json.has("image700")) {
        val image700 = json.getJSONObject("image700")
        add(Thumbnail(width = image700.getInt("width"), height = image700.getInt("height"),
                url = image700.getString("url")))
    }
    if (json.has("image460")) {
        val image460 = json.getJSONObject("image460")
        add(Thumbnail(width = image460.getInt("width"), height = image460.getInt("height"),
                url = image460.getString("url")))
    }
}