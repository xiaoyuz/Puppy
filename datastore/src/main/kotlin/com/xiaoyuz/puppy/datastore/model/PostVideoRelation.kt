package com.xiaoyuz.puppy.datastore.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "post_video_relation")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class PostVideoRelation(@Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Int = 0,
                             @Column(name = "create_time") var createTime: Timestamp? = null,
                             @Column(name = "post_id", nullable = false) var postId: Int = 0,
                             @Column(name = "video_id", nullable = false) var videoId: Int = 0,
                             @Column(name = "order_num", nullable = false) var orderNum: Int = -1)