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
package org.palading.clivia.event;
import org.palading.clivia.cache.DefaultCliviaCacheManager;
import org.palading.clivia.event.api.constans.CliviaEventConstant;
import org.palading.clivia.event.api.listener.CliviaListenerCallable;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.thread.CliviaFixScheduleThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * system cache load
 * 
 * @author palading_cr
 * @title CliviaFixCacheEventListener
 * @project clivia
 *
 */
public class CliviaCompleteCacheEventListener implements CliviaListenerCallable<ApplicationContext, Boolean> {

    private static Logger logger = LoggerFactory.getLogger(CliviaCompleteCacheEventListener.class);

    /**
     * First load cache
     * 
     * @author palading_cr
     *
     */
    @Override
    public Boolean invoke(ApplicationContext applicationContext, Class<Boolean> booleanClass) throws Exception {
        return (Boolean) DefaultCliviaCacheManager.getCliviaServerCache().initCache(applicationContext);
    }

    /**
     * Refresh cache
     * 
     * @author palading_cr
     *
     */
    @Override
    public void invokeScheduler(ApplicationContext applicationContext) throws Exception {
        try {
            CliviaFixScheduleThreadPool.buildCliviaFixScheduleThreadPool().getFixThreadPool().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        DefaultCliviaCacheManager.getCliviaServerCache().flushCache();
                    } catch (Exception e) {
                        logger.error("CliviaCompleteCacheEventListener[invokeScheduler]error", e);
                    }
                }
            }, 1, getPeriod(getCliviaServerProperties(applicationContext).getCacheFlushPeriod()), TimeUnit.SECONDS);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @author palading_cr
     *
     */
    @Override
    public long getPeriod(long period) {
        return period <= CliviaEventConstant.clivia_cache_default_period ? CliviaEventConstant.clivia_cache_default_period
            : 100 > period ? CliviaEventConstant.clivia_cache_default_period : period;

    }

    /**
     * @author palading_cr
     *
     */
    @Override
    public int getOrder() {
        return CliviaConstants.clivia_cache_listener_order;
    }

}
