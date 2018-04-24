package com.xiaoyuz.puppy.common.domain

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(var result: Result = Result(),
                          @JsonProperty("page_info") var pageInfo: PageInfo = PageInfo(),
                          var content: T? = null,
                          @JsonProperty("request_id") var requestId: String? = null) {

    data class Result(var message: String = "success", var code: Int = 0)

    data class PageInfo(@JsonProperty("has_more") var hasMore: Boolean = false,
                        @JsonProperty("next_id") var nextId: String = "",
                        @JsonProperty("total_count") var totalCount: Int = 0)

    constructor(content: T?, code: Int, message: String): this(content = content) {
        result = Result(message, code)
    }

    companion object {
        fun success(): ApiResponse<Unit> = ApiResponse()

        fun failed(code: Int, msg: String) = ApiResponse<Unit>(null, code, msg)

        fun <T> success(content: T?) = ApiResponse(content = content)

        fun <T> success(content: T?, pageInfo: PageInfo) = ApiResponse(content = content, pageInfo = pageInfo)

        fun <T> failed(content: T?, code: Int, message: String) = ApiResponse(content, code, message)
    }
}