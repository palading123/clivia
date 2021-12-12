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
import org.palading.clivia.config.api.CliviaClientSecurityConfigService;
import org.palading.clivia.dbCore.repository.CliviaClientSecurityConfigRepository;
import org.palading.clivia.support.common.domain.ClientSecureInfo;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author palading_cr
 * @title CliviaClientSwitcherConfigService
 * @project clivia
 */
public class CliviaClientSecurityConfigServiceImpl implements CliviaClientSecurityConfigService {

    private static Logger logger = LoggerFactory.getLogger(CliviaClientSecurityConfigServiceImpl.class);

    @Autowired(required = false)
    private CliviaClientSecurityConfigRepository cliviaClientSecurityConfigRepository;

    /**
     * getSwitch
     *
     * @author palading_cr
     *
     */
    public CliviaResponse getSwitch(String token) {
        try {
            if (StringUtils.isEmpty(token)) {
                return CliviaResponse.error_token_not_exists();
            }
            String state = cliviaClientSecurityConfigRepository.selectClientState(token);
            if (StringUtils.isEmpty(state)) {
                return CliviaResponse.error_token_check_failed();
            }
            return CliviaResponse.success(state);
        } catch (Exception e) {
            logger.error("CliviaSwitchService[getSwitch] error", e);
            return CliviaResponse.error();
        }
    }

    /**
     * get token
     *
     * @author palading_cr
     *
     */
    public CliviaResponse getToken(ClientSecureInfo clientSecureInfo) {
        String ipParam = clientSecureInfo.getClientName();
        if (logger.isDebugEnabled()) {
            logger.debug("ServerService[getToken]:secureParam[" + clientSecureInfo.getClientPwd() + "],ipParam[" + ipParam + "]");
        }
        String token = cliviaClientSecurityConfigRepository.getToken(ipParam, clientSecureInfo.getClientPwd());
        logger.info("ServerService[getToken]:token[" + token + "]");
        if (StringUtils.isEmpty(token)) {
            return CliviaResponse.error();
        }
        return CliviaResponse.success(token);
    }
}
