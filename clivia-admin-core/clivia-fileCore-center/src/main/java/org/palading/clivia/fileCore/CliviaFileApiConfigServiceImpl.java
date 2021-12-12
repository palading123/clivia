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
package org.palading.clivia.fileCore;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.config.api.CliviaApiConfigService;
import org.palading.clivia.fileCore.domain.CliviaFileApiInfo;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.domain.Api;
import org.palading.clivia.support.common.domain.ApiDefaultRoute;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.domain.ApiNonHttpRoute;
import org.palading.clivia.support.common.domain.common.*;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.palading.clivia.support.common.util.Md5Util;
import org.palading.clivia.support.thread.CliviaFixScheduleThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author palading_cr
 * @title CliviaApiFileConfigServiceImpl
 * @project clivia
 */
public class CliviaFileApiConfigServiceImpl implements CliviaApiConfigService {

    private static Logger logger = LoggerFactory.getLogger(CliviaFileApiConfigServiceImpl.class);

    private static Map<String, Api> cliviaApiCache = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, Long> cliviaFileCacheLastModified = new ConcurrentHashMap<String, Long>();

    @Value("${clivia.admin.config.refreshInterval:5}")
    private String refreshInterval;

    @Value("${clivia.admin.config.absoluteFilePath:/opt/clivia/gateway/}")
    private String absoluteFilePath;

    @Value("${clivia.admin.file.config.type:json}")
    private String cliviaAdminFileConfigType;

    private CliviaFileConfigCommonService cliviaFileConfigCommonService;

    public CliviaFileApiConfigServiceImpl(CliviaFileConfigCommonService cliviaFileConfigCommonService) {
        this.cliviaFileConfigCommonService = cliviaFileConfigCommonService;
    }

    @Override
    public CliviaResponse getAllApiList(String token) {
        if (StringUtils.isEmpty(token)) {
            return CliviaResponse.error_token_not_exists();
        }
        int tokenCount =
            cliviaFileConfigCommonService.getTokenCount(
                token,
                new File(absoluteFilePath.concat(CliviaConstants.default_clivia_gateway_clientSecurity_file).concat(".")
                    .concat(cliviaAdminFileConfigType)));
        if (tokenCount != 1) {
            return CliviaResponse.error_token_check_failed();
        }
        if (cliviaApiCache.size() == 0) {
            loadApiCache();
        }
        if (cliviaApiCache.size() == 0) {
            return CliviaResponse.success(null);
        } else {
            String json = JsonUtil.toJson(cliviaApiCache);
            return CliviaResponse.success(json);
        }

    }

    @Override
    public CliviaResponse getApiList(String token, String json) {
        try {
            if (StringUtils.isEmpty(token)) {
                return CliviaResponse.error_token_not_exists();
            }
            int tokenCount =
                cliviaFileConfigCommonService.getTokenCount(token,
                    new File(absoluteFilePath.concat(CliviaConstants.default_clivia_gateway_clientSecurity_file).concat(".")
                        .concat(cliviaAdminFileConfigType)));

            if (tokenCount != 1) {
                return CliviaResponse.error_token_check_failed();
            }
            if (cliviaApiCache.size() == 0) {
                loadApiCache();
            }
            if (StringUtils.isEmpty(json)) {
                return CliviaResponse.error_param_empty();
            }
            // String apiParamJson = DesUtil.decrypt(json, FileUtil.readToString(secureFile, "UTF-8"));
            Map<String, String> param = JsonUtil.toObject(json, Map.class);
            if (null != param) {
                Map<String, Api> apiResult = new ConcurrentHashMap<>();
                for (Map.Entry<String, String> paramEntry : param.entrySet()) {
                    if (!cliviaApiCache.containsKey(paramEntry.getKey())) {
                        apiResult.put(paramEntry.getKey(), new Api());
                    }
                    if (cliviaApiCache.containsKey(paramEntry.getKey())) {
                        Api apiCache = cliviaApiCache.get(paramEntry.getKey());
                        if (!apiCache.getDesCode().equals(paramEntry.getValue())) {
                            apiResult.put(paramEntry.getKey(), cliviaApiCache.get(paramEntry.getValue()));
                        }
                    }
                }
                for (Map.Entry<String, Api> apiEntrys : cliviaApiCache.entrySet()) {
                    if (!param.containsKey(apiEntrys.getKey())) {
                        apiResult.put(apiEntrys.getKey(), apiEntrys.getValue());
                    }
                }
                if (apiResult.size() < 1) {
                    return CliviaResponse.success();
                } else {
                    String returnJson = JsonUtil.toJson(apiResult);
                    // return CliviaResponse.success(DesUtil.encrypt(returnJson, FileUtil.readToString(secureFile,
                    // "UTF-8")));

                    return CliviaResponse.success(returnJson);
                }
            }
        } catch (Exception e) {
            logger.error("CliviaFileApiConfigServiceImpl[getApiList] error ", e);
        }
        return CliviaResponse.error();
    }

    @Override
    public void loadApiCache() {
        clearCache();
        List<CliviaFileApiInfo> cliviaFileApiInfos =
            cliviaFileConfigCommonService.readCliviaCacheListFromFile(
                new File(absoluteFilePath.concat(CliviaConstants.default_clivia_gateway_api_file).concat(".")
                    .concat(cliviaAdminFileConfigType)), CliviaFileApiInfo.class);
        if (null != cliviaFileApiInfos) {
            for (CliviaFileApiInfo cliviaFileApiInfo : cliviaFileApiInfos) {
                if (!cliviaFileApiInfo.getApiEnabled().equals("0")) {
                    cliviaFileApiInfos.remove(cliviaFileApiInfo);
                }
            }
            Set<String> groupSet = new HashSet<>();
            if (null != cliviaFileApiInfos && cliviaFileApiInfos.size() > 0) {
                logger.info("CliviaFileApiConfigServiceImpl[loadAllApis] At present, the total number of APIs is ["
                    + cliviaFileApiInfos.size() + "]");
                for (CliviaFileApiInfo tblApiInfo : cliviaFileApiInfos) {
                    groupSet.add(tblApiInfo.getGroupId());
                }
                for (String groupKey : groupSet) {
                    Map<String, ApiDetail> apiDetailMap = new ConcurrentHashMap<>();
                    for (CliviaFileApiInfo cliviaFileApiInfo : cliviaFileApiInfos) {
                        if (groupKey.equals(cliviaFileApiInfo.getGroupId())) {
                            ApiDetail apiDetail = buildApiDetail(cliviaFileApiInfo);
                            apiDetailMap.put(
                                apiDetail.getGroup().concat("@").concat(apiDetail.getVersion()).concat("@")
                                    .concat(apiDetail.getUrl()), apiDetail);
                        }
                    }
                    if (apiDetailMap.size() > 0) {
                        cliviaApiCache.put(groupKey, buildApi(apiDetailMap));
                    }
                }
                logger.info("CliviaFileApiConfigServiceImpl[loadAllApis] org.palading.clivia.config.api cache load done");
            }
        }
    }

    private void clearCache() {
        if (cliviaApiCache.size() > 0) {
            cliviaApiCache.clear();
        }
    }

    /**
     * @author palading_cr
     *
     */
    private synchronized void updateApiInfoCache() {
        File cliviaApiFile =
            new File(absoluteFilePath.concat(CliviaConstants.default_clivia_gateway_api_file).concat(".")
                .concat(cliviaAdminFileConfigType));
        String cliviaApiFileKey = cliviaApiFile.getAbsolutePath() + cliviaApiFile.getName();
        if (cliviaFileCacheLastModified.get(cliviaApiFileKey) != null
            && (cliviaApiFile.lastModified() != cliviaFileCacheLastModified.get(cliviaApiFileKey))) {
            cliviaFileCacheLastModified.remove(cliviaApiFileKey);
        }
        if (null == cliviaFileCacheLastModified.get(cliviaApiFileKey)) {
            Map<String, Api> cliviaApiCacheLastest = new ConcurrentHashMap<>();
            List<CliviaFileApiInfo> cliviaFileApiInfos =
                cliviaFileConfigCommonService.readCliviaCacheListFromFile(
                    new File(absoluteFilePath.concat(CliviaConstants.default_clivia_gateway_api_file).concat(".")
                        .concat(cliviaAdminFileConfigType)), CliviaFileApiInfo.class);
            if (null != cliviaFileApiInfos) {
                for (CliviaFileApiInfo cliviaFileApiInfo : cliviaFileApiInfos) {
                    if (!cliviaFileApiInfo.getApiEnabled().equals("0")) {
                        cliviaFileApiInfos.remove(cliviaFileApiInfo);
                    }
                }
                Set<String> groupSet = new HashSet<>();
                if (null != cliviaFileApiInfos && cliviaFileApiInfos.size() > 0) {
                    logger.info("CliviaFileApiConfigServiceImpl[loadAllApis] At present, the total number of APIs is ["
                        + cliviaFileApiInfos.size() + "]");
                    for (CliviaFileApiInfo tblApiInfo : cliviaFileApiInfos) {
                        groupSet.add(tblApiInfo.getGroupId());
                    }
                    for (String groupKey : groupSet) {
                        Map<String, ApiDetail> apiDetailMap = new ConcurrentHashMap<>();
                        for (CliviaFileApiInfo tblApiInfo : cliviaFileApiInfos) {
                            if (groupKey.equals(tblApiInfo.getGroupId())) {
                                ApiDetail apiDetail = buildApiDetail(tblApiInfo);
                                apiDetailMap.put(apiDetail.getGroup().concat("@").concat(apiDetail.getVersion()).concat("@")
                                    .concat(apiDetail.getUrl()), apiDetail);
                            }
                        }
                        if (apiDetailMap.size() > 0) {
                            cliviaApiCacheLastest.put(groupKey, buildApi(apiDetailMap));
                        }
                    }
                    if (cliviaApiCacheLastest.size() == 0) {
                        cliviaApiCache.clear();
                    }
                    updateCache(cliviaApiCacheLastest, cliviaApiCache);
                    cliviaApiCacheLastest = null;
                    cliviaFileCacheLastModified.putIfAbsent(cliviaApiFileKey, cliviaApiFile.lastModified());
                    logger.info("CliviaFileApiConfigServiceImpl[loadAllApis] org.palading.clivia.config.api cache load done");
                }
            }
        }
    }

    /**
     * build org.palading.clivia.config.api
     *
     * @author palading_cr
     *
     */
    private Api buildApi(Map<String, ApiDetail> apiDetailMap) {
        String json = JsonUtil.toJson(apiDetailMap);
        String desCode = Md5Util.md5Encrypt32Upper(json);
        Api api = new Api();
        api.setDesCode(desCode);
        api.setApiDetailMap(apiDetailMap);
        return api;
    }

    /**
     * build apiDetail
     *
     * @author palading_cr
     *
     */
    private ApiDetail buildApiDetail(CliviaFileApiInfo cliviaFileApiInfo) {
        ApiDetail apiDetail = new ApiDetail();
        apiDetail.setApiId(cliviaFileApiInfo.getApiId());
        apiDetail.setApiServiceType(StringUtils.isEmpty(cliviaFileApiInfo.getApiType()) ? "1" : cliviaFileApiInfo.getApiType());
        apiDetail.setEnabled("0".equals(cliviaFileApiInfo.getApiEnabled()));
        apiDetail.setVersion(cliviaFileApiInfo.getVersion());
        apiDetail.setRpcType(StringUtils.isNotEmpty(cliviaFileApiInfo.getRpcType()) ? cliviaFileApiInfo.getRpcType() : "http");
        apiDetail.setUrl(cliviaFileApiInfo.getUrl());
        apiDetail.setMethodType(cliviaFileApiInfo.getMethodType());
        apiDetail.setBlackListEnabled("0".equals(cliviaFileApiInfo.getBlacklistEnabled()));
        apiDetail.setGroup(cliviaFileApiInfo.getGroupId());
        apiDetail.setAppKey(cliviaFileApiInfo.getAppKey());
        // apiDetail.setGroupLogic(tblApiInfo.getLogicGroup());
        ApiNonHttpRoute apiNonHttpRoute = new ApiNonHttpRoute();
        if (StringUtils.isNotEmpty(cliviaFileApiInfo.getApiNonHttproute())) {
            apiNonHttpRoute = JsonUtil.toObject(cliviaFileApiInfo.getApiNonHttproute(), ApiNonHttpRoute.class);
        }
        apiDetail.setApiNonHttpRoute(apiNonHttpRoute);
        ApiReqSizeLimit apiReqSizeLimit = new ApiReqSizeLimit();
        if (StringUtils.isNotEmpty(cliviaFileApiInfo.getApiReqSize())) {
            apiReqSizeLimit = JsonUtil.toObject(cliviaFileApiInfo.getApiReqSize(), ApiReqSizeLimit.class);
        }
        apiDetail.setApiReqSizeLimit(apiReqSizeLimit);
        ApiHeader apiHeader = new ApiHeader();
        if (StringUtils.isNotEmpty(cliviaFileApiInfo.getApiHeader())) {
            apiHeader = JsonUtil.toObject(cliviaFileApiInfo.getApiHeader(), ApiHeader.class);
        }
        apiDetail.setApiHeader(apiHeader);
        ApiRewrite apiRewrite = new ApiRewrite();
        if (StringUtils.isNotEmpty(cliviaFileApiInfo.getApiRewrite())) {
            apiRewrite = JsonUtil.toObject(cliviaFileApiInfo.getApiRewrite(), ApiRewrite.class);
        }
        apiDetail.setApiRewrite(apiRewrite);
        ApiAuth apiAuth = new ApiAuth();
        if (StringUtils.isNotEmpty(cliviaFileApiInfo.getApiAuth())) {
            apiAuth = JsonUtil.toObject(cliviaFileApiInfo.getApiAuth(), ApiAuth.class);
        }
        apiDetail.setApiAuth(apiAuth);
        ApiDefaultRoute apiDefaultRoute = new ApiDefaultRoute();
        if (StringUtils.isNotEmpty(cliviaFileApiInfo.getApiHttpRoute())) {
            apiDefaultRoute = JsonUtil.toObject(cliviaFileApiInfo.getApiHttpRoute(), ApiDefaultRoute.class);
        }
        apiDetail.setApiDefaultRoute(apiDefaultRoute);
        ApiRequestLimit apiRequestLimit = new ApiRequestLimit();
        if (StringUtils.isNotEmpty(cliviaFileApiInfo.getApiRequestLimit())) {
            apiRequestLimit = JsonUtil.toObject(cliviaFileApiInfo.getApiRequestLimit(), ApiRequestLimit.class);
        }
        apiDetail.setApiRequestLimit(apiRequestLimit);
        ApiMock apiMock = null;
        if (StringUtils.isNotEmpty(cliviaFileApiInfo.getMock())) {
            apiMock = JsonUtil.toObject(cliviaFileApiInfo.getMock(), ApiMock.class);
        }
        apiDetail.setApiMock(apiMock);
        ApiParamModify apiParamModify = null;
        if (StringUtils.isNotEmpty(cliviaFileApiInfo.getApiParamModify())) {
            apiParamModify = JsonUtil.toObject(cliviaFileApiInfo.getApiParamModify(), ApiParamModify.class);
        }
        apiDetail.setApiParamModify(apiParamModify);
        return apiDetail;
    }

    @Override
    public void schduledTask() {
        try {
            CliviaFixScheduleThreadPool.buildCliviaFixScheduleThreadPool().getFixThreadPool().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    updateApiInfoCache();
                }
            }, 1, Long.valueOf(refreshInterval), TimeUnit.MINUTES);
        } catch (Exception e) {
            throw e;
        }
    }
}
