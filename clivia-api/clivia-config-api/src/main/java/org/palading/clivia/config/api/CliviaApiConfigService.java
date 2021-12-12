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
package org.palading.clivia.config.api;

import org.palading.clivia.support.common.domain.Api;
import org.palading.clivia.support.common.response.CliviaResponse;


import java.util.Map;

/**
 * @author palading_cr
 * @title CliviaApiConfigService
 * @project clivia
 */
public interface CliviaApiConfigService {

    public CliviaResponse getAllApiList(String token);

    public CliviaResponse getApiList(String token, String json);

    public void loadApiCache();

    default void updateCache(Map<String, Api> cliviaApiCacheLastest, Map<String, Api> cliviaApiCacheCurrent) {
        for (Map.Entry<String, Api> entry : cliviaApiCacheLastest.entrySet()) {
            if (entry.getKey().equals(cliviaApiCacheCurrent.get(entry.getKey()))
                && entry.getValue().getDesCode().equals(cliviaApiCacheCurrent.get(entry.getKey()).getDesCode())) {
                continue;
            }
            if ((cliviaApiCacheCurrent.containsKey(entry.getKey()) && !entry.getValue().getDesCode()
                .equals(cliviaApiCacheCurrent.get(entry.getKey()).getDesCode()))
                || !cliviaApiCacheCurrent.containsKey(entry.getKey())) {
                cliviaApiCacheCurrent.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, Api> entry : cliviaApiCacheCurrent.entrySet()) {
            if (!cliviaApiCacheLastest.containsKey(entry.getKey())) {
                cliviaApiCacheCurrent.remove(entry.getKey());
            }
        }
    }

    public void schduledTask();

}
