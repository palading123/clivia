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
package org.palading.clivia.request.parameter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.domain.common.ApiParamModify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.alibaba.fastjson.JSONObject;


/**
 * @author palading_cr
 * @title CliviaRequestAddParameterFilter
 * @project clivia
 */
public class CliviaRequestAddParameterFilter implements CliviaFilter {

    private static Logger logger = LoggerFactory.getLogger(CliviaRequestAddParameterFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        ApiDetail apiDetail = cliviaRequestContext.getAppInfo();
        String requestCacheParam = cliviaRequestContext.getRequestParam();
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethodValue();
        String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        if (StringUtils.isNotEmpty(requestCacheParam)) {
            ApiParamModify apiParamModify = apiDetail.getApiParamModify();
            if (null == apiParamModify || !apiParamModify.getEnabled() || StringUtils.isEmpty(apiParamModify.getModify())) {
                logger.warn("CliviaRequestAddParameterFilter[filter] the paramModify property is not set correctly ");
                return cliviaFilterChain.filter(exchange, cliviaFilterChain);
            }
            if (CliviaConstants.clivia_request_modify_type_add.equals(apiParamModify.getType())) {
                if (CliviaConstants.clivia_support_request_method_get.equals(method)) {
                    ServerHttpRequest requestNew =
                        exchange.getRequest().mutate()
                            .uri(rebuildUri(request, rebuildRequestParam(apiParamModify, requestCacheParam))).build();
                    return cliviaFilterChain.filter(exchange.mutate().request(requestNew).build(), cliviaFilterChain);
                }
                if (CliviaConstants.clivia_support_request_method_post.equals(method)) {
                    String body = "";
                    if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(contentType)) {
                        body = rebuildRequestParam(apiParamModify, requestCacheParam).toString();
                    }
                    if (MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentType)) {
                        body = rebuildJsonRequestParam(apiParamModify, requestCacheParam).toJSONString();
                    }
                    DataBuffer bodyDataBuffer = getDataBufferByBodyStr(body);
                    Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
                    HttpHeaders headers = new HttpHeaders();
                    headers.putAll(exchange.getRequest().getHeaders());
                    int length = body.getBytes().length;
                    headers.setContentLength(length);
                    if (StringUtils.isNotBlank(contentType)) {
                        headers.set(HttpHeaders.CONTENT_TYPE, contentType);
                    }
                    request = new ServerHttpRequestDecorator(request) {
                        @Override
                        public HttpHeaders getHeaders() {
                            long contentLength = headers.getContentLength();
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.putAll(super.getHeaders());
                            if (contentLength > 0) {
                                httpHeaders.setContentLength(contentLength);
                            } else {
                                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                            }
                            return httpHeaders;
                        }

                        @Override
                        public Flux<DataBuffer> getBody() {
                            return bodyFlux;
                        }
                    };
                    request.mutate().header(HttpHeaders.CONTENT_LENGTH, Integer.toString(body.length()));
                    return cliviaFilterChain.filter(exchange.mutate().request(request).build(), cliviaFilterChain);
                }
            }
        }
        return cliviaFilterChain.filter(exchange, cliviaFilterChain);
    }

    private JSONObject rebuildJsonRequestParam(ApiParamModify apiParamModify, String requestCacheParam) {
        JSONObject jsonObject = JSONObject.parseObject(requestCacheParam);
        jsonObject.putAll(buildMapParam(apiParamModify));
        return jsonObject;
    }

    private Map<String, String> buildMapParam(ApiParamModify apiParamModify) {
        return Arrays.asList(apiParamModify.getModify().split(",")).stream().map(modifyList -> modifyList.split("="))
            .collect(Collectors.toMap(modifyListArr -> modifyListArr[0], modifyListArr -> modifyListArr[1]));
    }

    private String rebuildRequestParam(ApiParamModify apiParamModify, String requestCacheParam) {
        StringBuilder requestBuilder = new StringBuilder(requestCacheParam);
        Map<String, String> param = buildMapParam(apiParamModify);
        for (Map.Entry<String, String> entry : param.entrySet()) {
            requestBuilder.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return requestBuilder.toString();
    }

    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        return Optional
            .ofNullable(cliviaRequestContext.getAppInfo())
            .map(apiDetail -> apiDetail.getApiParamModify())
            .map(
                apiParamModify -> apiParamModify.getEnabled()
                    && apiParamModify.getType().equals(CliviaConstants.clivia_request_modify_type_add)).orElse(false);
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_add_param_order;
    }
}
