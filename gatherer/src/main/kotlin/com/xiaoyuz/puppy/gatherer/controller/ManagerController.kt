package com.xiaoyuz.puppy.gatherer.controller

import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.gatherer.constants.GAG9_GROUPS
import com.xiaoyuz.puppy.gatherer.constants.IMGUR_TAGS
import com.xiaoyuz.puppy.gatherer.constants.VIMEO_CATEGORIES
import com.xiaoyuz.puppy.gatherer.service.GatherService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/gatherer/manager")
class ManagerController {

    @Autowired
    private lateinit var mGatherService: GatherService

    @PutMapping("/switch/{name}/{action}")
    fun switch(@PathVariable("name") name: String,
               @PathVariable("action") action: String): Boolean {
        val on = when (action) {
            "on" -> true
            "off" -> false
            else -> false
        }
        mGatherService.switch(name, on)
        return on
    }

    @GetMapping("/switch/{name}")
    fun isSwitchOn(@PathVariable("name") name: String) = mGatherService.getSwitch(name)

    @GetMapping("/craw/vimeo")
    fun crawVimeoPages(@RequestParam(value = "max") maxPage: Int): List<Post> {
        val startTime = System.currentTimeMillis()
        val list = mGatherService.gatherVimeoAllPosts(VIMEO_CATEGORIES[0], maxPage)
        logger.info { "Crawler finished, time: ${(System.currentTimeMillis() - startTime) / 1000}s" }
        return list
    }

    @GetMapping("/craw/imgur")
    fun crawImgurPages(@RequestParam(value = "max") maxPage: Int): List<Post> {
        val startTime = System.currentTimeMillis()
        val list = mutableListOf<Post>()
        IMGUR_TAGS.forEach {
            list.addAll(mGatherService.gatherImgurAllPosts(it, maxPage))
        }
        logger.info { "Crawler finished, time: ${(System.currentTimeMillis() - startTime) / 1000}s" }
        return list
    }

    @GetMapping("/craw/9gag")
    fun craw9GagPages(): List<Post> {
        val startTime = System.currentTimeMillis()
        val list = mutableListOf<Post>()
        GAG9_GROUPS.forEach {
            list.addAll(mGatherService.gather9GagAllPosts(it))
        }
        logger.info { "Crawler finished, time: ${(System.currentTimeMillis() - startTime) / 1000}s" }
        return list
    }
}