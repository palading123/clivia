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

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.cache.CliviaCacheLoad;
import org.palading.clivia.cache.CliviaStandandCacheFactory;
import org.palading.clivia.cache.api.CliviaCache;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaResponseEnum;
import org.palading.clivia.support.common.domain.ApiBlacklistVo;
import org.palading.clivia.support.common.domain.common.ApiBlacklist;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.HttpClientUtil;
import org.palading.clivia.support.common.util.JsonUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author palading_cr
 * @title CliviaBlackListCache
 * @project clivia
 *
 */
public class CliviaBlackListCache extends AbstractCliviaBizCache implements CliviaCacheLoad {

    private static CliviaBlackListCache cliviaBlackListCache = new CliviaBlackListCache();

    public static CliviaBlackListCache getCliviaBlackListCacheInstance() {
        return cliviaBlackListCache;
    }

    /**
     * 首次加载黑名单
     *
     * @author palading_cr
     *
     */
    @Override
    public void load(CliviaCache cache) throws Exception {
        if (!cache.isEmpty()) {
            cache.clear();
        }
        Map<String, ApiBlacklist> cliviaApiBlackList = getCliviaBlackList();
        cache.put(CliviaConstants.blackList_cache, cliviaApiBlackList);
    }

    /**
     * 获取最新全量黑名单
     *
     * @author palading_cr
     *
     *
     */
    public Map<String, ApiBlacklist> getCliviaBlackList() throws Exception {
        Map<String, ApiBlacklist> cliviaBlacklistSet = new ConcurrentHashMap<>();
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("token",
                CliviaStandandCacheFactory.getCliviaStandandCacheFactory().getString(CliviaConstants.gateway_node_token));
            String response =
                HttpClientUtil.sendHttpPost(
                    cliviaServerProperties().getCliviaAdminUrl().concat(CliviaConstants.gateway_admin_blacklist_all_method),
                    param);
            if (StringUtils.isNotEmpty(response)) {
                CliviaResponse cliviaResponse = JsonUtil.toObject(response, CliviaResponse.class);
                if (cliviaResponse.getResCode() == CliviaResponseEnum.success.getCode()
                    && Objects.nonNull(cliviaResponse.getResData())) {
                    String cliviaBlacklist = String.valueOf(cliviaResponse.getResData());
                    cliviaBlacklistSet =
                        JsonUtil.mapper.readValue(cliviaBlacklist,
                            new TypeReference<ConcurrentHashMap<String, ApiBlacklist>>() {});
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return cliviaBlacklistSet;
    }

    /**
     * 事件更新
     *
     * @author palading_cr
     *
     *
     */
    @Override
    public void update(CliviaCache cliviaCache) throws Exception {
        Map<String, ApiBlacklist> remoteIncrementalBlacklistCache = null;
        Map<String, ApiBlacklist> localStockBlackListCache =
            (Map<String, ApiBlacklist>)cliviaCache.get(CliviaConstants.blackList_cache);
        // when the localStockBlackListCache is empty ,just reload all
        if (null == localStockBlackListCache || localStockBlackListCache.size() == 0) {
            cliviaCache.put(CliviaConstants.blackList_cache, getCliviaBlackList());
        } else {
            List<ApiBlacklistVo> apiBlacklistVos = new ArrayList<>();
            for (Map.Entry<String, ApiBlacklist> apiBlacklistEntry : localStockBlackListCache.entrySet()) {
                ApiBlacklistVo apiBlacklistVo = new ApiBlacklistVo();
                apiBlacklistVo.setGroup(apiBlacklistEntry.getKey());
                apiBlacklistVo.setDesCode(apiBlacklistEntry.getValue().getDesCode());
                apiBlacklistVos.add(apiBlacklistVo);
            }
            Map<String, Object> param = new HashMap<>();
            param.put("apiBlacklistVos", JsonUtil.toJson(apiBlacklistVos));
            param.put("token",
                CliviaStandandCacheFactory.getCliviaStandandCacheFactory().getString(CliviaConstants.gateway_node_token));
            String response =
                HttpClientUtil.sendHttpPost(
                    cliviaServerProperties().getCliviaAdminUrl().concat(CliviaConstants.gateway_admin_blacklist_method), param);
            if (StringUtils.isNotEmpty(response)) {
                CliviaResponse cliviaResponse = JsonUtil.toObject(response, CliviaResponse.class);
                if (cliviaResponse.getResCode() == CliviaResponseEnum.success.getCode()
                    && Objects.nonNull(cliviaResponse.getResData())) {
                    String cliviaBlacklist = String.valueOf(cliviaResponse.getResData());
                    remoteIncrementalBlacklistCache =
                        JsonUtil.mapper.readValue(cliviaBlacklist,
                            new TypeReference<ConcurrentHashMap<String, ApiBlacklist>>() {});
                    for (Map.Entry<String, ApiBlacklist> apiBlacklistEntry : localStockBlackListCache.entrySet()) {
                        // It indicates that the cache has been deleted on the server side when
                        // remoteIncrementalBlacklistCache has same key but no desCode
                        if (remoteIncrementalBlacklistCache.containsKey(apiBlacklistEntry.getKey())
                            && null == remoteIncrementalBlacklistCache.get(apiBlacklistEntry.getKey()).getDesCode()) {
                            localStockBlackListCache.remove(apiBlacklistEntry.getKey());
                            continue;
                        }
                        // It indicates that the cache has been updated on the server side
                        if (remoteIncrementalBlacklistCache.containsKey(apiBlacklistEntry.getKey())
                            && null != remoteIncrementalBlacklistCache.get(apiBlacklistEntry.getKey()).getDesCode()
                            && !remoteIncrementalBlacklistCache.get(apiBlacklistEntry.getKey()).getDesCode()
                                .equals(apiBlacklistEntry.getValue().getDesCode())) {
                            apiBlacklistEntry.getValue().setDesCode(
                                remoteIncrementalBlacklistCache.get(apiBlacklistEntry.getKey()).getDesCode());
                            apiBlacklistEntry.getValue().setBlackList(
                                remoteIncrementalBlacklistCache.get(apiBlacklistEntry.getKey()).getBlackList());
                            continue;
                        }
                    }
                    // a new cache has been added,now the localStockBlackListCache must add it
                    for (Map.Entry<String, ApiBlacklist> apiBlacklistEntry : remoteIncrementalBlacklistCache.entrySet()) {
                        if (localStockBlackListCache.containsKey(apiBlacklistEntry.getKey())) {
                            localStockBlackListCache.put(apiBlacklistEntry.getKey(), apiBlacklistEntry.getValue());
                        }
                    }

                }
            }
        }
    }

    @Override
    public String getCacheKey() {
        return CliviaConstants.blackList_cache;
    }
}
