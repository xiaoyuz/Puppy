package com.xiaoyuz.puppy.datastore.manager.jpa

import com.xiaoyuz.puppy.datastore.model.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostJpaRepository : JpaRepository<Post, Int> {
    fun findByLink(link: String): Post?
}