package com.xiaoyuz.puppy.datastore.manager.jpa

import com.xiaoyuz.puppy.datastore.model.Video
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VideoJpaRepository : JpaRepository<Video, Int> {

    @Query("SELECT video FROM PostVideoRelation pvr, Video video WHERE pvr.postId = ?1 AND pvr.videoId = video.id ORDER BY pvr.orderNum")
    fun getVideosByPostId(postId: Int): List<Video>

    fun findByLink(link: String): Video?
}