package com.xiaoyuz.puppy.gatherer.tasks

import com.xiaoyuz.puppy.gatherer.constants.GAG9_GROUPS
import com.xiaoyuz.puppy.gatherer.service.GatherService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty(value = ["schedule.task.9gag.crawler.enable"], havingValue = "true")
class Gag9CrawlerTask {

    @Autowired
    private lateinit var mGatherService: GatherService

    @PostConstruct
    fun init() {
        logger.info { "[9GagCrawlerTask] 9gag crawler task" }
    }

    @Scheduled(initialDelay = 30 * 1000, fixedDelay = 5 * 60 * 1000) // 5 minutes
    fun task() {
        if (!mGatherService.getSwitch("9gag")) {
            logger.info { "[9GagCrawlerTask] Switch is off." }
            return
        }
        logger.info { "[9GagCrawlerTask] Crawler start." }
        val startTime = System.currentTimeMillis()
        GAG9_GROUPS.forEach {
            val list = mGatherService.gather9GagAllPosts(it)
            logger.info { "[9GagCrawlerTask] Tag ${it.first} finished. Return ${list.size} posts." }
        }
        logger.info { "[9GagCrawlerTask] Crawler finished, time: ${(System.currentTimeMillis() - startTime) / 1000}s" }
    }
}