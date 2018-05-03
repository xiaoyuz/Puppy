package com.xiaoyuz.puppy.gatherer.client

import com.gargoylesoftware.htmlunit.Page
import com.gargoylesoftware.htmlunit.UnexpectedPage
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.charset.Charset

@Component
open class CrawlerClient(@Autowired private val mWebClient: WebClient) {

    fun getDoc(url: String) = Jsoup.parse(mWebClient.getPage<HtmlPage>(url).asXml())

    fun getUnexpectedPageText(url: String) = IOUtils.toString(getPage<UnexpectedPage>(url).inputStream,
            Charset.defaultCharset())

    fun <P: Page> getPage(url: String) = mWebClient.getPage<P>(url)
}