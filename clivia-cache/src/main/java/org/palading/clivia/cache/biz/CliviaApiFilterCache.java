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
import org.palading.clivia.dynamic.CliviaDynamicFilterLoaderRunner;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.support.common.constant.CliviaConstants;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** @ClassName ApiInterceptorCache @Description TODO @Author palading_cr @Version 1.0 */
public class CliviaApiFilterCache extends AbstractCliviaBizCache implements CliviaCacheLoad {

    private static CliviaApiFilterCache cliviaApiFilterCacheInstance = new CliviaApiFilterCache();

    /**
     * instance
     *
     * @author palading_cr
     *
     */
    public static CliviaApiFilterCache getApiFilterCacheInstace() {
        return cliviaApiFilterCacheInstance;
    }

    /**
     * Initialize cache
     *
     * @author palading_cr 0
     */
    @Override
    public void load(CliviaCache cache) throws Exception {
        if (null != cache && cache.size() > 0) {
            cache.clear();
        }
        List<CliviaFilter> cliviaFilterList =
            applicationContext().getBeansOfType(CliviaFilter.class).values().stream()
                .sorted(Comparator.comparing(e -> e.getOrder())).collect(Collectors.toList());
        cache.put(CliviaConstants.filter_cache, cliviaFilterList);
    }

    /**
     * dynamic update filter
     * 
     * @author palading_cr /16
     */
    @Override
    public void update(CliviaCache cliviaCache) throws Exception {
        CliviaServerProperties cliviaServerProperties = cliviaServerProperties();
        if (null != cliviaServerProperties && StringUtils.isNotEmpty(cliviaServerProperties.getDynamicFilterPath())) {
            CliviaDynamicFilterLoaderRunner.getCliviaDynamicFilterLoaderRunner().loadDynamicFile(cliviaCache,
                cliviaServerProperties, applicationContext());
        }
    }

    @Override
    public String getCacheKey() {
        return CliviaConstants.filter_cache;
    }
}
