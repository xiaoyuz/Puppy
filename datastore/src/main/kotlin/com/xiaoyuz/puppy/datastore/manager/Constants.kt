package com.xiaoyuz.puppy.datastore.manager

private const val COMMON_PREFIX = "puppy-"

internal const val COMMON_INDEX_PREFIX = "${COMMON_PREFIX}index_name:"

internal const val SESSION_SID_KEY = "${COMMON_PREFIX}session_sid:"
internal const val SESSION_DEVICE_KEY = "${COMMON_PREFIX}session_device_id:"

internal const val POST_INDEX_KEY = "${COMMON_PREFIX}post_index"

internal const val POST_PRIMARY_KEY_STRING_PREFIX = "${COMMON_PREFIX}post_prim_id_"
internal const val POST_KEY_STRING_PREFIX = "${COMMON_PREFIX}post_post_id_"

internal const val POST_KEY_PREFIX = "'$POST_KEY_STRING_PREFIX'" // SpEL
internal const val VIDEO_LIST_KEY_PREFIX = "'${COMMON_PREFIX}video_list_post_id_'" // SpEL