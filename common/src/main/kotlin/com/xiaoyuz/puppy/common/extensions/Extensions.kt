package com.xiaoyuz.puppy.common.extensions

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder
import java.sql.Timestamp
import java.util.Random
import java.util.UUID

/**
 * Generate a UUID in 32 bit reduced length.
 */
fun reducedUUID(): String {
    val uuid = UUID.randomUUID().toString()
    return StringBuilder().append(uuid.substring(14, 18))
            .append(uuid.substring(9, 13)).append(uuid.substring(0, 8))
            .append(uuid.substring(19, 23)).append(uuid.substring(24)).toString()
}

/**
 * Get current time in timestamp.
 */
fun currentTimestamp() = Timestamp(System.currentTimeMillis())

fun millisFrom(start: Long) = (System.currentTimeMillis() - start).toDouble()

/**
 * Return random number between two numbers.
 */
fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start

fun <R> JSONArray.map(transform: (Any) -> R): List<R> {
    return (0..(length() - 1)).map { transform(get(it)) }
}

fun <K, V> JSONArray.associate(transform: (JSONObject) -> Pair<K, V>): Map<K, V> {
    return map { it as JSONObject }.associate(transform)
}

fun JSONArray.forEach(action: (Any) -> Unit) {
    (0..(length() - 1)).forEach { action(get(it)) }
}

fun JSONArray.any(predicate: (Any) -> Boolean): Boolean {
    return (0..(length() - 1)).any { predicate(get(it)) }
}

fun <R> String.jsonArrayMap(transform: (String) -> R): List<R>? {
    try {
        val jsonArray = JSONArray(this)
        return jsonArray.map { transform(it.toString()) }
    } catch (e: JSONException) {
    }
    return null
}

/**
 * Get data set from redis by ids, and ordered by ids
 * if part of data set is not in cache, get the part from db and gather them.
 */
fun <T, R : Any> gatherListFromCacheAndDataSource(ids: List<T>, getFromCache: (List<T>) -> Map<T, R>,
                                                  getMapFromDb: (List<T>) -> Map<T, R>?,
                                                  cacheOps: (Map<T, R>) -> Unit): List<R?> {
    val result = gatherMapFromCacheAndDataSource(ids, getFromCache = getFromCache, getMapFromRemote = getMapFromDb,
            cacheOps = cacheOps)
    return ids.map { result[it] }
}

fun <T, R> gatherMapFromCacheAndDataSource(ids: List<T>, getFromCache: (List<T>) -> Map<T, R>,
                                           getMapFromRemote: (List<T>) -> Map<T, R>?,
                                           cacheOps: (Map<T, R>) -> Unit): Map<T, R> {
    val distinctIds = ids.distinct()
    val caches = getFromCache(distinctIds)
    val noMatchedInfos = distinctIds.filter { caches[it] == null }
    if (noMatchedInfos.isEmpty()) return caches

    val results = caches.toMutableMap()
    val noMatchedResultMap = getMapFromRemote(noMatchedInfos)
    noMatchedResultMap?.apply { cacheOps(this) }?.let { results.putAll(it) }
    return results
}

/**
 * Get sth from cache, if not matched, get from db and cache it.
 */
fun <T, R> gatherFromCacheAndDb(key: String, id: T, getFromCache: (String) -> R?,
                                getFromDb: (T) -> R?, cacheOps: (String, R) -> Unit): R? {
    var result = getFromCache(key)
    if (result == null) {
        result = getFromDb(id)
        result?.let { cacheOps(key, it) }
    }
    return result
}

fun String.toUrlEncode(enc: String = "utf-8") = URLEncoder.encode(this, enc)

fun String.safeToInt() = try { this.toInt() } catch (e: NumberFormatException) { null }

fun <T> retry(func: () -> T, time: Int = 5): T? = if (time > 0) try {
    func()
} catch (e: Exception) {
    retry(func, time - 1)
} else null
