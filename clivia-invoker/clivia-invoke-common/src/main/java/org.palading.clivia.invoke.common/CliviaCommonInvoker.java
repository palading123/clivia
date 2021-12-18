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
package org.palading.clivia.invoke.common;

import java.util.Objects;

import org.palading.clivia.cache.DefaultCliviaCacheManager;
import org.palading.clivia.invoker.api.CliviaInvoker;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;


/**
 * @author palading_cr
 * @title CliviaCommonInvoker
 * @project clivia
 */
public class CliviaCommonInvoker implements CliviaInvokerWraper {

    private static final String default_error_msg = JsonUtil.toJson(CliviaResponse.error());

    @Override
    public Mono<Void> invoke(ServerWebExchange serverWebExchange) {
        CliviaInvoker cliviaInvoker = null;
        try {
            CliviaRequestContext cliviaRequestContext =
                (CliviaRequestContext)serverWebExchange.getAttributes().get(CliviaConstants.request_context);
            String rpcType = cliviaRequestContext.getAppInfo().getRpcType();
            if (CliviaConstants.clivia_system_invoker.equals(rpcType)) {
                throw new Exception("CliviaCommonInvoker[invoke] rpcType is system,you can not call this one ,rpcType[" + rpcType
                    + "]");
            }
            cliviaInvoker = DefaultCliviaCacheManager.getCliviaServerCache().getCacheApiInvoker().get(rpcType);
            if (Objects.isNull(cliviaInvoker)) {
                throw new Exception("CliviaCommonInvoker[invoke] rpcType is not exists,rpcType[" + rpcType + "]");
            }
            Mono<Void> res = cliviaInvoker.invoke(serverWebExchange);
            return res;
        } catch (Exception e) {
            logger
                .error("CliviaCommonInvoker[invoke] current invoker[" + cliviaInvoker.getClass().getSimpleName() + "] error", e);
        }
        return writeResponse(serverWebExchange, default_error_msg);
    }

//    @Override
//    public String getRpcType() {
//        return CliviaConstants.clivia_system_invoker;
//    }

}
