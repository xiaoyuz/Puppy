package com.xiaoyuz.puppy.gatherer.controller

import com.xiaoyuz.puppy.datastore.model.Post
import com.xiaoyuz.puppy.gatherer.constants.GAG9_GROUPS
import com.xiaoyuz.puppy.gatherer.constants.IMGUR_TAGS
import com.xiaoyuz.puppy.gatherer.constants.VIMEO_CATEGORIES
import com.xiaoyuz.puppy.gatherer.service.Gag9Service
import com.xiaoyuz.puppy.gatherer.service.ImgurService
import com.xiaoyuz.puppy.gatherer.service.VimeoService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/gatherer/manager")
class ManagerController {

    @Autowired
    private lateinit var mVimeoService: VimeoService
    @Autowired
    private lateinit var mImgurService: ImgurService
    @Autowired
    private lateinit var mGag9Service: Gag9Service

    @GetMapping("/craw/vimeo")
    fun crawVimeoPages(@RequestParam(value = "max") maxPage: Int): List<Post> {
        val startTime = System.currentTimeMillis()
        val list = mVimeoService.gatherAllPosts(VIMEO_CATEGORIES[0], maxPage)
        logger.info { "Crawler finished, time: ${(System.currentTimeMillis() - startTime) / 1000}s" }
        return list
    }

    @GetMapping("/craw/imgur")
    fun crawImgurPages(@RequestParam(value = "max") maxPage: Int): List<Post> {
        val startTime = System.currentTimeMillis()
        val list = mImgurService.gatherAllPosts(IMGUR_TAGS[0], maxPage)
        logger.info { "Crawler finished, time: ${(System.currentTimeMillis() - startTime) / 1000}s" }
        return list
    }

    @GetMapping("/craw/9gag")
    fun craw9GagPages(@RequestParam(value = "max") maxPage: Int): List<Post> {
        val startTime = System.currentTimeMillis()
        val list = mGag9Service.gatherAllPosts(GAG9_GROUPS[0], maxPage)
        logger.info { "Crawler finished, time: ${(System.currentTimeMillis() - startTime) / 1000}s" }
        return list
    }
}