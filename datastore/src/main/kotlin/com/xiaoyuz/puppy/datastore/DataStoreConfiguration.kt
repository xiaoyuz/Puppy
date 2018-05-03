package com.xiaoyuz.puppy.datastore

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync

@Configuration
@EntityScan("com.xiaoyuz.puppy")
@EnableJpaRepositories("com.xiaoyuz.puppy.datastore")
@PropertySource("classpath:/datastore.properties")
@EnableCaching
@EnableAsync
class DataStoreConfiguration