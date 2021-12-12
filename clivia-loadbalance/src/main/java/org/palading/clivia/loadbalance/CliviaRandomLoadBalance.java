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
import java.util.concurrent.ThreadLocalRandom;

/**
 * random load balance
 * 
 * @author palading_cr
 * @title CliviaRandomLoadBalance
 * @project clivia
 */
public class CliviaRandomLoadBalance extends CliviaAbstractLoadbalance {

    /**
     * random load balance. if all of apiLoadbalanceRouters are not available,then return null . from dubbo
     * 2.7.2-SNAPSHOT
     * 
     * @author palading_cr
     *
     */
    @Override
    protected ApiDefaultLoadbalanceRouter doChoose(ApiDefaultRoute apiDefaultRoute) {
        List<ApiDefaultLoadbalanceRouter> apiDefaultLoadbalanceRouters = apiDefaultRoute.getLoadbalanceRouters();
        // Number of ApiLoadbalanceRouter
        int length = apiDefaultLoadbalanceRouters.size();
        // Every ApiLoadbalanceRouter has the same weight?
        boolean sameWeight = true;
        // the weight of every invokers
        int[] weights = new int[length];
        // the first invoker's weight
        int firstWeight = getWeight(apiDefaultLoadbalanceRouters.get(0));
        weights[0] = firstWeight;
        // The sum of weights
        int totalWeight = firstWeight;
        for (int i = 1; i < length; i++) {
            int weight = getWeight(apiDefaultLoadbalanceRouters.get(i));
            // save for later use
            weights[i] = weight;
            // Sum
            totalWeight += weight;
            if (sameWeight && weight != firstWeight) {
                sameWeight = false;
            }
        }
        if (totalWeight > 0 && !sameWeight) {
            // If (not every invoker has the same weight & at least one invoker's weight>0), select randomly based on
            // totalWeight.
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            // Return a invoker based on the random value.
            for (int i = 0; i < length; i++) {
                offset -= weights[i];
                if (offset < 0) {
                    return apiDefaultLoadbalanceRouters.get(i);
                }
            }
        }
        // If all invokers have the same weight value or totalWeight=0, return evenly.
        return apiDefaultLoadbalanceRouters.get(ThreadLocalRandom.current().nextInt(length));
    }
}
