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
package org.palading.clivia.loadbalance;


import org.palading.clivia.support.common.domain.ApiDefaultLoadbalanceRouter;
import org.palading.clivia.support.common.domain.ApiDefaultRoute;

import java.util.List;

/**
 * @author palading_cr
 * @title CliviaAbstractLoadbalance
 * @project clivia
 */
public abstract class CliviaAbstractLoadbalance implements Loadbalance {

    /**
     * Calculate the weight according to the uptime proportion of warmup time the new weight will be within 1(inclusive)
     * to weight(inclusive)
     *
     * @param uptime
     *            the uptime in milliseconds
     * @param warmup
     *            the warmup time in milliseconds
     * @param weight
     *            the weight of an invoker
     * @return weight which takes warmup into account
     */
    static int calculateWarmupWeight(int uptime, int warmup, int weight) {
        int ww = (int)((float)uptime / ((float)warmup / (float)weight));
        return ww < 1 ? 1 : (ww > weight ? weight : ww);
    }

    static int getHash(String str) {
        final int p = 16777619;
        int hash = (int)2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }

    protected int getWeight(ApiDefaultLoadbalanceRouter apiDefaultLoadbalanceRouter) {
        int weight = apiDefaultLoadbalanceRouter.getUpstreamWeight();
        if (weight > 0) {
            long timestamp = apiDefaultLoadbalanceRouter.getTimestamp() <= 0 ? 0L : apiDefaultLoadbalanceRouter.getTimestamp();
            if (timestamp > 0L) {
                int uptime = (int)(System.currentTimeMillis() - timestamp);

                int warmup =
                    apiDefaultLoadbalanceRouter.getWarmup() > 0 ? apiDefaultLoadbalanceRouter.getWarmup() : 10 * 60 * 1000;
                if (uptime > 0 && uptime < warmup) {
                    weight = calculateWarmupWeight(uptime, warmup, weight);
                }
            }
        }
        return weight >= 0 ? weight : 0;
    }

    protected abstract ApiDefaultLoadbalanceRouter doChoose(ApiDefaultRoute apiDefaultRoute);

    /**
     * get ApiLoadbalanceRouter by apiLoadbalance
     *
     * @author palading_cr
     *
     */
    @Override
    public ApiDefaultLoadbalanceRouter choose(ApiDefaultRoute apiDefaultRoute) {
        List<ApiDefaultLoadbalanceRouter> apiDefaultLoadbalanceRouters = apiDefaultRoute.getLoadbalanceRouters();
        if (null == apiDefaultLoadbalanceRouters) {
            return null;
        }
        if (apiDefaultLoadbalanceRouters.size() == 1) {
            return apiDefaultLoadbalanceRouters.get(0).getEnabled() ? apiDefaultLoadbalanceRouters.get(0) : null;
        }
        if (apiDefaultLoadbalanceRouters.size() > 1) {
            return doChoose(apiDefaultRoute);
        }
        return null;
    }

}
