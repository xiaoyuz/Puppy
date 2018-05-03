package com.xiaoyuz.puppy.datastore.manager.jpa

import com.xiaoyuz.puppy.datastore.model.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.sql.Timestamp

interface PostJpaRepository : JpaRepository<Post, Int> {

    fun findFirstByPostId(postId: String): Post?

    fun findByIdIn(ids: List<Int>): List<Post>

    fun findByLink(link: String): Post?

    @Query(value = "SELECT post.id, post.create_time FROM post post ORDER BY post.create_time DESC, post.id DESC", nativeQuery = true)
    fun findPostIndex(): List<Array<Any>>

    @Query(value = "SELECT post.id, post.create_time FROM post post WHERE post.media_type in (2, 3) ORDER BY post.create_time DESC, post.id DESC", nativeQuery = true)
    fun findAnimatedPostIndex(): List<Array<Any>>

    @Query(value = "SELECT post.id, post.create_time FROM post post ORDER BY post.create_time DESC, post.id DESC LIMIT ?1, 1", nativeQuery = true)
    fun findPostionPostIndex(postion: Int): Array<Any>

    fun findByCreateTimeBefore(createTime: Timestamp): List<Post>
}