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

enum class PostMediaType(val value: Int) {
    DEFAULT(0),
    IMAGE(1),
    VIDEO(2),
    GIF(3),
    MIXED(4)
}

enum class TagType(val value: String) {
    DEFAULT("default"),
    NATURE("nature"),
    CUTE("cute")
}