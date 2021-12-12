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

import org.palading.clivia.support.common.domain.common.ApiBlacklist;
import org.palading.clivia.support.common.response.CliviaResponse;

import java.util.Map;

/**
 * @author palading_cr
 * @title CliviaBlacklistConfigService
 * @project clivia
 */
public interface CliviaBlacklistConfigService {

    public void loadBlacklistCache();

    public CliviaResponse getBlacklistCache(String token, String json);

    public CliviaResponse getAllBlacklistCache(String token);

    public void schduledTask();

    default void updateCache(Map<String, ApiBlacklist> cliviaBlacklistLastest,
        Map<String, ApiBlacklist> cliviaBlacklistCacheCurrent) {
        for (Map.Entry<String, ApiBlacklist> entry : cliviaBlacklistLastest.entrySet()) {
            if (entry.getKey().equals(cliviaBlacklistCacheCurrent.get(entry.getKey()))
                && entry.getValue().getDesCode().equals(cliviaBlacklistCacheCurrent.get(entry.getKey()).getDesCode())) {
                continue;
            }
            if ((cliviaBlacklistCacheCurrent.containsKey(entry.getKey()) && !entry.getValue().getDesCode()
                .equals(cliviaBlacklistCacheCurrent.get(entry.getKey()).getDesCode()))
                || !cliviaBlacklistCacheCurrent.containsKey(entry.getKey())) {
                cliviaBlacklistCacheCurrent.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, ApiBlacklist> entry : cliviaBlacklistCacheCurrent.entrySet()) {
            if (!cliviaBlacklistLastest.containsKey(entry.getKey())) {
                cliviaBlacklistCacheCurrent.remove(entry.getKey());
            }
        }
    }
}
