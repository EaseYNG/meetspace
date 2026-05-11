package com.venus.meetspace.cache;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface CacheService {

    <T> T get(String key, Class<T> clazz);

    void set(String key, Object value, long timeout, TimeUnit unit);

    void delete(String key);

    boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 缓存穿透防护：查询缓存，未命中时通过supplier回源DB，并缓存结果
     * 若DB返回null，缓存空值（短TTL）防止穿透
     */
    <T> T getOrLoad(String key, Class<T> clazz, long ttl, TimeUnit unit, Supplier<T> supplier);

    /**
     * 缓存击穿防护：使用分布式锁，仅一个线程回源DB重建缓存，其余线程等待
     */
    <T> T getOrLoadWithLock(String lockKey, String cacheKey, Class<T> clazz,
                            long ttl, TimeUnit unit, Supplier<T> supplier);

    void addToBlacklist(String tokenHash, long ttl, TimeUnit unit);

    boolean isBlacklisted(String tokenHash);
}
