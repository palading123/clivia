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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.cache.CliviaStandandCacheFactory;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.event.api.constans.CliviaEventConstant;
import org.palading.clivia.event.api.listener.CliviaListenerCallable;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.HttpClientUtil;
import org.palading.clivia.support.common.util.JsonUtil;
import org.palading.clivia.support.thread.CliviaFixScheduleThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * In admin server, you can specify a gateway node to stop or continue the service, and obtain the latest status of the
 * node through the listener
 * 
 * @author palading_cr
 * @title CliviaGatewayServerOperationEventListener
 * @project clivia
 */
public class CliviaGatewayClientSwitchrEventListener implements CliviaListenerCallable<ApplicationContext, Boolean> {

    private static Logger logger = LoggerFactory.getLogger(CliviaGatewayClientSwitchrEventListener.class);

    @Override
    public Boolean invoke(ApplicationContext applicationContextr, Class<Boolean> booleanClass) throws Exception {
        return true;
    }

    /**
     * if oldSuspend and newSuspend are different,update the cache .When an exception occurs, it is not updated
     *
     * @author palading_cr
     *
     */
    private void modifySwitcherState(CliviaServerProperties cliviaServerProperties) throws Exception {
        try {

            String secureToken = getValue(CliviaConstants.gateway_node_token);
            checkTokenIfEmpty(secureToken);
            String response = getHttpResponse(cliviaServerProperties, secureToken);
            checkResponseIfEmpty(response);
            CliviaResponse cliviaResponse = JsonUtil.toObject(response, CliviaResponse.class);
            checkReponseObject(cliviaResponse);
            String oldSuspend =
                null == getValue(CliviaConstants.gateway_node_switch) ? "" : getValue(CliviaConstants.gateway_node_switch);
            String newSuspend = String.valueOf(cliviaResponse.getResData());
            if (!oldSuspend.equals(newSuspend)) {
                getCliviaStandandCacheFactory().put(CliviaConstants.gateway_node_switch, newSuspend);
            }
        } catch (Exception e) {
            logger.error("CliviaGatewayServerEvent[updateSuspend] error", e);
            throw e;
        }
    }

    @Override
    public long getPeriod(long period) {
        return period <= CliviaEventConstant.clivia_server_default_period ? CliviaEventConstant.clivia_server_default_period
            : 100 > period ? CliviaEventConstant.clivia_server_default_period : period;
    }

    @Override
    public int getOrder() {
        return CliviaConstants.clivia_server_listener_order;
    }

    private void checkTokenIfEmpty(String token) throws Exception {
        if (StringUtils.isEmpty(token)) {
            throw new Exception("CliviaGatewayServerEvent[getSuspend] secureToken is empty");
        }
    }

    private void checkResponseIfEmpty(String response) throws Exception {
        if (StringUtils.isEmpty(response)) {
            throw new Exception("CliviaGatewayServerEvent[getSuspend] remote call url["
                + CliviaConstants.gateway_admin_switch_url + "] error");
        }
    }

    private void checkReponseObject(CliviaResponse cliviaResponse) throws Exception {
        if (CliviaConstants.success != cliviaResponse.getResCode() || ObjectUtils.isEmpty(cliviaResponse.getResData())) {
            throw new Exception("CliviaGatewayServerEvent[getSuspend] remote call url["
                + CliviaConstants.gateway_admin_switch_url + "] error,the existence of returned data is abnormal");
        }
    }

    /**
     * @author palading_cr
     *
     */
    @Override
    public void invokeScheduler(ApplicationContext applicationContext) throws Exception {
        try {
            CliviaFixScheduleThreadPool
                .buildCliviaFixScheduleThreadPool()
                .getFixThreadPool()
                .scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                modifySwitcherState(getCliviaServerProperties(applicationContext));
                            } catch (Exception e) {
                                logger.error("CliviaGatewayServerOperationEventListener[invokeScheduler] error", e);
                            }
                        }
                    },
                    1,
                    getPeriod(applicationContext.getBean("cliviaServerProperties", CliviaServerProperties.class)
                        .getServerPeriod()), TimeUnit.SECONDS);
        } catch (Exception e) {
            throw e;
        }
    }

    private String getHttpResponse(CliviaServerProperties cliviaServerProperties, String token) throws Exception {
        return HttpClientUtil.sendHttpPost(
            cliviaServerProperties.getCliviaAdminUrl().concat(CliviaConstants.gateway_admin_switch_url),
            new HashMap<String, Object>() {
                {
                    put("token", token);
                }
            });
    }

    private String getValue(String cacheKey) {
        return getCliviaStandandCacheFactory().getString(cacheKey);
    }

    private CliviaStandandCacheFactory getCliviaStandandCacheFactory() {
        return CliviaStandandCacheFactory.getCliviaStandandCacheFactory();
    }

}
