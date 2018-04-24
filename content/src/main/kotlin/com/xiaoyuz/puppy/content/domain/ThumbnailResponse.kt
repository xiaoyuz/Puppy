package com.xiaoyuz.puppy.content.domain

import org.json.JSONObject

data class ThumbnailResponse(var width: Int? = null,
                             var height: Int? = null,
                             var url: String? = null) {
    constructor(json: JSONObject): this() {
        width = json.getInt("width")
        height = json.getInt("height")
        url = json.getString("url")
    }
}