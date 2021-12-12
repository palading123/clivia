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
package org.palading.clivia.support.thread;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/** @ClassName CliviaFixThread @Description TODO @Author palading_cr @Version 1.0 */
public class CliviaFixScheduleThreadPool {

    private static volatile CliviaFixScheduleThreadPool CLIVIA_FIX_THREAD_POOL;

    private static List<ScheduledExecutorService> workerCache;

    private final String threadNamePrefix;

    private AtomicInteger index;

    private int poolSize;

    private CliviaFixScheduleThreadPool(int poolSize, String threadNamePrefix) {
        super();
        this.poolSize = poolSize > 0 ? poolSize : 3;
        this.threadNamePrefix = threadNamePrefix;
        initEven();
    }

    public static CliviaFixScheduleThreadPool buildCliviaFixScheduleThreadPool() {
        if (null == CLIVIA_FIX_THREAD_POOL) {
            synchronized (CliviaFixScheduleThreadPool.class) {
                if (null == CLIVIA_FIX_THREAD_POOL) {
                    CLIVIA_FIX_THREAD_POOL = new CliviaFixScheduleThreadPool(3, "clivia.schedule.thread");
                }
            }
        }
        return CLIVIA_FIX_THREAD_POOL;
    }

    /**
     *
     * @author palading_cr
     *
     */
    private void initEven() {
        index = new AtomicInteger(0);
        workerCache = new CopyOnWriteArrayList<>();
        for (int i = 0; i < poolSize; i++) {
            workerCache.add(Executors.newScheduledThreadPool(1, new CliviaThreadFactory(threadNamePrefix + i + "_", false)));
        }
    }

    public ScheduledExecutorService getFixThreadPool() {
        if (poolSize > 0) {
            int nextIndex = index.addAndGet(1) % poolSize;
            index.set(nextIndex);
            return workerCache.get(nextIndex);
        }
        return null;
    }
}
