package org.palading.clivia.cache;

import org.palading.clivia.cache.api.CliviaCache;

/**
 * @author palading_cr
 * @title CliviaLocalGuavaCacheFactory
 * @project clivia
 */
public class CliviaLocalGuavaCacheFactory<K, V> implements CliviaCacheFactory {
    private static CliviaCache cache = new CliviaLocalGuavaCache();

    private static CliviaLocalGuavaCacheFactory cliviaLocalGuavaCacheFactory = new CliviaLocalGuavaCacheFactory();

    public static CliviaLocalGuavaCacheFactory getCliviaStandandCacheFactory() {
        return cliviaLocalGuavaCacheFactory;
    }

    @Override
    public CliviaCache getCache() {
        return cache;
    }

    @Override
    public void loadCache() {

    }

    public void put(K k, V v) {
        cache.put(k, v);
    }

    public V getCacheByKey(K k) {
        return (V)cache.get(k);
    }
}
