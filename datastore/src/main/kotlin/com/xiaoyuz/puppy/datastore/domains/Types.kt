package com.xiaoyuz.puppy.datastore.domains

enum class VideoSource(val value: Int) {
    DEFAULT(0),
    VIMEO(1),
    IMGUR(2),
    GAG9(3) // 9gag.com
}

enum class VideoType(val value: Int) {
    DEFAULT(0),
    IMAGE(1),
    VIDEO(2),
    GIF(3)
}