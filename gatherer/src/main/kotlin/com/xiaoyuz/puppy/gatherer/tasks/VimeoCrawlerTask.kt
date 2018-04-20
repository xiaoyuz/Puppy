package com.xiaoyuz.puppy.gatherer.tasks

import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty(value = ["schedule.task.vimeo.crawler.enable"], havingValue = "true")
class VimeoCrawlerTask {

    @PostConstruct
    fun init() {
        logger.info { "[Schedule-Task] Vimeo crawler task" }
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000) // 5 minutes
    fun task() {
    }
}