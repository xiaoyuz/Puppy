package com.xiaoyuz.puppy.datastore.manager

import com.xiaoyuz.puppy.common.extensions.currentTimestamp
import com.xiaoyuz.puppy.datastore.manager.jpa.PostJpaRepository
import com.xiaoyuz.puppy.datastore.manager.jpa.PostVideoRelationJpaRepository
import com.xiaoyuz.puppy.datastore.manager.jpa.VideoJpaRepository
import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.datastore.model.PostVideoRelation
import com.xiaoyuz.puppy.datastore.model.Video
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DataManager {

    @Autowired
    private lateinit var mPostJpaRepository: PostJpaRepository
    @Autowired
    private lateinit var mVideoJpaRepository: VideoJpaRepository
    @Autowired
    private lateinit var mPostVideoRelationJpaRepository: PostVideoRelationJpaRepository

    fun addPost(post: Post)
            = if (mPostJpaRepository.findByLink(post.link) == null) mPostJpaRepository.save(post) else null

    fun storeVideoInfos(videos: List<Video>) = videos.mapNotNull {
        if (mVideoJpaRepository.findByLink(it.link) == null) mVideoJpaRepository.save(it) else null
    }

    fun savePostVideoRelation(post: Post, videos: List<Video>) = mPostVideoRelationJpaRepository.saveAll(
            videos.mapIndexed { index, it -> PostVideoRelation(postId = post.id, videoId = it.id,
                    orderNum = index, createTime = currentTimestamp()) }
    )
}