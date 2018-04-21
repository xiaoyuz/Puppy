package com.xiaoyuz.puppy.datastore.manager.jpa

import com.xiaoyuz.puppy.datastore.model.PostVideoRelation
import org.springframework.data.jpa.repository.JpaRepository

interface PostVideoRelationJpaRepository : JpaRepository<PostVideoRelation, Int> {
    fun findByPostId(postId: Int): List<PostVideoRelation>
}