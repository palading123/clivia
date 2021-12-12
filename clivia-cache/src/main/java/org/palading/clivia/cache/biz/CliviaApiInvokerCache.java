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
package org.palading.clivia.cache.biz;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.cache.CliviaCacheLoad;
import org.palading.clivia.cache.api.CliviaCache;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.dynamic.CliviaDynamicInvokerLoaderRunner;
import org.palading.clivia.invoker.api.CliviaInvoker;
import org.palading.clivia.support.common.constant.CliviaConstants;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author palading_cr
 * @title CliviaApiInvokerCache
 * @project clivia
 */
public class CliviaApiInvokerCache extends AbstractCliviaBizCache implements CliviaCacheLoad {

    private static CliviaApiInvokerCache cliviaApiInvokerCacheInstance = new CliviaApiInvokerCache();

    /**
     * instance
     *
     * @author palading_cr
     *
     */
    public static CliviaApiInvokerCache getApiInvokeCacheInstace() {
        return cliviaApiInvokerCacheInstance;
    }

    /**
     * load cliviaInvoker cache
     * 
     * @author palading_cr
     *
     */
    @Override
    public void load(CliviaCache cache) throws Exception {
        if (null != cache && cache.size() > 0) {
            cache.clear();
        }
        Map<String, CliviaInvoker> cliviaInvokerMap =
            applicationContext().getBeansOfType(CliviaInvoker.class).values().stream()
                .collect(Collectors.toMap(e -> e.getRpcType(), e -> e));
        cache.put(CliviaConstants.invoker_cache, cliviaInvokerMap);
    }

    /**
     * dynamic update cliviaCache of CliviaInvoker
     * 
     * @author palading_cr
     *
     */
    @Override
    public void update(CliviaCache cliviaCache) throws Exception {
        CliviaServerProperties cliviaServerProperties = cliviaServerProperties();
        if (null != cliviaServerProperties && StringUtils.isNotEmpty(cliviaServerProperties.getDynamicInvokerPath())) {
            CliviaDynamicInvokerLoaderRunner.getCliviaDynamicInvokerLoaderRunner().loadDynamicFile(cliviaCache,
                cliviaServerProperties, applicationContext());
        }
    }

    @Override
    public String getCacheKey() {
        return CliviaConstants.invoker_cache;
    }

}
