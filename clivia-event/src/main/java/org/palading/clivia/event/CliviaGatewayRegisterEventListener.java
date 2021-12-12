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
package org.palading.clivia.event;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.cache.CliviaStandandCacheFactory;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.event.api.constans.CliviaEventConstant;
import org.palading.clivia.event.api.listener.CliviaListenerCallable;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.domain.ClientSecureInfo;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.HttpClientUtil;
import org.palading.clivia.support.common.util.JsonUtil;
import org.palading.clivia.support.thread.CliviaFixScheduleThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Obtain the latest token through the user name and password. If the token expires, the service will be denied
 * 
 * @author palading_cr
 * @title CliviaGatewayRegisterListener1
 * @project clivia
 */
public class CliviaGatewayRegisterEventListener implements CliviaListenerCallable<ApplicationContext, Boolean> {

    private static Logger logger = LoggerFactory.getLogger(CliviaGatewayRegisterEventListener.class);

    @Override
    public Boolean invoke(ApplicationContext applicationContext, Class<Boolean> booleanClass) throws Exception {
        return (Boolean)getToken(getCliviaServerProperties(applicationContext));
    }

    /**
     * @author palading_cr
     *
     */
    @Override
    public void invokeScheduler(ApplicationContext applicationContext) throws Exception {
        try {
            CliviaFixScheduleThreadPool.buildCliviaFixScheduleThreadPool().getFixThreadPool().scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        invoke(applicationContext, boolean.class);
                    } catch (Exception e) {
                        logger.error("CliviaGatewayRegisterEventListener[invokeScheduler] error", e);
                    }
                }
            }, 1, getPeriod(getCliviaServerProperties(applicationContext).getRegisterPeriod()), TimeUnit.SECONDS);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * get token from admin server
     *
     * @author palading_cr
     *
     */
    private String getHttpResponse(CliviaServerProperties cliviaServerProperties) throws Exception {
        return HttpClientUtil.sendHttpPostJson(
            cliviaServerProperties.getCliviaAdminUrl().concat(CliviaConstants.gateway_admin_fetch_token_url), JsonUtil
                .toJson(new ClientSecureInfo(cliviaServerProperties.getCliviaClientName(), cliviaServerProperties
                    .getCliviaClientPwd())));
    }

    /**
     * put token if the token is not exists
     *
     * @author palading_cr
     *
     */
    private Object getToken(CliviaServerProperties cliviaServerProperties) {
        try {
            String res = getHttpResponse(cliviaServerProperties);
            if (!StringUtils.isEmpty(res)) {
                CliviaResponse response = JsonUtil.toObject(res, CliviaResponse.class);
                if (CliviaConstants.success == response.getResCode()) {
                    if (Objects.nonNull(response.getResData())) {
                        String secureToken = String.valueOf(response.getResData());
                        CliviaStandandCacheFactory cliviaStandandCacheFactory =
                            CliviaStandandCacheFactory.getCliviaStandandCacheFactory();
                        if (Objects.nonNull(cliviaStandandCacheFactory.getString(CliviaConstants.gateway_node_token))
                            && secureToken.equals(cliviaStandandCacheFactory.getString(CliviaConstants.gateway_node_token))) {
                            return true;
                        }
                        cliviaStandandCacheFactory.put(CliviaConstants.gateway_node_token, secureToken);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("CliviaGatewayRegisterEvent[getToken] error", e);
            if (null != CliviaStandandCacheFactory.getCliviaStandandCacheFactory().get(CliviaConstants.gateway_node_token)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long getPeriod(long period) {
        return period <= CliviaEventConstant.clivia_register_default_period ? CliviaEventConstant.clivia_register_default_period
            : 100 > period ? CliviaEventConstant.clivia_register_default_period : period;
    }

    @Override
    public int getOrder() {
        return CliviaConstants.clivia_register_listener_order;
    }

}
