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
package org.palading.clivia.request.sizeLimit;
import java.util.Optional;

import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.domain.common.ApiReqSizeLimit;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;



/**
 * @author palading_cr
 * @title CliviaRequestSizeLimitFilter
 * @project clivia
 */
public class CliviaRequestSizeLimitFilter implements CliviaFilter {

    private static final String default_error_reqSize_max_msg = JsonUtil.toJson(CliviaResponse.error_reqSize_max());

    private static Logger logger = LoggerFactory.getLogger(CliviaRequestSizeLimitFilter.class);

    private CliviaServerProperties cliviaServerProperties;

    public CliviaRequestSizeLimitFilter(CliviaServerProperties cliviaServerProperties) {
        this.cliviaServerProperties = cliviaServerProperties;
    }

    private Long contentSize(ServerWebExchange exchange) {
        String contentLength = exchange.getRequest().getHeaders().getFirst(CliviaConstants.http_content_length);
        return Long.valueOf(StringUtils.isEmpty(contentLength) ? "0" : contentLength);
    }

    private Long requestParamCacheSize(CliviaRequestContext cliviaRequestContext) {
        String requestParam = cliviaRequestContext.getRequestParam();
        return org.apache.commons.lang3.StringUtils.isNotEmpty(requestParam) ? Long.valueOf(requestParam.getBytes().length) : 0;
    }

    private Long apiReqSizeLimit(CliviaRequestContext cliviaRequestContext) {
        ApiReqSizeLimit apiReqSizeLimit = cliviaRequestContext.getAppInfo().getApiReqSizeLimit();
        return apiReqSizeLimit.getMaxSize();
    }

    /**
     * @author palading_cr
     *
     */
    private boolean verifyParamSize(Long maxSize, Long currentRequestSize) {
        return (maxSize > 0 && currentRequestSize > maxSize)
            || (cliviaServerProperties.getRequestMaxSize() > 0 && currentRequestSize > cliviaServerProperties.getRequestMaxSize());
    }

    /**
     * request size limit
     *
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        Long contentSize = contentSize(exchange);
        Long maxSize = apiReqSizeLimit(cliviaRequestContext);
        // cacheRequestParam(exchange, cliviaRequestContext);
        Long cacheRequestSize = requestParamCacheSize(cliviaRequestContext);
        if (logger.isDebugEnabled()) {
            logger.debug("CliviaRequestSizeLimitFilter[filter]contentSize:[" + contentSize + "],maxSize:[" + maxSize
                + "] ,requestMaxSize:[" + cliviaServerProperties.getRequestMaxSize() + "]");
        }
        if (cacheRequestSize > 0) {
            if (verifyParamSize(maxSize, cacheRequestSize)) {
                exchange.getResponse().setStatusCode(HttpStatus.PAYLOAD_TOO_LARGE);
                if (!exchange.getResponse().isCommitted()) {
                    return writeResponse(exchange, default_error_reqSize_max_msg);
                }
            }
        } else {
            if (contentSize > 0) {
                if (verifyParamSize(maxSize, contentSize)) {
                    exchange.getResponse().setStatusCode(HttpStatus.PAYLOAD_TOO_LARGE);
                    if (!exchange.getResponse().isCommitted()) {
                        return writeResponse(exchange, default_error_reqSize_max_msg);
                    }
                }
            }
        }
        return cliviaFilterChain.filter(exchange, cliviaFilterChain);
    }

    /**
     * if apiServiceType is not interface level ,then return false
     * 
     * @author palading_cr
     */
    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        ApiDetail apiDetail = cliviaRequestContext.getAppInfo();
        if (logger.isDebugEnabled()) {
            logger.debug("CliviaRequestSizeLimitFilter[shouldFilter] the service type of current request  is ["
                + apiDetail.getApiServiceType() + "] ");
        }
        if (CliviaConstants.api_service_type_interface.equals(apiDetail.getApiServiceType())) {
            return Optional.ofNullable(apiDetail.getApiReqSizeLimit())
                .map(apiReqSizeLimit -> apiReqSizeLimit.getEnabled() && apiReqSizeLimit.getMaxSize() > 0).orElse(false);
        }
        return false;
    }

    /**
     * @author palading_cr
     *
     */
    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_reqSizeLimit_order;
    }
}
