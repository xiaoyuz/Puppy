package com.xiaoyuz.puppy.gatherer.tasks

import com.xiaoyuz.puppy.gatherer.constants.VIMEO_CATEGORIES
import com.xiaoyuz.puppy.gatherer.service.GatherService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty(value = ["schedule.task.vimeo.crawler.enable"], havingValue = "true")
open class VimeoCrawlerTask {

    @Autowired
    private lateinit var mGatherService: GatherService

    @PostConstruct
    fun init() {
        logger.info { "[VimeoCrawlerTask] Vimeo crawler task" }
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000) // 10 minutes
    fun task() {
        if (!mGatherService.getSwitch("vimeo")) {
            logger.info { "[VimeoCrawlerTask] Switch is off." }
            return
        }
        logger.info { "[VimeoCrawlerTask] Crawler start." }
        val startTime = System.currentTimeMillis()
        VIMEO_CATEGORIES.forEach {
            val list = mGatherService.gatherVimeoAllPosts(it, 3)
            logger.info { "[VimeoCrawlerTask] Tag ${it.first} finished. Return ${list.size} posts." }
        }
        logger.info { "[VimeoCrawlerTask] Crawler finished, time: ${(System.currentTimeMillis() - startTime) / 1000}s" }
    }
}