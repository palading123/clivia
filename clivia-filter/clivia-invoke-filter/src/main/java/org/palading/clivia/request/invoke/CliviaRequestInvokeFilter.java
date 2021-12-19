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
package org.palading.clivia.request.invoke;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.invoke.common.CliviaInvokerWraper;
import org.palading.clivia.invoke.common.CliviaInvokerWraperFactory;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author palading_cr
 * @title CliviaRequestInvokeFilter
 * @project clivia
 */
public class CliviaRequestInvokeFilter implements CliviaFilter {

    private CliviaInvokerWraperFactory cliviaInvokerWraperFactory;

    public CliviaRequestInvokeFilter(CliviaInvokerWraperFactory cliviaInvokerWraperFactory) {
        this.cliviaInvokerWraperFactory = cliviaInvokerWraperFactory;
    }

    /**
     * get invoker and invoke it
     *
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaInvokerWraper cliviaInvokerWraper = cliviaInvokerWraperFactory.create();
        return cliviaInvokerWraperFactory.invoke(exchange,cliviaInvokerWraper);
    }

    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        return true;
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_invoker_order;
    }
}
