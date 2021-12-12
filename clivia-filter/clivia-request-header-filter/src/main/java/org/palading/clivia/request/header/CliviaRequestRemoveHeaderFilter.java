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
package org.palading.clivia.request.header;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.domain.common.ApiHeader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @author palading_cr
 * @title CliviaRequestRemoveHeaderFilter
 * @project clivia
 */
public class CliviaRequestRemoveHeaderFilter implements CliviaFilter {

    /**
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        ApiDetail appInfo = cliviaRequestContext.getAppInfo();
        ApiHeader apiHeader = appInfo.getApiHeader();
        if (null != apiHeader) {
            String removeHeader = apiHeader.getRemoveHeader();
            if (StringUtils.isNotEmpty(removeHeader)) {
                String[] headers = removeHeader.split(",");
                for (String headerName : headers) {
                    exchange.getRequest().mutate().headers(httpHeaders1 -> httpHeaders1.remove(headerName));
                }
                ServerHttpRequest serverRequest = exchange.getRequest().mutate().build();
                exchange.mutate().request(serverRequest).build();
            }
        }
        return cliviaFilterChain.filter(exchange, cliviaFilterChain);
    }

    /**
     * @author palading_cr
     *
     */
    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        return Optional.ofNullable(cliviaRequestContext.getAppInfo()).map(apiDetail -> apiDetail.getApiHeader())
            .map(apiHeader -> apiHeader.getEnabled() && StringUtils.isNotEmpty(apiHeader.getRemoveHeader())).orElse(false);
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_header_remove_order;
    }
}
