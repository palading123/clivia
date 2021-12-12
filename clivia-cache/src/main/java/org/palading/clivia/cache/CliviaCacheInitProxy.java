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
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.springframework.context.ApplicationContext;

/**
 * @author palading_cr
 * @title CliviaCacheInitInvoke
 * @project clivia
 */
public class CliviaCacheInitProxy implements CliviaCacheLoadInvoker {

    private static CliviaCacheLoadInvoker cliviaCacheLoadInvoker;

    /**
     * @author palading_cr
     *
     */
    public CliviaCacheInitProxy() {
        if (CliviaConstants.clivia_gateway_cache_type_mem.equals(getCliviaCacheType())) {
            cliviaCacheLoadInvoker = new CliviaLocalCacheInvoker();
        } else {
            cliviaCacheLoadInvoker = new CliviaRemoteCacheInvoker();
        }
    }

    /**
     * @author palading_cr
     *
     */
    @Override
    public CliviaCacheFactory loadCache(CliviaCacheLoad cliviaCacheLoad) throws Exception {
        return cliviaCacheLoadInvoker.loadCache(cliviaCacheLoad);
    }

    private String getCliviaCacheType() {
        ApplicationContext applicationContext =
            (ApplicationContext) CliviaStandandCacheFactory.getCliviaStandandCacheFactory().get("springContext");
        String cacheType = applicationContext.getEnvironment().getProperty(CliviaConstants.clivia_gateway_cache_type);
        String cliviaGatewayCacheType =
            StringUtils.isEmpty(cacheType) ? CliviaConstants.clivia_gateway_cache_type_mem : cacheType;
        return cliviaGatewayCacheType;
    }

}
