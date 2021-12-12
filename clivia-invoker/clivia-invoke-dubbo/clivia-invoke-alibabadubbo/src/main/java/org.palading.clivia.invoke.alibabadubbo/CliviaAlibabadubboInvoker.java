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
package org.palading.clivia.invoke.alibabadubbo;

import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.invoke.alibabadubbo.refrence.CliviaDubboGenericService;
import org.palading.clivia.invoker.api.CliviaInvoker;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.domain.ApiNonHttpRoute;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;


/**
 * @author palading_cr
 * @title CliviaAlibabadubboInvoker
 * @project clivia
 */
public class CliviaAlibabadubboInvoker implements CliviaInvoker {

    private static Logger logger = LoggerFactory.getLogger(CliviaAlibabadubboInvoker.class);

    /**
     * invoke dubbo service
     *
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> invoke(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        ApiDetail apiDetail = cliviaRequestContext.getAppInfo();
        ApiNonHttpRoute apiNonHttpRoute = apiDetail.getApiNonHttpRoute();
        String[] parameterTypes;
        if (StringUtils.isEmpty(apiNonHttpRoute.getParameterTypes())) {
            parameterTypes = new String[] {};
        } else {
            parameterTypes = apiNonHttpRoute.getParameterTypes().split(",");
        }
        Object[] val = getParamValByRequest(exchange);
        CliviaDubboGenericService cliviaDubboGenericService = CliviaDubboGenericService.getCliviaDubboGenericServiceInstance();
        GenericService genericService = cliviaDubboGenericService.getGenericService(apiNonHttpRoute);
        // the dubbo service has no found
        if (null == genericService) {
            return writeResponse(exchange, JsonUtil.toJson(CliviaResponse.no_dubbo_service_error()));
        }
        // dubbo call
        Object object = genericService.$invoke(apiNonHttpRoute.getMethodName(), parameterTypes, val);
        return Mono.defer(() -> writeResponse(exchange, JsonUtil.toJson(CliviaResponse.success(object)))).onErrorResume(e -> {
            logger.error("CliviaAlibabadubboInvoker[invoke] error", e.getMessage());
            return writeResponse(exchange, JsonUtil.toJson(CliviaResponse.no_dubbo_service_error()));
        }).then();

    }

    /**
     * @author palading_cr
     *
     */
    private Object[] getParamValByRequest(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        MediaType mediaType = getMediaType(serverHttpRequest);
        if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
            String body = getBodyFromRequest(serverHttpRequest);
            if (StringUtils.isNotEmpty(body)) {
                Map<String, Object> valMap = JsonUtil.toObject(body, Map.class);
                return new Object[] {valMap};
            }
        } else {
            MultiValueMap<String, String> multiValueMap = serverHttpRequest.getQueryParams();
            if (null != multiValueMap) {
                return new Object[] {multiValueMap.toSingleValueMap()};
            }
            return new Object[] {};
        }
        return new Object[] {};
    }

    @Override
    public String getRpcType() {
        return CliviaConstants.rpc_type_alibabadubbo;
    }
}
