package com.xiaoyuz.puppy.datastore.manager

import com.xiaoyuz.puppy.datastore.manager.redis.IndexRedisRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
internal class IndexOperator {
    @Autowired
    private lateinit var mIndexRedisRepository: IndexRedisRepository

    fun addIndex(indexKey: String, index: Any, score: Double) {
        if (mIndexRedisRepository.exists(indexKey)) {
            mIndexRedisRepository.addIndex(indexKey, index.toString(), score)
        }
    }

    fun addIndexes(indexKey: String, indexes: List<Array<Any>>, scoreConvert: (Any) -> Double) {
        mIndexRedisRepository.addIndexes(indexKey, indexes, scoreConvert)
    }

    fun deleteIndex(indexKey: String, index: Any) = mIndexRedisRepository.deleteIndex(indexKey, index.toString())

    fun flushIndex(indexKey: String) = mIndexRedisRepository.flushIndexes(indexKey)

    /**
     * Get index as string list from redis, if no cached index, get from mysql.
     */
    @Suppress("UNCHECKED_CAST")
    fun getListWithIndexes(indexKey: String, fromDb: () -> List<Array<Any>>,
                           scoreConvert: (Any) -> Double): List<String> {
        val indexes = mIndexRedisRepository.getIndexes(indexKey).map { it as String }
        return processIndexesList(indexKey, indexes, fromDb, scoreConvert, { it as String })
    }

    /**
     * Get index list as int from redis, if no cached index, get from mysql.
     */
    @Suppress("UNCHECKED_CAST")
    fun getIntListWithIndexes(indexKey: String, fromDb: () -> List<Array<Any>>,
                              scoreConvert: (Any) -> Double): List<Int> {
        val indexes = mIndexRedisRepository.getIndexes(indexKey).map { it as Int }
        return processIndexesList(indexKey, indexes, fromDb, scoreConvert, { it as Int })
    }

    private fun <T> processIndexesList(indexKey: String, indexes: List<T>,
                                       fromDb: () -> List<Array<Any>>,
                                       scoreConvert: (Any) -> Double,
                                       indexConvert: (Any) -> T): List<T> {
        var result: List<T> = indexes
        if (result.isEmpty()) {
            val dbIndexes = fromDb()
            if (!dbIndexes.isEmpty()) {
                mIndexRedisRepository.addIndexes(indexKey, dbIndexes, scoreConvert)
                result = dbIndexes.map { indexConvert(it[0]) }
            }
        }
        return result
    }
}
