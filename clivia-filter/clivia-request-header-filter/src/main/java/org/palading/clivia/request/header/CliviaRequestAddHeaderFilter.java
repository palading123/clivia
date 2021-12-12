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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * add fixed filter
 * 
 * @author palading_cr
 * @title CliviaRequestHeaderFilter
 * @project clivia
 */
public class CliviaRequestAddHeaderFilter implements CliviaFilter {

    /**
     * set data to the http header
     *
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        ApiDetail appInfo = cliviaRequestContext.getAppInfo();
        ApiHeader apiHeader = appInfo.getApiHeader();
        String addHeader = apiHeader.getAddHeader();
        if (StringUtils.isNotEmpty(addHeader)) {
            List<Header> headerList = headers(addHeader);
            ServerHttpRequest serverRequest = exchange.getRequest();
            if (null != headerList && headerList.size() > 0) {
                for (Header header : headerList) {
                    serverRequest.mutate().header(header.getHeaderName(), header.getHeadValue());
                }
                exchange.mutate().request(serverRequest).build();
            }
        }
        return cliviaFilterChain.filter(exchange, cliviaFilterChain);
    }

    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        return Optional
            .ofNullable(cliviaRequestContext.getAppInfo())
            .filter(
                apiDetail1 -> CliviaConstants.rpc_type_http.equals(apiDetail1.getRpcType())
                    || CliviaConstants.rpc_type_springcloud.equals(apiDetail1.getRpcType())).map(ApiDetail::getApiHeader)
            .map(apiHeader -> apiHeader.getEnabled() && StringUtils.isNotEmpty(apiHeader.getAddHeader())).orElse(false);
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_header_add_order;
    }

    private List<Header> headers(String headers) {
        List<Header> headerList = null;
        if (StringUtils.isNotEmpty(headers)) {
            String[] headerArray = headers.split(",");
            if (null != headerArray && headerArray.length > 0) {
                headerList = new ArrayList<>();
                for (String header : headerArray) {
                    String[] arr = header.split("=");
                    String headName = arr[0];
                    String headValue = arr[1];
                    Header head = new Header(headName, headValue);
                    headerList.add(head);
                }
            }
        }
        return headerList;
    }

    class Header {
        private String headerName;
        private String headValue;

        public Header(String headerName, String headValue) {
            this.headerName = headerName;
            this.headValue = headValue;
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public String getHeadValue() {
            return headValue;
        }

        public void setHeadValue(String headValue) {
            this.headValue = headValue;
        }
    }
}
