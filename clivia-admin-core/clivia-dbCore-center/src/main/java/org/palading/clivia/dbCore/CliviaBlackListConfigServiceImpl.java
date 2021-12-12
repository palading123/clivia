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
package org.palading.clivia.dbCore;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.config.api.CliviaBlacklistConfigService;
import org.palading.clivia.dbCore.domain.TblBlacklistInfo;
import org.palading.clivia.dbCore.repository.CliviaBlacklistConfigRepository;
import org.palading.clivia.dbCore.repository.CliviaClientSecurityConfigRepository;
import org.palading.clivia.support.common.domain.ApiBlacklistVo;
import org.palading.clivia.support.common.domain.common.ApiBlacklist;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.palading.clivia.support.common.util.Md5Util;
import org.palading.clivia.support.thread.CliviaFixScheduleThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author palading_cr
 * @title CliviaBlackListConfigService
 * @project clivia
 */
public class CliviaBlackListConfigServiceImpl implements CliviaBlacklistConfigService {

    private static final Logger logger = LoggerFactory.getLogger(CliviaBlackListConfigServiceImpl.class);

    private static Map<String, ApiBlacklist> blacklistCache = new ConcurrentHashMap<>();

    @Value("${clivia.admin.config.schduledPeriod:5}")
    private String schduledPeriod;

    @Autowired(required = false)
    private CliviaClientSecurityConfigRepository cliviaClientSecurityConfigRepository;

    @Autowired(required = false)
    private CliviaBlacklistConfigRepository cliviaBlacklistConfigRepository;

    /**
     * @author palading_cr
     */
    public void loadBlacklistCache() {
        try {
            if (blacklistCache.size() > 0) {
                blacklistCache.clear();
            }
            List<TblBlacklistInfo> blacklistInfos = cliviaBlacklistConfigRepository.selectAll();
            Set<String> groupKeySet = new HashSet<>();
            for (TblBlacklistInfo blacklistInfo : blacklistInfos) {
                groupKeySet.add(blacklistInfo.getGroupName());
            }
            for (String group : groupKeySet) {
                ApiBlacklist apiBlacklist = new ApiBlacklist();
                Set<String> blacklistSet = new HashSet<>();
                for (TblBlacklistInfo blacklistInfo : blacklistInfos) {
                    if (group.equals(blacklistInfo.getGroupName())) {
                        blacklistSet.add(blacklistInfo.getBlacklistIp());
                    }
                }
                apiBlacklist.setGroup(group);
                apiBlacklist.setBlackList(blacklistSet);
                apiBlacklist.setDesCode(Md5Util.md5Encrypt32Upper(JsonUtil.toJson(blacklistSet)));
                blacklistCache.put(group, apiBlacklist);
            }
        } catch (Exception e) {
            logger.error("CliviaBlackListConfigServiceImpl[loadBlacklistCache] error", e);
        }
    }

    /**
     * @author palading_cr
     */
    public CliviaResponse getBlacklistCache(String token, String json) {
        Map<String, ApiBlacklist> returnCache = new ConcurrentHashMap<>();
        try {
            if (StringUtils.isEmpty(token)) {
                return CliviaResponse.error_token_not_exists();
            }
            int tokenCount = cliviaClientSecurityConfigRepository.selectToken(token);
            if (tokenCount != 1) {
                return CliviaResponse.error_token_check_failed();
            }
            List<ApiBlacklistVo> apiBlacklistVos = JsonUtil.jsonStr2List(json, ApiBlacklistVo.class);
            ConcurrentHashMap<String, ApiBlacklist> apiBlacklists = new ConcurrentHashMap<>();
            apiBlacklistVos.stream().forEach(e -> {
                ApiBlacklist apiBlacklist = new ApiBlacklist();
                apiBlacklist.setGroup(e.getGroup());
                apiBlacklist.setDesCode(e.getDesCode());
                apiBlacklists.put(e.getGroup(), apiBlacklist);
            });

            for (Map.Entry<String, ApiBlacklist> entry : blacklistCache.entrySet()) {
                String groupKey = entry.getKey();
                if (apiBlacklists.containsKey(groupKey)) {
                    ApiBlacklist apiBlacklist = apiBlacklists.get(groupKey);
                    if (apiBlacklist.getDesCode().equals(entry.getValue().getDesCode())) {
                        apiBlacklists.remove(groupKey);
                        continue;
                    } else {
                        apiBlacklists.remove(groupKey);
                        returnCache.put(entry.getKey(), entry.getValue());
                    }
                } else {
                    returnCache.putIfAbsent(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<String, ApiBlacklist> entry : apiBlacklists.entrySet()) {
                String groupKey = entry.getKey();
                if (!blacklistCache.containsKey(groupKey)) {
                    returnCache.put(groupKey, new ApiBlacklist());
                }
            }
            if (returnCache.size() > 0) {
                String returnJson = JsonUtil.toJson(returnCache);
                return CliviaResponse.success(returnJson);
            }
        } catch (Exception e) {
            logger.error("CliviaBlackListConfigServiceImpl[getBlacklistCache] error", e);
        }
        return CliviaResponse.error();
    }

    /**
     * get all blacklistCache
     *
     * @author palading_cr
     */
    public CliviaResponse getAllBlacklistCache(String token) {
        try {

            if (StringUtils.isEmpty(token)) {
                return CliviaResponse.error_token_not_exists();
            }
            int tokenCount = cliviaClientSecurityConfigRepository.selectToken(token);
            if (tokenCount != 1) {
                return CliviaResponse.error_token_check_failed();
            }
            if (blacklistCache.size() == 0) {
                loadBlacklistCache();
            }
            if (blacklistCache.size() == 0) {
                return CliviaResponse.success(null);
            }
            String zipJson = JsonUtil.toJson(blacklistCache);
            return CliviaResponse.success(zipJson);
        } catch (Exception e) {
            logger.error("CliviaBlackListConfigServiceImpl[getAllBlacklistCache] error", e);
        }
        return CliviaResponse.error();
    }

    @Override
    public void schduledTask() {
        try {
            CliviaFixScheduleThreadPool.buildCliviaFixScheduleThreadPool().getFixThreadPool().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    updateBlackListCache();
                }
            }, 1, Long.valueOf(schduledPeriod), TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("CliviaBlackListConfigServiceImpl[schduledTask] error", e);
        }
    }

    private synchronized void updateBlackListCache() {
        try {
            Map<String, ApiBlacklist> cliviaBlacklistLastest = new ConcurrentHashMap<>();
            List<TblBlacklistInfo> blacklistInfos = cliviaBlacklistConfigRepository.selectAll();
            Set<String> groupKeySet = new HashSet<>();
            for (TblBlacklistInfo blacklistInfo : blacklistInfos) {
                groupKeySet.add(blacklistInfo.getGroupName());
            }
            for (String group : groupKeySet) {
                ApiBlacklist apiBlacklist = new ApiBlacklist();
                Set<String> blacklistSet = new HashSet<>();
                for (TblBlacklistInfo blacklistInfo : blacklistInfos) {
                    if (group.equals(blacklistInfo.getGroupName())) {
                        blacklistSet.add(blacklistInfo.getBlacklistIp());
                    }
                }
                apiBlacklist.setGroup(group);
                apiBlacklist.setBlackList(blacklistSet);
                apiBlacklist.setDesCode(Md5Util.md5Encrypt32Upper(JsonUtil.toJson(blacklistSet)));
                cliviaBlacklistLastest.put(group, apiBlacklist);
            }
            if (cliviaBlacklistLastest.size() > 0) {
                updateCache(cliviaBlacklistLastest, blacklistCache);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
