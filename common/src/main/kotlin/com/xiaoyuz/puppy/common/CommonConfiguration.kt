package com.xiaoyuz.puppy.common

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@Configuration
@PropertySource("classpath:/common.properties")
class CommonConfiguration