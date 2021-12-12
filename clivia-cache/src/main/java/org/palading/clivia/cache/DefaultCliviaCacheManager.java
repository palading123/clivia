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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * gateway client cache default manager. When the client receives the notification, the method initCache called for
 * system cache initialization。if the system cache is initialized successfully, the system will call flushCache method
 * regularly
 * 
 * @author palading_cr
 *
 */
public class DefaultCliviaCacheManager extends AbstractCacheManage {

    private static DefaultCliviaCacheManager clivia_cache_manager = new DefaultCliviaCacheManager();

    private static Logger logger = LoggerFactory.getLogger(DefaultCliviaCacheManager.class);

    public static DefaultCliviaCacheManager getCliviaServerCache() {
        return clivia_cache_manager;
    }

    /**
     * nitialize the caches
     * 
     * @author palading_cr
     *
     */
    @Override
    public Object initCache(@NotNull ApplicationContext applicationContext) {
        try {
            setApplicationContext(applicationContext);
            logger.info("CliviaLocalCacheManager[initCache] begin");
            long startTime = System.currentTimeMillis();
            initBackListCliviaCacheFactory();
            initApiCliviaCacheFactory();
            initApiFilterCliviaCacheFactory();
            initApiInvokeCliviaCacheFactory();
            logger.info("CliviaLocalCacheManager[initCache] end ,cost time(second):[" + (System.currentTimeMillis() - startTime)
                / 1000L + "]");
        } catch (Exception e) {
            logger.error("CliviaLocalCacheManager[initCache] error", e);
            return false;
        }
        return true;
    }

    /**
     * Loop to get all cache instances and call the update method to refresh the cache
     * 
     * @author palading_cr
     */
    @Override
    public void flushCache() throws Exception {
        try {
            if (cacheLoadAtomic.compareAndSet(false, true)) {
                if (CliviaCacheInitLinkedHashMap.cliviaCacheInitLinkedHashMap.size() > 0) {
                    Map<String, Method> cliviaMethodInvokes = getCliviaInvoMethod();
                    for (Map.Entry<String, CliviaCacheLoad> cliviaCacheInitEntry : CliviaCacheInitLinkedHashMap.cliviaCacheInitLinkedHashMap
                        .entrySet()) {
                        CliviaCacheLoad cliviaCacheLoad = cliviaCacheInitEntry.getValue();
                        String methodKey = cliviaCacheLoad.getCacheKey();
                        if (StringUtils.isNotEmpty(methodKey) && cliviaMethodInvokes.containsKey(methodKey)) {
                            Object o = cliviaMethodInvokes.get(methodKey).invoke(clivia_cache_manager, null);
                            CliviaCacheFactory cliviaCacheFactory = (CliviaCacheFactory)o;
                            cliviaCacheLoad.update(cliviaCacheFactory.getCache());
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            cacheLoadAtomic.set(false);
        }
    }

}
