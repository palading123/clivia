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
package org.palading.clivia.event.api.listener;

/**
 * @author palading_cr
 * @title CliviaListenerCallable
 * @project clivia /22
 */
public interface CliviaListenerCallable<T, S> extends CliviaListener {

    /**
     * Custom event execution with return value
     *
     * @author palading_cr
     *
     */
    S invoke(T t, Class<S> sClass) throws Exception;

    /**
     * Execute timing events without return value
     *
     * @author palading_cr /24
     *
     */
    void invokeScheduler(T t) throws Exception;

    /**
     * Get the scheduling interval of each event thread
     * 
     * @author palading_cr /24
     *
     */
    long getPeriod(long period);

}
