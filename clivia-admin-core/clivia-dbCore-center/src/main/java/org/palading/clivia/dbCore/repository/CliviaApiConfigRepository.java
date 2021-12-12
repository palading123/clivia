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
package org.palading.clivia.dbCore.repository;


import org.palading.clivia.dbCore.domain.CliviaApiDetail;
import org.palading.clivia.dbCore.domain.TblApiInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author palading_cr
 * @title CliviaApiConfigRepository
 * @project clivia
 */

@Repository
 public interface CliviaApiConfigRepository extends JpaRepository<TblApiInfo, String> {

    @Query(value = "select new com.clivia.admin.dbConfig.domain.CliviaApiDetail(org.palading.clivia.config.api.apiId,"
        + "org.palading.clivia.config.api.groupId,org.palading.clivia.config.api.rpcType,org.palading.clivia.config.api.version,org.palading.clivia.config.api.apiType,org.palading.clivia.config.api.apiEnabled,org.palading.clivia.config.api.apiHeader,"
        + "org.palading.clivia.config.api.apiRewrite,org.palading.clivia.config.api.url,org.palading.clivia.config.api.methodType,org.palading.clivia.config.api.apiReqSize,org.palading.clivia.config.api.blacklistEnabled,org.palading.clivia.config.api.apiHystrix,org.palading.clivia.config.api.apiRequestLimit,"
        + "org.palading.clivia.config.api.apiHttpRoute,org.palading.clivia.config.api.apiNonHttproute,org.palading.clivia.config.api.apiAuth,app.appKey) from TblApiInfo org.palading.clivia.config.api left join TblAppInfo app on org.palading.clivia.config.api.appId=app.appId where org.palading.clivia.config.api.apiEnabled='0' "
        + "and app.state='0' ", nativeQuery = false)
        List<CliviaApiDetail> selectApiInfoByPageIndex(Pageable pageable);

    @Query(value = " select count(1) from tbl_api_info where api_enabled = '0' ", nativeQuery = true)
    int selectApiEnabledCount();
}
