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
import org.palading.clivia.config.api.CliviaClientSecurityConfigService;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.domain.ClientSecureInfo;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * @author palading_cr
 * @title CliviaClientSecurityConfigServiceImpl
 * @project clivia
 */
public class CliviaFileClientSecurityConfigServiceImpl implements CliviaClientSecurityConfigService {

    private static Logger logger = LoggerFactory.getLogger(CliviaFileClientSecurityConfigServiceImpl.class);

    @Value("${clivia.admin.config.absoluteFilePath:/opt/clivia/gateway/}")
    private String absoluteFilePath;

    @Value("${clivia.admin.file.config.type:json}")
    private String cliviaAdminFileConfigType;

    private CliviaFileConfigCommonService cliviaFileConfigCommonService;

    public CliviaFileClientSecurityConfigServiceImpl(CliviaFileConfigCommonService cliviaFileConfigCommonService) {
        this.cliviaFileConfigCommonService = cliviaFileConfigCommonService;
    }

    @Override
    public CliviaResponse getSwitch(String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return CliviaResponse.error_token_not_exists();
            }
            String state =
                cliviaFileConfigCommonService.getClientState(token,
                    getFileByDefaultName(CliviaConstants.default_clivia_gateway_clientSecurity_file));
            if (StringUtils.isEmpty(state)) {
                return CliviaResponse.error_token_check_failed();
            }
            return CliviaResponse.success(state);
        } catch (Exception e) {
            logger.error("CliviaSwitchService[getSwitch] error", e);
            return CliviaResponse.error();
        }
    }

    @Override
    public CliviaResponse getToken(ClientSecureInfo clientSecureInfo) {
        if (logger.isDebugEnabled()) {
            logger.debug("ServerService[getToken]:secureParam[" + clientSecureInfo.getClientPwd() + "],clientName["
                + clientSecureInfo.getClientName() + "]");
        }
        String token =
            cliviaFileConfigCommonService.getToken(clientSecureInfo.getClientName(), clientSecureInfo.getClientPwd(),
                getFileByDefaultName(CliviaConstants.default_clivia_gateway_clientSecurity_file));
        logger.info("ServerService[getToken]:token[" + token + "]");
        if (StringUtils.isEmpty(token)) {
            return CliviaResponse.error();
        }
        return CliviaResponse.success(token);
    }

    private File getFileByDefaultName(String defaultFileName) {
        return new File(absoluteFilePath.concat(defaultFileName).concat(".").concat(cliviaAdminFileConfigType));
    }
}
