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


import static org.palading.clivia.cache.AbstractCacheManage.CliviaCacheInitLinkedHashMap.storedAndReturnCliviaInitCache;

/**
 * local cache invoker
 * 
 * @author palading_cr
 * @title CliviaLocalCacheInvoker
 * @project clivia
 */
public class CliviaLocalCacheInvoker implements CliviaCacheLoadInvoker {

    /**
     * load the cache and put it into cliviaLocaleCache
     * 
     * @author palading_cr
     *
     */
    public CliviaCacheFactory loadCache(CliviaCacheLoad cliviaCacheLoad) {
        return new CliviaLocalCacheFactory<>(storedAndReturnCliviaInitCache(cliviaCacheLoad), new CliviaLocaleCache<>(), 0);
    }
}
