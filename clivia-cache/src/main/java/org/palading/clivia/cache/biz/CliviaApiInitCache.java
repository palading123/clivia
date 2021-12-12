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
import org.palading.clivia.cache.CliviaStandandCacheFactory;
import org.palading.clivia.cache.api.CliviaCache;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaResponseEnum;
import org.palading.clivia.support.common.domain.Api;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.HttpClientUtil;
import org.palading.clivia.support.common.util.JsonUtil;
import org.palading.clivia.support.common.util.StrZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * api cache
 * 
 * @author palading_cr
 *
 */
public class CliviaApiInitCache extends AbstractCliviaBizCache implements CliviaCacheLoad {

    private static final String clivia_cache_file_bak_prefix = "clivia_api_cache_";

    private static final String clivia_cache_file_bak_suffix = ".txt";

    private static Logger logger = LoggerFactory.getLogger(CliviaApiInitCache.class);

    private static CliviaApiInitCache cliviaApiInitCache = new CliviaApiInitCache();

    public static CliviaApiInitCache getCliviaApiInfoCacheInstance() {
        return cliviaApiInitCache;
    }

    /**
     * load api cache
     *
     * @author palading_cr /7
     */
    @Override
    public void load(CliviaCache cache) throws Exception {
        Map<String, Api> cliviaApiInfoLatestMap = new ConcurrentHashMap<>();
        try {
            if (!cache.isEmpty()) {
                cache.clear();
            }
            // get lates cache
            cliviaApiInfoLatestMap = getCliviaApiInfoList(cliviaServerProperties());
            cache.put(CliviaConstants.api_cache, cliviaApiInfoLatestMap);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * update cache by event
     *
     * @author palading_cr
     */
    @Override
    public void update(CliviaCache cliviaCache) throws Exception {
        try {
            Map<String, Api> cliviaApiInfoCache = (Map<String, Api>)cliviaCache.get(CliviaConstants.api_cache);
            Map<String, String> groupAndDesCodes = null;
            if (null != cliviaApiInfoCache && cliviaApiInfoCache.size() > 0) {
                groupAndDesCodes = new HashMap<>();
                for (Map.Entry<String, Api> cliviaApiInfoCacheEntry : cliviaApiInfoCache.entrySet()) {
                    groupAndDesCodes.put(cliviaApiInfoCacheEntry.getKey(), cliviaApiInfoCacheEntry.getValue().getDesCode());
                }
            }
            if (null != groupAndDesCodes && groupAndDesCodes.size() > 0) {
                String response =
                    HttpClientUtil.sendHttpPostJson(
                        cliviaServerProperties().getCliviaAdminUrl().concat(CliviaConstants.gateway_admin_apiInfoList_method),
                        getApiParam(groupAndDesCodes));
                if (StringUtils.isEmpty(response)) {
                    throw new Exception("CliviaApiInitCache[update] exception occurred in remote call admin");
                }
                CliviaResponse cliviaResponse = JsonUtil.toObject(response, CliviaResponse.class);
                if (cliviaResponse.getResCode() == CliviaResponseEnum.success.getCode()
                    && Objects.nonNull(cliviaResponse.getResData())) {
                    String jsonData = String.valueOf((cliviaResponse.getResData()));
                    Map<String, Api> cliviaIncrementApiMap = JsonUtil.toComplexMapObject(jsonData, Api.class);
                    if (null != cliviaIncrementApiMap) {
                        for (Map.Entry<String, Api> cliviaIncrementApiEntry : cliviaIncrementApiMap.entrySet()) {
                            String groupKey = cliviaIncrementApiEntry.getKey();
                            Api apiGroup = cliviaIncrementApiEntry.getValue();
                            if (!cliviaApiInfoCache.containsKey(groupKey)) {
                                cliviaApiInfoCache.put(groupKey, apiGroup);
                            }
                            if (cliviaApiInfoCache.containsKey(groupKey)
                                && !cliviaApiInfoCache.get(groupKey).getDesCode().equals(apiGroup.getDesCode())) {
                                cliviaApiInfoCache.put(groupKey, apiGroup);
                            }
                            if (cliviaApiInfoCache.containsKey(groupKey) && null == apiGroup.getDesCode()) {
                                cliviaApiInfoCache.remove(groupKey);
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            throw e;
        }
    }

    /* Judge the number of files in the snapshot folder,if the number of files reaches the threshold, delete the oldest snapshot file.
       *In addition, all the data written to the file will be compressed to reduce the storage pressure.
       *The default maximum number of files is 10
      * @author palading_cr
      */
    @Deprecated
    private void cliviaApiFilesnapshot(CliviaCache cliviaCache, CliviaServerProperties cliviaServerProperties) {
        try {
            // latest apiInfo cache
            Map<String, Map<String, ApiDetail>> cliviaApiInfoLast =
                (Map<String, Map<String, ApiDetail>>)cliviaCache.get(CliviaConstants.api_cache);
            if (null != cliviaApiInfoLast && cliviaApiInfoLast.size() > 0) {
                // snapshot backup file path
                String cliviaAbsolutePath = cliviaServerProperties.getCliviaApiBakFilePath();
                int cliviaCacheFileCount = 0;
                // snapshot backup file number threshold
                int cliviaApiBakFileLimit = cliviaServerProperties.getCliviaApiBakFileLimit();
                if (StringUtils.isNotEmpty(cliviaAbsolutePath) && cliviaApiBakFileLimit > 0) {
                    // get the number of snapshot backup files that already exist
                    // and put file lastModified and name to treeMap
                    File cliviaFiles = new File(cliviaAbsolutePath);
                    if (!cliviaFiles.exists()) {
                        throw new Exception(
                            "CliviaApiInitCache[cliviaApiFilesnapshot] exception occurred when the cliviaAbsolutePath is not exsits");
                    }
                    File[] apiCacheFiles = cliviaFiles.listFiles();
                    if (Objects.nonNull(apiCacheFiles)) {
                        TreeMap<Long, String> treeMap = new TreeMap<>();
                        for (File file : apiCacheFiles) {
                            if (file.isFile() && file.getName().startsWith(clivia_cache_file_bak_prefix)
                                && file.getName().endsWith(clivia_cache_file_bak_suffix)) {
                                cliviaCacheFileCount++;
                                treeMap.put(file.lastModified(), file.getName());
                                if (cliviaCacheFileCount == cliviaApiBakFileLimit) {
                                    break;
                                }
                            }
                        }
                        // when cliviaCacheFileCount<cliviaApiBakFileLimit,write data to snapshot file
                        // when cliviaCacheFileCount>= cliviaApiBakFileLimit,delete the oldest snapshot file and
                        // write data to a new snapshot file
                        if (cliviaCacheFileCount < cliviaApiBakFileLimit) {
                            writeFile(cliviaApiInfoLast, cliviaAbsolutePath);
                        } else {
                            writeFile(treeMap, cliviaApiInfoLast, cliviaAbsolutePath);
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.error("CliviaApiInitCache[cliviaApiFilesnapshot]save snapshot file error", e);
        }
    }

    /**
     * delete the oldest files and write the latest data
     *
     * @author palading_cr
     *
     */
    private void writeFile(TreeMap<Long, String> treeMap, Map<String, Map<String, ApiDetail>> cliviaApiInfoLast,
        String absolutePath) throws Exception {
        new File(absolutePath + treeMap.firstEntry().getValue()).deleteOnExit();
        writeFile(cliviaApiInfoLast, absolutePath);
    }

    /**
     * compress the JSON cache data and write it to the snapshot file
     *
     * @author palading_cr /11
     *
     */
    private void writeFile(Map<String, Map<String, ApiDetail>> cliviaApiInfoLast, String absolutePath) {
        Writer out = null;
        try {
            String zipJson = StrZipUtil.zip(JsonUtil.toJson(cliviaApiInfoLast));
            File cliviaFile =
                new File(absolutePath.concat(clivia_cache_file_bak_prefix).concat(String.valueOf(new Date().getTime()))
                    .concat(clivia_cache_file_bak_suffix));
            out = new FileWriter(cliviaFile);
            out.write(zipJson);
            out.close();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * get lastest api from admin server
     *
     * @author palading_cr
     *
     */
    private Map<String, Api> getCliviaApiInfoList(final CliviaServerProperties cliviaServerProperties) throws Exception {
        Map<String, Api> cliviaApiInfoMap = new ConcurrentHashMap<>();
        try {
            String response =
                HttpClientUtil.sendHttpPost(
                    cliviaServerProperties.getCliviaAdminUrl().concat(CliviaConstants.gateway_admin_apiInfoList_method_all),
                    getTokenParam());
            if (StringUtils.isNotEmpty(response)) {
                CliviaResponse cliviaResponse = JsonUtil.toObject(response, CliviaResponse.class);
                if (CliviaResponseEnum.success.getCode() == cliviaResponse.getResCode()
                    && Objects.nonNull(cliviaResponse.getResData())) {
                    String jsonData = String.valueOf(cliviaResponse.getResData());
                    cliviaApiInfoMap = JsonUtil.toComplexMapObject(jsonData, Api.class);
                }
            }
        } catch (Exception e) {
            logger.error("CliviaApiInitCache[getCliviaApiInfoList] error", e);
            throw e;
        }
        return cliviaApiInfoMap;
    }

    /**
     * token param
     *
     * @author palading_cr
     *
     */
    private Map<String, Object> getTokenParam() {
        Map<String, Object> param = new HashMap<>();
        param.put("token",
            CliviaStandandCacheFactory.getCliviaStandandCacheFactory().getString(CliviaConstants.gateway_node_token));
        return param;
    }

    private String getApiParam(Map<String, String> groupAndDesCodes) {
        Map<String, Object> param = getTokenParam();
        param.put("groupAndDesCode", JsonUtil.toJson(groupAndDesCodes));
        String paramJson = JsonUtil.toJson(param);
        return paramJson;
    }

    /**
     * cache key
     *
     * @author palading_cr
     *
     */
    @Override
    public String getCacheKey() {
        return CliviaConstants.api_cache;
    }

}
