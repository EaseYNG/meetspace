package com.venus.meetspace.cache.impl;

import com.venus.meetspace.cache.CacheService;
import com.venus.meetspace.repository.ActivityMapper;
import com.venus.meetspace.repository.UserMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@Slf4j
public class CacheServiceImpl implements CacheService {

    private static final long NULL_VALUE_TTL_SECONDS = 60;
    private static final long LOCK_WAIT_SECONDS = 5;
    private static final long LOCK_LEASE_SECONDS = 10;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;
    private final ActivityMapper activityMapper;
    private final UserMapper userMapper;
    private RBloomFilter<Long> bloomFilter;

    public CacheServiceImpl(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient, ActivityMapper activityMapper, UserMapper userMapper) {
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
        this.activityMapper = activityMapper;
        this.userMapper = userMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            return (T) value;
        } catch (Exception e) {
            log.warn("缓存反序列化失败，删除过期缓存: key={}", key, e);
            redisTemplate.delete(key);
            return null;
        }
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        long baseTTL = unit.toSeconds(timeout);
        long jitter = ThreadLocalRandom.current().nextLong(baseTTL / 10); // 防雪崩
        redisTemplate.opsForValue().set(key, value, baseTTL+jitter, TimeUnit.SECONDS);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    @PostConstruct
    public void init() {
        this.bloomFilter = redissonClient.getBloomFilter("activityIdBloom");
        bloomFilter.tryInit(100000L, 0.001); // 预计容量 + 期望误判率
        List<Long> allIds = activityMapper.selectAllIds();
        allIds.forEach(bloomFilter::add);
        log.info("布隆过滤器初始化完成，已加载 {} 条记录", allIds.size());
    }

    @Override
    public boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit));
    }

    @Override
    public <T> T getOrLoad(String key, Class<T> clazz, long ttl, TimeUnit unit, Supplier<T> supplier) {
        T cached = get(key, clazz);
        if (cached != null) {
            log.debug("缓存命中: {}", key);
            return cached;
        }

        log.debug("缓存未命中，回源加载: {}", key);
        T value = supplier.get();
        if (value != null) {
            set(key, value, ttl, unit);
        } else {
            // 缓存空值防止穿透
            set(key, "NULL_PLACEHOLDER", NULL_VALUE_TTL_SECONDS, TimeUnit.SECONDS);
        }
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrLoadWithLock(String lockKey, String cacheKey, Class<T> clazz,
                                    long ttl, TimeUnit unit, Supplier<T> supplier) {
        T cached = get(cacheKey, clazz);
        if (cached != null) {
            log.debug("缓存命中: {}", cacheKey);
            return cached;
        }

        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS)) {
                try {
                    // 双重检查
                    cached = get(cacheKey, clazz);
                    if (cached != null) {
                        return cached;
                    }

                    log.info("重建缓存: {}", cacheKey);
                    T value = supplier.get();
                    if (value != null) {
                        set(cacheKey, value, ttl, unit);
                    } else {
                        set(cacheKey, "NULL_PLACEHOLDER", NULL_VALUE_TTL_SECONDS, TimeUnit.SECONDS);
                    }
                    return value;
                } finally {
                    lock.unlock();
                }
            } else {
                log.warn("获取分布式锁超时: {}", lockKey);
                // 降级：直接回源
                return supplier.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return supplier.get();
        }
    }

    @Override
    public void addToBlacklist(String tokenHash, long ttl, TimeUnit unit) {
        String key = "token:blacklist:" + tokenHash;
        redisTemplate.opsForValue().set(key, "1", ttl, unit);
    }

    @Override
    public boolean isBlacklisted(String tokenHash) {
        String key = "token:blacklist:" + tokenHash;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
