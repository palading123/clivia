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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/** @ClassName CliviaThreadFactory @Description TODO @Author palading_cr @Version 1.0 */
public class CliviaThreadFactory implements ThreadFactory {

    AtomicInteger index = new AtomicInteger(0);
    private String threadNamePrefix;
    private boolean isDaemon;

    public CliviaThreadFactory(String threadNamePrefix, boolean isDaemon) {
        super();
        this.threadNamePrefix = threadNamePrefix;
        this.isDaemon = isDaemon;
    }

    @Override
    public Thread newThread(Runnable r) {

        Thread thread = new Thread(r, threadNamePrefix + index.addAndGet(1));
        thread.setDaemon(isDaemon);
        return thread;
    }
}
