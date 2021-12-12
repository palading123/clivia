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
import org.palading.clivia.config.api.CliviaApiConfigService;
import org.palading.clivia.dbCore.domain.CliviaApiDetail;
import org.palading.clivia.dbCore.repository.CliviaApiConfigRepository;
import org.palading.clivia.dbCore.repository.CliviaClientSecurityConfigRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author palading_cr
 * @title CliviaApiConfigService
 * @project clivia
 */
public class CliviaApiConfigServiceImpl implements CliviaApiConfigService {
    private static Logger logger = LoggerFactory.getLogger(CliviaApiConfigServiceImpl.class);

    private static Map<String, Api> cliviaApiCache = new ConcurrentHashMap<>();

    private static int eachThreadNeedsToProcess = 30000;

    private static int eachThreadPageNeedsToProcess = 3000;

    private static int startThreadOrPageIndex = 0;

    @Value("${clivia.admin.config.schduledPeriod:5}")
    private String schduledPeriod;

    @Autowired(required = false)
    private CliviaApiConfigRepository cliviaApiConfigRepository;

    @Autowired(required = false)
    private CliviaClientSecurityConfigRepository cliviaClientSecurityConfigRepository;

    /**
     * @author palading_cr
     *
     */
    public CliviaResponse getAllApiList(String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return CliviaResponse.error_token_not_exists();
            }
            int tokenCount = cliviaClientSecurityConfigRepository.selectToken(token);
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
        } catch (Exception e) {
            logger.error("CliviaApiConfigServiceImpl[getAllApiList] error", e);
        }
        return CliviaResponse.success(null);
    }

    /**
     * @author palading_cr
     *
     */
    public CliviaResponse getApiList(String token, String json) {
        try {
            if (StringUtils.isEmpty(token)) {
                return CliviaResponse.error_token_not_exists();
            }
            int tokenCount = cliviaClientSecurityConfigRepository.selectToken(token);
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
                    return CliviaResponse.success(returnJson);
                }
            }
        } catch (Exception e) {
            logger.error("CliviaApiConfigServiceImpl[getApiList] error ", e);
        }
        return CliviaResponse.error();
    }

    /**
     * @author palading_cr
     *
     */
    public void loadApiCache() {
        try {
            if (cliviaApiCache.size() > 0) {
                cliviaApiCache.clear();
            }
            int count = cliviaApiConfigRepository.selectApiEnabledCount();
            if (0 != count) {
                double threadNeedFloat = (double)count / eachThreadNeedsToProcess;
                int allThreadToNeed = (int)Math.ceil(threadNeedFloat);
                CountDownLatch countDownLatch = new CountDownLatch(allThreadToNeed);
                List<CliviaApiDetail> copyArrayList = new CopyOnWriteArrayList<>();
                for (int threadIndex = 1; threadIndex <= allThreadToNeed; threadIndex++) {
                    // int threadBeginIndex = threadIndex == 0 ? 0 : threadIndex * eachThreadNeedsToProcess;
                    int threadBeginIndex = (threadIndex - 1) * eachThreadNeedsToProcess;
                    // int threadEndIndex = (threadIndex + 1) * eachThreadNeedsToProcess;
                    int threadEndIndex = threadIndex * eachThreadNeedsToProcess;
                    // double threadPageLoopDouble = (double)(threadEndIndex - threadBeginIndex) /
                    // eachThreadPageNeedsToProcess;
                    // int threadPageLoop = 0 != (int)Math.ceil(threadPageLoopDouble) ?
                    // (int)Math.ceil(threadPageLoopDouble) : 1;
                    int threadPageLoop =
                        count <= eachThreadPageNeedsToProcess ? 0 : (threadEndIndex - threadBeginIndex)
                            / eachThreadPageNeedsToProcess;
                    List<Index> apiInfosIndexList = new ArrayList<>();
                    for (int threadPageIndex = 0; threadPageIndex <= threadPageLoop; threadPageIndex++) {
                        int pageBeginIndex =
                            threadPageIndex == 0 ? threadPageIndex : (threadPageIndex * eachThreadPageNeedsToProcess) + 1;
                        // int pageBeginIndex =
                        // threadPageIndex == 0 ? threadBeginIndex : threadBeginIndex
                        // + (threadPageIndex * eachThreadPageNeedsToProcess);
                        int pageSize = eachThreadPageNeedsToProcess;
                        apiInfosIndexList.add(new Index(pageBeginIndex, pageSize));
                    }
                    if (apiInfosIndexList.size() > 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (Index index : apiInfosIndexList) {
                                        Pageable pageable = PageRequest.of(index.getBeginIndex(), index.getPageSize());
                                        // List<TblApiInfo> threadPageTblApiInfoList =
                                        // tblApiInfoMapper.selectApiInfoByPageIndex(index.getBeginIndex(),
                                        // index.getEndIndex());

                                        List<CliviaApiDetail> threadPageTblApiInfoList =
                                            cliviaApiConfigRepository.selectApiInfoByPageIndex(pageable);
                                        if (null != threadPageTblApiInfoList && threadPageTblApiInfoList.size() > 0) {
                                            copyArrayList.addAll(threadPageTblApiInfoList);
                                        }
                                    }
                                } finally {
                                    countDownLatch.countDown();
                                }
                            }
                        }).start();
                    }
                }
                countDownLatch.await();
                Set<String> groupSet = new HashSet<>();
                if (null != copyArrayList && copyArrayList.size() > 0) {
                    logger.info("CliviaApiConfigServiceImpl[loadApiCache] At present, the total number of APIs is ["
                        + copyArrayList.size() + "]");
                    for (CliviaApiDetail apiDetail : copyArrayList) {
                        groupSet.add(apiDetail.getGroupId());
                    }
                    for (String groupKey : groupSet) {
                        Map<String, ApiDetail> apiDetailMap = new ConcurrentHashMap<>();
                        for (CliviaApiDetail cliviaApiDetail : copyArrayList) {
                            if (groupKey.equals(cliviaApiDetail.getGroupId())) {
                                ApiDetail apiDetail = buildApiDetail(cliviaApiDetail);
                                apiDetailMap.put(apiDetail.getGroup().concat("@").concat(apiDetail.getVersion()).concat("@")
                                    .concat(apiDetail.getUrl()), apiDetail);
                            }
                        }
                        if (apiDetailMap.size() > 0) {
                            cliviaApiCache.put(groupKey, buildApi(apiDetailMap));
                        }
                    }
                    logger.info("CliviaApiConfigServiceImpl[loadApiCache] org.palading.clivia.config.api cache load done");
                }
            }
        } catch (Exception e) {
            logger.error("CliviaApiConfigServiceImpl[loadApiCache] error ", e);
        }
    }

    @Override
    public void schduledTask() {
        try {
            CliviaFixScheduleThreadPool.buildCliviaFixScheduleThreadPool().getFixThreadPool().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    updateApiCache();
                }
            }, 1, Long.valueOf(schduledPeriod), TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("CliviaApiConfigServiceImpl[schduledTask] error", e);
        }
    }

    private synchronized void updateApiCache() {
        try {
            Map<String, Api> cliviaApiCacheLastest = new ConcurrentHashMap<>();
            int count = cliviaApiConfigRepository.selectApiEnabledCount();
            if (0 != count) {
                double threadNeedFloat = (double)count / eachThreadNeedsToProcess;
                int allThreadToNeed = (int)Math.ceil(threadNeedFloat);
                CountDownLatch countDownLatch = new CountDownLatch(allThreadToNeed);
                List<CliviaApiDetail> copyArrayList = new CopyOnWriteArrayList<>();
                for (int threadIndex = 1; threadIndex <= allThreadToNeed; threadIndex++) {
                    // int threadBeginIndex = threadIndex == 0 ? 0 : threadIndex * eachThreadNeedsToProcess;
                    int threadBeginIndex = (threadIndex - 1) * eachThreadNeedsToProcess;
                    // int threadEndIndex = (threadIndex + 1) * eachThreadNeedsToProcess;
                    int threadEndIndex = threadIndex * eachThreadNeedsToProcess;
                    // double threadPageLoopDouble = (double)(threadEndIndex - threadBeginIndex) /
                    // eachThreadPageNeedsToProcess;
                    // int threadPageLoop = 0 != (int)Math.ceil(threadPageLoopDouble) ?
                    // (int)Math.ceil(threadPageLoopDouble) : 1;
                    int threadPageLoop =
                        count <= eachThreadPageNeedsToProcess ? 0 : (threadEndIndex - threadBeginIndex)
                            / eachThreadPageNeedsToProcess;
                    List<Index> apiInfosIndexList = new ArrayList<>();
                    for (int threadPageIndex = 0; threadPageIndex <= threadPageLoop; threadPageIndex++) {
                        int pageBeginIndex =
                            threadPageIndex == 0 ? threadPageIndex : (threadPageIndex * eachThreadPageNeedsToProcess) + 1;
                        // int pageBeginIndex =
                        // threadPageIndex == 0 ? threadBeginIndex : threadBeginIndex
                        // + (threadPageIndex * eachThreadPageNeedsToProcess);
                        int pageSize = eachThreadPageNeedsToProcess;
                        apiInfosIndexList.add(new Index(pageBeginIndex, pageSize));
                    }
                    if (apiInfosIndexList.size() > 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (Index index : apiInfosIndexList) {
                                        Pageable pageable = PageRequest.of(index.getBeginIndex(), index.getPageSize());
                                        // List<TblApiInfo> threadPageTblApiInfoList =
                                        // tblApiInfoMapper.selectApiInfoByPageIndex(index.getBeginIndex(),
                                        // index.getEndIndex());

                                        List<CliviaApiDetail> threadPageTblApiInfoList =
                                            cliviaApiConfigRepository.selectApiInfoByPageIndex(pageable);
                                        if (null != threadPageTblApiInfoList && threadPageTblApiInfoList.size() > 0) {
                                            copyArrayList.addAll(threadPageTblApiInfoList);
                                        }
                                    }
                                } finally {
                                    countDownLatch.countDown();
                                }
                            }
                        }).start();
                    }
                }
                countDownLatch.await();
                Set<String> groupSet = new HashSet<>();
                if (null != copyArrayList && copyArrayList.size() > 0) {
                    logger.info("CliviaApiConfigServiceImpl[updateApiCache] At present, the total number of APIs is ["
                        + copyArrayList.size() + "]");
                    for (CliviaApiDetail cliviaApiDetail : copyArrayList) {
                        groupSet.add(cliviaApiDetail.getGroupId());
                    }
                    for (String groupKey : groupSet) {
                        Map<String, ApiDetail> apiDetailMap = new ConcurrentHashMap<>();
                        for (CliviaApiDetail cliviaApiDetail : copyArrayList) {
                            if (groupKey.equals(cliviaApiDetail.getGroupId())) {
                                ApiDetail apiDetail = buildApiDetail(cliviaApiDetail);
                                apiDetailMap.put(apiDetail.getGroup().concat("@").concat(apiDetail.getVersion()).concat("@")
                                    .concat(apiDetail.getUrl()), apiDetail);
                            }
                        }
                        if (apiDetailMap.size() > 0) {
                            cliviaApiCacheLastest.put(groupKey, buildApi(apiDetailMap));
                        }
                    }
                    updateCache(cliviaApiCacheLastest, cliviaApiCache);
                    logger.info("CliviaApiConfigServiceImpl[updateApiCache] org.palading.clivia.config.api cache reload done");
                }
            }
        } catch (Exception e) {
            logger.error("CliviaApiConfigServiceImpl[updateApiCache] error", e);
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
    private ApiDetail buildApiDetail(CliviaApiDetail cliviaApiDetail) {
        ApiDetail apiDetail = new ApiDetail();
        try {
            apiDetail.setApiId(cliviaApiDetail.getApiId());
            apiDetail.setApiServiceType(StringUtils.isEmpty(cliviaApiDetail.getApiType()) ? "1" : cliviaApiDetail.getApiType());
            apiDetail.setEnabled("0".equals(cliviaApiDetail.getApiEnabled()));
            apiDetail.setVersion(cliviaApiDetail.getVersion());
            apiDetail.setRpcType(StringUtils.isNotEmpty(cliviaApiDetail.getRpcType()) ? cliviaApiDetail.getRpcType() : "http");
            apiDetail.setUrl(cliviaApiDetail.getUrl());
            apiDetail.setMethodType(cliviaApiDetail.getMethodType());
            apiDetail.setBlackListEnabled("0".equals(cliviaApiDetail.getBlacklistEnabled()));
            apiDetail.setGroup(cliviaApiDetail.getGroupId());
            apiDetail.setAppKey(cliviaApiDetail.getAppKey());
            // apiDetail.setGroupLogic(tblApiInfo.getLogicGroup());
            ApiNonHttpRoute apiNonHttpRoute = new ApiNonHttpRoute();
            if (StringUtils.isNotEmpty(cliviaApiDetail.getApiNonHttproute())) {
                apiNonHttpRoute = JsonUtil.toObject(cliviaApiDetail.getApiNonHttproute(), ApiNonHttpRoute.class);
            }
            apiDetail.setApiNonHttpRoute(apiNonHttpRoute);
            ApiReqSizeLimit apiReqSizeLimit = new ApiReqSizeLimit();
            if (StringUtils.isNotEmpty(cliviaApiDetail.getApiReqSize())) {
                apiReqSizeLimit = JsonUtil.toObject(cliviaApiDetail.getApiReqSize(), ApiReqSizeLimit.class);
            }
            apiDetail.setApiReqSizeLimit(apiReqSizeLimit);
            ApiHeader apiHeader = new ApiHeader();
            if (StringUtils.isNotEmpty(cliviaApiDetail.getApiHeader())) {
                apiHeader = JsonUtil.toObject(cliviaApiDetail.getApiHeader(), ApiHeader.class);
            }
            apiDetail.setApiHeader(apiHeader);
            ApiRewrite apiRewrite = new ApiRewrite();
            if (StringUtils.isNotEmpty(cliviaApiDetail.getApiRewrite())) {
                apiRewrite = JsonUtil.toObject(cliviaApiDetail.getApiRewrite(), ApiRewrite.class);
            }
            apiDetail.setApiRewrite(apiRewrite);
            ApiAuth apiAuth = new ApiAuth();
            if (StringUtils.isNotEmpty(cliviaApiDetail.getApiAuth())) {
                apiAuth = JsonUtil.toObject(cliviaApiDetail.getApiAuth(), ApiAuth.class);
            }
            apiDetail.setApiAuth(apiAuth);
            ApiDefaultRoute apiDefaultRoute = new ApiDefaultRoute();
            if (StringUtils.isNotEmpty(cliviaApiDetail.getApiHttpRoute())) {
                apiDefaultRoute = JsonUtil.toObject(cliviaApiDetail.getApiHttpRoute(), ApiDefaultRoute.class);
            }
            apiDetail.setApiDefaultRoute(apiDefaultRoute);
            ApiRequestLimit apiRequestLimit = new ApiRequestLimit();
            if (StringUtils.isNotEmpty(cliviaApiDetail.getApiRequestLimit())) {
                apiRequestLimit = JsonUtil.toObject(cliviaApiDetail.getApiRequestLimit(), ApiRequestLimit.class);
            }
            apiDetail.setApiRequestLimit(apiRequestLimit);

            ApiMock apiMock = null;
            if (StringUtils.isNotEmpty(cliviaApiDetail.getMock())) {
                apiMock = JsonUtil.toObject(cliviaApiDetail.getMock(), ApiMock.class);
            }
            apiDetail.setApiMock(apiMock);
            ApiParamModify apiParamModify = null;
            if (StringUtils.isNotEmpty(cliviaApiDetail.getApiParamModify())) {
                apiParamModify = JsonUtil.toObject(cliviaApiDetail.getApiParamModify(), ApiParamModify.class);
            }
            apiDetail.setApiParamModify(apiParamModify);
        } catch (Exception e) {
            throw e;
        }
        return apiDetail;
    }

    class Index {
        int beginIndex;
        int pageSize;

        public Index(int beginIndex, int pageSize) {
            this.beginIndex = beginIndex;
            this.pageSize = pageSize;
        }

        public int getBeginIndex() {
            return beginIndex;
        }

        public void setBeginIndex(int beginIndex) {
            this.beginIndex = beginIndex;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }
}
