package com.xiaoyuz.puppy.gatherer.tasks

import com.xiaoyuz.puppy.gatherer.constants.IMGUR_TAGS
import com.xiaoyuz.puppy.gatherer.service.GatherService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty(value = ["schedule.task.imgur.crawler.enable"], havingValue = "true")
open class ImgurCrawlerTask {

    @Autowired
    private lateinit var mGatherService: GatherService

    @PostConstruct
    fun init() {
        logger.info { "[ImgurCrawlerTask] Imgur crawler task" }
    }

    @Scheduled(initialDelay = 1 * 60 * 1000, fixedDelay = 10 * 60 * 1000) // 10 minutes
    fun task() {
        if (!mGatherService.getSwitch("imgur")) {
            logger.info { "[ImgurCrawlerTask] Switch is off." }
            return
        }
        logger.info { "[ImgurCrawlerTask] Crawler start." }
        val startTime = System.currentTimeMillis()
        IMGUR_TAGS.forEach {
            val list = mGatherService.gatherImgurAllPosts(it, 3)
            logger.info { "[ImgurCrawlerTask] Tag ${it.first} finished. Return ${list.size} posts." }
        }
        logger.info { "[ImgurCrawlerTask] Crawler finished, time: ${(System.currentTimeMillis() - startTime) / 1000}s" }
    }
}