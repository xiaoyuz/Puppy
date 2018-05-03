package com.xiaoyuz.puppy.datastore.manager.redis

import com.xiaoyuz.puppy.datastore.manager.POST_PRIMARY_KEY_STRING_PREFIX
import com.xiaoyuz.puppy.datastore.manager.VIDEO_LIST_KEY_STRING_PREFIX
import com.xiaoyuz.puppy.datastore.manager.redis.conf.PuppyRedisTemplate
import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.datastore.model.Video
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

private const val EXPIRE_DAYS = 1L

@Repository
open class ModelRedisRepository {

    @Autowired
    private lateinit var mPostRedisTemplate: PuppyRedisTemplate<Post>
    @Autowired
    private lateinit var mVideoListRedisTemplate: PuppyRedisTemplate<List<Video>>

    fun getPosts(ids: List<Int>): Map<Int, Post> = multiGetMapForIds(ids) {
        mPostRedisTemplate.opsForValue().multiGet(it.map { "$POST_PRIMARY_KEY_STRING_PREFIX$it" })
    }

    fun setPost(id: Int, post: Post) = mPostRedisTemplate.opsForValue()
            .set("$POST_PRIMARY_KEY_STRING_PREFIX$id", post, EXPIRE_DAYS, TimeUnit.DAYS)

    fun deletePost(id: Int) = mPostRedisTemplate.delete("$POST_PRIMARY_KEY_STRING_PREFIX$id")

    fun deletePostVideos(postId: Int) = mVideoListRedisTemplate.delete("$VIDEO_LIST_KEY_STRING_PREFIX$postId")

    private fun <T, K> multiGetMapForIds(ids: List<T>, func: (List<T>) -> List<K>): Map<T, K>
            = ids.distinct().let { t -> t.zip(func(t)).toMap() }
}