package org.palading.clivia.cache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.palading.clivia.cache.api.CliviaCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author palading_cr
 * @title CliviaGuavaCache
 * @project clivia
 */
public class CliviaLocalGuavaCache<K, V> implements CliviaCache<K, V> {
    private static final int GUAVA_INIT_CAPACITY = 100;
    private static final int GUAVA_MAXMUM_SIZE = 50000;
    private static final int GUAVA_EXPIRE_AFTER_WRITE = 30;
    private static final Logger logger = LoggerFactory.getLogger(CliviaLocalGuavaCache.class);

    private Cache<K, V> cache = CacheBuilder
        .newBuilder()
        .initialCapacity(GUAVA_INIT_CAPACITY)
        .maximumSize(GUAVA_MAXMUM_SIZE)
        .expireAfterWrite(GUAVA_EXPIRE_AFTER_WRITE, TimeUnit.MINUTES)
        .concurrencyLevel(Runtime.getRuntime().availableProcessors())
        .removalListener(
            notify -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("cliviaLocalGuavaCache remove cache key:" + notify.getKey() + ",cache value:"
                        + notify.getValue() + "");
                }
            }).build();

    @Override
    public void put(K k, V v) {
        cache.put(k, v);
    }

    @Override
    public V get(K k) {
        try {
            return cache.get(k, null);
        } catch (ExecutionException e) {
            return null;
        }
    }

    @Override
    public V getDefault(K k, V v1) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public void remove(K k) {

    }

    @Override
    public Object get() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void putIfAbsent(K k, V v) {

    }
}
