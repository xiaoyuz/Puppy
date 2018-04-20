package com.xiaoyuz.puppy.gatherer.client

import com.clickntap.vimeo.Vimeo
import com.xiaoyuz.puppy.common.extensions.map
import com.xiaoyuz.puppy.common.extensions.retry
import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.datastore.model.gag9Result2Post
import com.xiaoyuz.puppy.datastore.model.imgurResult2Post
import com.xiaoyuz.puppy.datastore.model.vimeoResult2Post
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class VimeoClient {

    @Autowired
    private lateinit var mVimeo: Vimeo

    fun getCategoryPosts(category: String, page: Int = 1, perPage: Int = 25): List<Post> {
        val func = {
            mVimeo.get("/categories/$category/videos?sort=date&direction=desc&page=$page&per_page=$perPage")
        }
        val response = retry(func, 5)
        return response?.let {
            it.json.getJSONArray("data").map { vimeoResult2Post(it as JSONObject) }
        }?: emptyList()
    }
}

@Component
class ImgurClient {

    @Value("\${third_party.imgur.app.id}")
    private lateinit var mImgurAppId: String
    @Value("\${third_party.imgur.api.host}")
    private lateinit var mImgurApiHost: String
    @Autowired
    private lateinit var mRestTemplate: RestTemplate

    fun getGalleyTagPosts(tag: String, page: Int = 0): List<Post> {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Client-ID $mImgurAppId")
        }
        val func = {
            mRestTemplate.exchange("${mImgurApiHost}gallery/t/$tag/time/day/$page",
                    HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        }
        val responseEntity = retry(func, 5)
        return responseEntity?.let { JSONObject(it.body).getJSONObject("data").getJSONArray("items").map {
            imgurResult2Post(it as JSONObject) }
        }?: emptyList()
    }
}

@Component
class Gag9Client {

    @Value("\${third_party.9gag.api.host}")
    private lateinit var m9GagApiHost: String
    @Autowired
    private lateinit var mRestTemplate: RestTemplate

    fun getGroupPosts(group: String, cursor: String? = null): Pair<List<Post>, String> {
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val url = "${m9GagApiHost}group-posts/group/$group/type/fresh?${cursor?.let { it }}"
        val func = {
            mRestTemplate.exchange(url, HttpMethod.GET,
                    HttpEntity<String>(headers), String::class.java)
        }
        val responseEntity = retry(func, 5)
        return responseEntity?.let { entity ->
            val resultJson = JSONObject(entity.body).getJSONObject("data")
            val nextCursor = resultJson.getString("nextCursor")
            resultJson.getJSONArray("posts").map {
                gag9Result2Post(it as JSONObject)
            } to nextCursor
        }?: emptyList<Post>() to ""
    }
}