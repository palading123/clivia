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
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * ConsistentHash LoadBalance
 * 
 * @author palading_cr
 * @title CliviaConsistentHashLoadBalance
 * @project clivia
 */
public class CliviaConsistentHashLoadBalance extends CliviaAbstractLoadbalance {

    @Override
    protected ApiDefaultLoadbalanceRouter doChoose(ApiDefaultRoute apiDefaultRoute) {
        List<ApiDefaultLoadbalanceRouter> apiDefaultLoadbalanceRouters = apiDefaultRoute.getLoadbalanceRouters();
        SortedMap<Integer, ApiDefaultLoadbalanceRouter> sortedMap = new TreeMap<>();
        for (ApiDefaultLoadbalanceRouter apiDefaultLoadbalanceRouter : apiDefaultLoadbalanceRouters) {
            int hash = getHash(apiDefaultLoadbalanceRouter.getUpstreamUrl());
            sortedMap.put(hash, apiDefaultLoadbalanceRouter);
        }
        int hash = getHash(apiDefaultRoute.getClientIp());
        // Get all maps larger than the hash value
        SortedMap<Integer, ApiDefaultLoadbalanceRouter> subMap = sortedMap.tailMap(hash);
        if (subMap.isEmpty()) {
            Integer i = sortedMap.firstKey();
            return sortedMap.get(i);
        } else {
            Integer i = subMap.firstKey();
            return subMap.get(i);
        }

    }

}
