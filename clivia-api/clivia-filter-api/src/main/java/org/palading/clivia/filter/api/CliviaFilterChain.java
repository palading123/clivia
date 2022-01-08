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
package org.palading.clivia.filter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author palading_cr
 * @title CliviaFilterChain
 * @project clivia
 */
public class CliviaFilterChain {

    private static Logger logger = LoggerFactory.getLogger(CliviaFilterChain.class);

    private int index;
    private String defaultErrorMessage;
    private List<CliviaFilter> cliviaFilterList;

    public CliviaFilterChain(List<CliviaFilter> cliviaFilterList, String defaultErrorMessage) {
        this.cliviaFilterList = cliviaFilterList;
        this.defaultErrorMessage = defaultErrorMessage;
    }

    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        return Mono
            .defer(() -> {
                if (this.index < cliviaFilterList.size()) {
                    CliviaFilter cliviaFilter = cliviaFilterList.get(this.index++);
                        if (!cliviaFilter.shouldFilter(exchange)) {
                            return filter(exchange, cliviaFilterChain);
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("CliviaFilterChain[filter] current filter[" + cliviaFilter.getClass().getSimpleName()
                                + "]");
                        }
                        return cliviaFilter.filter(exchange, this);

                }else{
                    return Mono.empty();
                }
            });
    }
}
