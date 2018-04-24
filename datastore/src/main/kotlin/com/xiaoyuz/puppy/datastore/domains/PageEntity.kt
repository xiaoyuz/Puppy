package com.xiaoyuz.puppy.datastore.domains

import com.xiaoyuz.puppy.common.domain.ApiResponse

data class PageEntity(var list: List<Any>? = null,
                      var hasMore: Boolean = false,
                      var nextId: String = "",
                      var totalCount: Int = 0) {
    fun genPageInfo(): ApiResponse.PageInfo = ApiResponse.PageInfo(hasMore = hasMore,
            nextId = nextId, totalCount = totalCount)
}