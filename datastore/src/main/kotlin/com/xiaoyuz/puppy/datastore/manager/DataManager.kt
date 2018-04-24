package com.xiaoyuz.puppy.datastore.manager

import com.xiaoyuz.puppy.common.extensions.currentTimestamp
import com.xiaoyuz.puppy.common.extensions.gatherMapFromCacheAndDataSource
import com.xiaoyuz.puppy.datastore.manager.jpa.PostJpaRepository
import com.xiaoyuz.puppy.datastore.manager.jpa.PostVideoRelationJpaRepository
import com.xiaoyuz.puppy.datastore.manager.jpa.VideoJpaRepository
import com.xiaoyuz.puppy.datastore.manager.redis.ModelRedisRepository
import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.datastore.model.PostVideoRelation
import com.xiaoyuz.puppy.datastore.model.Video
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class DataManager {

    @Autowired
    private lateinit var mPostJpaRepository: PostJpaRepository
    @Autowired
    private lateinit var mVideoJpaRepository: VideoJpaRepository
    @Autowired
    private lateinit var mPostVideoRelationJpaRepository: PostVideoRelationJpaRepository
    @Autowired
    private lateinit var mIndexOperator: IndexOperator
    @Autowired
    private lateinit var mModelRedisRepository: ModelRedisRepository

    fun addPost(post: Post)
            = if (mPostJpaRepository.findByLink(post.link) == null) mPostJpaRepository.save(post) else null

    fun storeVideoInfos(videos: List<Video>) = videos.mapNotNull {
        if (mVideoJpaRepository.findByLink(it.link) == null) mVideoJpaRepository.save(it) else null
    }

    fun savePostVideoRelation(post: Post, videos: List<Video>) = mPostVideoRelationJpaRepository.saveAll(
            videos.mapIndexed { index, it -> PostVideoRelation(postId = post.id, videoId = it.id,
                    orderNum = index, createTime = currentTimestamp()) }
    )

    fun getPostIds() = mIndexOperator.getIntListWithIndexes(POST_INDEX_KEY,
            { mPostJpaRepository.findPostIndex() }, { (it as Timestamp).time.toDouble() })

    fun getPostMapByIds(ids: List<Int>) = gatherMapFromCacheAndDataSource(ids,
            { mModelRedisRepository.getPosts(it) },
            { mPostJpaRepository.findByIdIn(it).associateBy { it.id } },
            { it.forEach { mModelRedisRepository.setPost(it.key, it.value) } })
}