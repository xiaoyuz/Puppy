package com.xiaoyuz.puppy.datastore.manager.redis

import com.xiaoyuz.puppy.datastore.manager.SWITCH_KEY_PREFIX
import com.xiaoyuz.puppy.datastore.manager.redis.conf.PuppyRedisTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class SwitchRedisRepository {

    @Autowired
    private lateinit var mBooleanRedisTemplate: PuppyRedisTemplate<Boolean>

    fun setSwitch(name: String, isOn: Boolean) = mBooleanRedisTemplate.opsForValue().set(getSwitchKey(name), isOn)

    fun getSwitch(name: String) = mBooleanRedisTemplate.opsForValue().get(getSwitchKey(name))

    private fun getSwitchKey(name: String) = "$SWITCH_KEY_PREFIX$name"
}