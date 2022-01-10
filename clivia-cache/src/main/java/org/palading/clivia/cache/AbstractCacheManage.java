/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.palading.clivia.cache;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.cache.anno.Just;
import org.palading.clivia.cache.biz.CliviaApiFilterCache;
import org.palading.clivia.cache.biz.CliviaApiInitCache;
import org.palading.clivia.cache.biz.CliviaApiInvokerCache;
import org.palading.clivia.cache.biz.CliviaBlackListCache;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.invoker.api.CliviaInvoker;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.domain.Api;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.domain.common.ApiBlacklist;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author palading_cr
 * @title AbstractCacheManager
 * @project clivia
 *
 */
public abstract class AbstractCacheManage implements CliviaCacheManage {

    protected static AtomicBoolean cacheLoadAtomic = new AtomicBoolean(false);
    protected static volatile CliviaCacheFactory<String, Map<String, Api>> cache_api;
    protected static volatile CliviaCacheFactory<String, Map<String, ApiBlacklist>> cache_blackList;
    protected static volatile CliviaCacheFactory<String, List<CliviaFilter>> cache_apiFilter;
    protected static volatile CliviaCacheFactory<String, Map<String, CliviaInvoker>> cache_apiInvoker;
    private static Map<String, Method> cliviaInvoMethod = new HashMap<>();
    // private static CliviaServerProperties cliviaServerProperties;
    // private static ApplicationContext applicationContext;
    private static CliviaStandandCacheFactory cliviaStandandCacheFactory;

    private static CliviaCacheLoad getCliviaBlackListCacheInstance() {
        return CliviaBlackListCache.getCliviaBlackListCacheInstance();
    }

    public static void setApplicationContext(ApplicationContext applicationContext1) {
        cliviaStandandCacheFactory = CliviaStandandCacheFactory.getCliviaStandandCacheFactory();
        cliviaStandandCacheFactory.put("springContext", applicationContext1);
        // cliviaServerProperties = applicationContext.getBean("cliviaServerProperties", CliviaServerProperties.class);
    }

    private static CliviaCacheLoad getCliviaApiInfoCacheInstance() {
        return CliviaApiInitCache.getCliviaApiInfoCacheInstance();
    }

    private static CliviaCacheLoad getApiFilterCacheInstace() {
        return CliviaApiFilterCache.getApiFilterCacheInstace();
    }

    private static CliviaCacheLoad getApiInvokeCacheInstace() {
        return CliviaApiInvokerCache.getApiInvokeCacheInstace();
    }

    protected CliviaCacheFactory<String, List<CliviaFilter>> getCache_ApiFilter() {
        return cache_apiFilter;
    }

    protected CliviaCacheFactory<String, Map<String, CliviaInvoker>> getCache_apiInvoker() {
        return cache_apiInvoker;
    }

    protected CliviaCacheFactory<String, Map<String, Api>> getCache_api() {
        return cache_api;
    }

    protected CliviaCacheFactory<String, Map<String, ApiBlacklist>> getCache_blackList() {
        return cache_blackList;
    }

    public List<CliviaFilter> getCacheApiFilter() {
        return Optional.ofNullable(getCache_ApiFilter()).map(cacheApiFilter -> cacheApiFilter.getCache())
            .map(cliviaCache -> cliviaCache.get(CliviaConstants.filter_cache)).orElse(null);
    }

    public Map<String, CliviaInvoker> getCacheApiInvoker() {
        return Optional.ofNullable(getCache_apiInvoker()).map(cacheApiInvoker -> cacheApiInvoker.getCache())
            .map(cliviaCache -> cliviaCache.get(CliviaConstants.invoker_cache)).orElse(null);
    }

    public Api getApiCacheByGroupKey(String groupKey) {
        return Optional.ofNullable(getCache_api()).map(apiCache -> apiCache.getCache())
            .map(cliviaCache -> cliviaCache.get(CliviaConstants.api_cache)).map(apiMap -> apiMap.get(groupKey)).orElse(null);
    }

    public ApiBlacklist getBlackListCacheByGroup(String groupKey) {
        return Optional.ofNullable(getCache_blackList()).map(blacklistCache -> blacklistCache.getCache())
            .map(cliviaCache -> cliviaCache.get(CliviaConstants.blackList_cache)).map(blacklistMap -> blacklistMap.get(groupKey))
            .orElse(null);
    }

    @Just(value = CliviaConstants.filter_cache)
    public CliviaCacheFactory<String, List<CliviaFilter>> initApiFilterCliviaCacheFactory() throws Exception {
        if (null == cache_apiFilter || cache_apiFilter.getCache().size() < 1) {
            cache_apiFilter = new CliviaCacheInitProxy().loadCache(getApiFilterCacheInstace());
        }
        return cache_apiFilter;
    }

    @Just(value = CliviaConstants.invoker_cache)
    public CliviaCacheFactory<String, Map<String, CliviaInvoker>> initApiInvokeCliviaCacheFactory() throws Exception {
        if (null == cache_apiInvoker || cache_apiInvoker.getCache().size() < 1) {
            cache_apiInvoker = new CliviaCacheInitProxy().loadCache(getApiInvokeCacheInstace());
        }
        return cache_apiInvoker;
    }

    @Just(value = CliviaConstants.api_cache)
    public CliviaCacheFactory<String, Map<String, Api>> initApiCliviaCacheFactory() throws Exception {
        if (null == cache_api || cache_api.getCache().size() < 1) {
            cache_api = new CliviaCacheInitProxy().loadCache(getCliviaApiInfoCacheInstance());
        }
        return cache_api;
    }

    @Just(value = CliviaConstants.blackList_cache)
    public CliviaCacheFactory<String, Map<String, ApiBlacklist>> initBackListCliviaCacheFactory() throws Exception {
        if (null == cache_blackList || cache_blackList.getCache().size() < 1) {
            cache_blackList = new CliviaCacheInitProxy().loadCache(getCliviaBlackListCacheInstance());
        }
        return cache_blackList;
    }

    public ApiDetail getApiDetailByCacheKey(String path, String version, String groupKey) {
        Api api = getApiCacheByGroupKey(groupKey);
        if (null != api) {
            Map<String, ApiDetail> apiDetailMap = api.getApiDetailMap();
            if (null != apiDetailMap) {
                String cacheKey = groupKey.concat("@").concat(version).concat("@").concat(path);
                if (apiDetailMap.containsKey(cacheKey)) {
                    return apiDetailMap.get(cacheKey);
                } else {
                    Set<String> apiKeySet = apiDetailMap.keySet();
                    String interfaceApiKey = null;
                    for (String stringKey : apiKeySet) {
                        if (cacheKey.startsWith(stringKey)) {
                            interfaceApiKey = stringKey;
                            break;
                        }
                    }
                    return apiDetailMap.get(interfaceApiKey);
                }
            }
        }
        return null;
    }

    protected Map<String, Method> getCliviaInvoMethod() {
        if (cliviaInvoMethod.size() > 0) {
            return cliviaInvoMethod;
        } else {
            Class clazz = DefaultCliviaCacheManager.class;
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                method.setAccessible(true);
                Just just = method.getAnnotation(Just.class);
                if (null != just && StringUtils.isNotEmpty(just.value())) {
                    cliviaInvoMethod.put(just.value(), method);
                }
            }
        }
        return cliviaInvoMethod;
    }

    static class CliviaCacheInitLinkedHashMap {

        public static Map<String, CliviaCacheLoad> cliviaCacheInitLinkedHashMap = new LinkedHashMap<>();

        public static CliviaCacheLoad storedAndReturnCliviaInitCache(CliviaCacheLoad cliviaCacheLoad) {
            if (!cliviaCacheInitLinkedHashMap.containsKey(cliviaCacheLoad.getClass().getName())) {
                cliviaCacheInitLinkedHashMap.put(cliviaCacheLoad.getClass().getName(), cliviaCacheLoad);
            }
            return cliviaCacheInitLinkedHashMap.get(cliviaCacheLoad.getClass().getName());
        }
    }
}
