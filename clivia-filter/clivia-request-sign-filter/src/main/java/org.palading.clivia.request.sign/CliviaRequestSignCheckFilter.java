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
package org.palading.clivia.request.sign;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.domain.common.ApiAuth;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * @author palading_cr
 * @title CliviaRequestSignCheckFilter
 * @project clivia
 */
public class CliviaRequestSignCheckFilter implements CliviaFilter {

    private static final String error_sign_empty_msg = JsonUtil.toJson(CliviaResponse.error_sign_empty());
    private static final String error_sign_request_invalid = JsonUtil.toJson(CliviaResponse.error_sign_request_invalid());

    private static final Pattern pattern = Pattern.compile("^[1-9]\\d*$");

    private static Logger logger = LoggerFactory.getLogger(CliviaRequestSignCheckFilter.class);

    public static void main(String[] args) {
        String signKey = "appKey1".concat("1").concat("/api/test").concat("asdasdqeqwe");
        System.out.println(DigestUtils.md5DigestAsHex(signKey.getBytes()));
    }

    /**
     * request param encryption verification
     *
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        if (null != cliviaRequestContext.getRequestParam()) {
            if (StringUtils.isEmpty(cliviaRequestContext.getSign())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("CliviaRequestSignCheckFilter[filter] request sign is empty");
                }
                return writeResponse(exchange, error_sign_empty_msg);
            }
            if (invalidCheck(cliviaRequestContext)) {
                return writeResponse(exchange, error_sign_request_invalid);
            }
            if (sign(cliviaRequestContext.getSign(), buildSign(cliviaRequestContext))) {
                return cliviaFilterChain.filter(exchange, cliviaFilterChain);
            }
            return writeResponse(exchange, JsonUtil.toJson(CliviaResponse.error_sign_not_pass()));
        }
        return cliviaFilterChain.filter(exchange, cliviaFilterChain);
    }

    /**
     * build sign
     *
     * @author palading_cr
     *
     */
    private String buildSign(CliviaRequestContext cliviaRequestContext) {
        String signKey =
            cliviaRequestContext.getAppKey().concat(cliviaRequestContext.getGroup()).concat(cliviaRequestContext.getPath())
                .concat(cliviaRequestContext.getNonce());
        return DigestUtils.md5DigestAsHex(signKey.getBytes());
    }

    /**
     * invalid check
     *
     * @author palading_cr
     *
     */
    private boolean invalidCheck(CliviaRequestContext cliviaRequestContext) {
        ApiAuth apiAuth = cliviaRequestContext.getAppInfo().getApiAuth();
        LocalDateTime startTime = cliviaRequestContext.getRequestTime();
        LocalDateTime endTime = LocalDateTime.now();
        long invalid = invalidTime(apiAuth.getInvalid());
        long mils = startTime.until(endTime, ChronoUnit.MILLIS);
        return mils > invalid;
    }

    /**
     * request invalid
     *
     * @author palading_cr
     *
     */
    private long invalidTime(String invalid) {
        long defaultInvalid = 5000;
        if (StringUtils.isEmpty(invalid)) {
            return defaultInvalid;
        }
        if (pattern.matcher(invalid).matches()) {
            if (Long.valueOf(invalid) > 20000) {
                return defaultInvalid;
            } else {
                return Long.valueOf(invalid);
            }
        }
        return defaultInvalid;
    }

    /**
     * param verification
     *
     * @author palading_cr
     *
     */
    private boolean sign(String sign, String buildSign) {
        return sign.equals(buildSign);
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_auth_order;
    }

    /**
     * should Filter if ApiAuth enabled is true
     *
     * @author palading_cr
     *
     */
    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        return true;
        // CliviaRequestContext cliviaRequestContext =
        // (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        // return Optional.of(cliviaRequestContext).map(u -> u.getAppInfo())
        // .map(f -> (null != f.getApiAuth() && f.getApiAuth().getEnabled())).orElse(false);
    }
}
