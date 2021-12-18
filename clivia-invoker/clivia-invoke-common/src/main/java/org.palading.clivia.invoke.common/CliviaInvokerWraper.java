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

import org.palading.clivia.invoker.api.CliviaInvoker;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author palading_cr
 * @title CliviaCommonInvoker
 * @project clivia
 */
public interface CliviaInvokerWraper {

    Logger logger = LoggerFactory.getLogger(CliviaInvokerWraper.class);
     static final String default_error_msg = JsonUtil.toJson(CliviaResponse.error());

    /**
     * writeResponse
     *
     * @author palading_cr
     *
     */
    default Mono<Void> writeResponse(final ServerWebExchange exchange, final String result) {
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(result.getBytes())));
    }

    public Mono<Void> invoke(ServerWebExchange serverWebExchange);
}
