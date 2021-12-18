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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.palading.clivia.cache.DefaultCliviaCacheManager;
import org.palading.clivia.invoker.api.CliviaInvoker;
import org.palading.clivia.micrometer.CliviaCountMetricsBuilderFactory;
import org.palading.clivia.micrometer.CliviaTimerMetricsBuilderFactory;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
/**
 * @author palading_cr
 * @title CliviaMetricsInvoker
 * @project clivia
 */
public class CliviaMetricsInvoker implements CliviaInvokerWraper{

    private CliviaCountMetricsBuilderFactory cliviaCountMetricsBuilderFactory;
    private CliviaTimerMetricsBuilderFactory cliviaTimerMetricsBuilderFactory;
    private MeterRegistry meterRegistry;
    public CliviaMetricsInvoker(CliviaCountMetricsBuilderFactory cliviaCountMetricsBuilderFactory,CliviaTimerMetricsBuilderFactory cliviaTimerMetricsBuilderFactory,MeterRegistry meterRegistry) {
        this.cliviaCountMetricsBuilderFactory = cliviaCountMetricsBuilderFactory;
        this.cliviaTimerMetricsBuilderFactory = cliviaTimerMetricsBuilderFactory;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Mono<Void> invoke(ServerWebExchange serverWebExchange) {
        CliviaInvoker cliviaInvoker = null;
        Timer.Sample sample = null;
        Timer timer = null;
        try {
            CliviaRequestContext cliviaRequestContext =
                    (CliviaRequestContext)serverWebExchange.getAttributes().get(CliviaConstants.request_context);
            try {
                timer = timer(cliviaRequestContext);
                sample = cliviaTimerMetricsBuilderFactory.start(timer);
                cliviaCountMetricsBuilderFactory.increment(counter(cliviaRequestContext));
            }catch (Exception e){

            }

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
        }finally {
            cliviaTimerMetricsBuilderFactory.stop(timer,sample);
        }
        return writeResponse(serverWebExchange, default_error_msg);
    }

    private Timer timer(CliviaRequestContext cliviaRequestContext){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        return cliviaTimerMetricsBuilderFactory.create(builder -> builder.tags("url",cliviaRequestContext.getPath()).tags("requestTime",df.format(cliviaRequestContext.getRequestTime())).publishPercentileHistogram().register(this.meterRegistry));
    }

    private Counter counter(CliviaRequestContext cliviaRequestContext){
        return cliviaCountMetricsBuilderFactory.create(builder -> builder.tag("url",cliviaRequestContext.getPath()).tag("rpcType", cliviaRequestContext.getRpcType()).register(this.meterRegistry));
    }
}
