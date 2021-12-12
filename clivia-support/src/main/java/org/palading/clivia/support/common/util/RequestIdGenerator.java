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
package org.palading.clivia.support.common.util;

import java.util.UUID;

/**
 * @author palading_cr
 * @title RequestIdGenerator
 * @project clivia
 */
public class RequestIdGenerator {
    private static final String CLIVIA_REQUEST_ID = "CLIVIA_REQ_ID";
    private static final ThreadLocal<Integer> ThreadTraceIdSequence = new ThreadLocal<Integer>();

    private static final String PROCESS_UUID;

    static {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        PROCESS_UUID = uuid.substring(uuid.length() - 7);
    }

    private RequestIdGenerator() {}

    /**
     * TraceId由以下规则组成<br/>
     * 2位version号 + 1位时间戳（毫秒数） + 1位进程随机号（UUID后7位） + 1位进程数号 + 1位线程号 + 1位线程内序号
     *
     * 注意：这里的位，是指“.”作为分隔符所占的位数，非字符串长度的位数。 TraceId为不定长字符串，但保证在分布式集群条件下的唯一性
     *
     * @return
     */
    public static String generate() {
        Integer seq = ThreadTraceIdSequence.get();
        if (seq == null || seq == 10000 || seq > 10000) {
            seq = 0;
        }
        seq++;
        ThreadTraceIdSequence.set(seq);

        return CLIVIA_REQUEST_ID + "." + System.currentTimeMillis() + "." + PROCESS_UUID + "." + MacheineUtil.getProcessNo()
            + "." + Thread.currentThread().getId() + "." + seq;
    }

}
