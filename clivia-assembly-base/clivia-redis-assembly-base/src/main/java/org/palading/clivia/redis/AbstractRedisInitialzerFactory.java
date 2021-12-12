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
package org.palading.clivia.redis;

import org.palading.clivia.common.api.CliviaServerProperties;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRedisInitialzerFactory {

    public abstract LettuceConnectionFactory buildLettuceConnectionFactory(CliviaServerProperties cliviaServerProperties);

    /**
     * add redis node
     * 
     * @author palading_cr
     *
     */
    protected List<RedisNode> buildRedisNodes(String url) {
        List<RedisNode> redisNodes = new ArrayList<>();
        try {
            String[] redisUrl = url.split(",");
            for (int i = 0; i < redisUrl.length; i++) {
                String[] node = redisUrl[i].split(":");
                RedisNode redisNode = new RedisNode(node[0], Integer.parseInt(node[1]));
                redisNodes.add(redisNode);
            }
        } catch (Exception e) {
        }
        return redisNodes;
    }

}
