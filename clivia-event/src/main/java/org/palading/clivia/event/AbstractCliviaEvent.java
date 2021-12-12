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
package org.palading.clivia.event;

import org.palading.clivia.event.api.listener.CliviaListenerCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author palading_cr
 * @title AbstractCliviaEvent
 * @project clivia
 */
public abstract class AbstractCliviaEvent<T> {

    private static Logger logger = LoggerFactory.getLogger(AbstractCliviaEvent.class);

    /**
     * @author palading_cr
     *
     */
    public boolean invokeEvent(CliviaListenerCallable callable, T t) {
        boolean invoke = false;
        try {
            invoke = (Boolean)callable.invoke(t, Boolean.class);
        } catch (Exception e) {
            logger.error(
                "AbstractCliviaEvent[invokeEvent] error," + "current CliviaListenerCallable[" + callable.getListenerName() + "]",
                e);
        }
        if (!invoke) {
            logger.error("AbstractCliviaEvent[invokeEvent] return false,the current cliviaListenerCallable name is ["
                + callable.getListenerName() + "]");
        }
        return invoke;
    }

    /**
     * 定时执行Event事件
     *
     * @author palading_cr
     *
     */
    public <T> void invokSchedulEvent(CliviaListenerCallable callable, T t) {
        try {
            callable.invokeScheduler(t);
        } catch (Exception e) {
            logger.error(
                "AbstractCliviaEvent[invokSchedulEvent] error,the current cliviaListenerCallable name is ["
                    + callable.getListenerName() + "]", e);
        }
    }

}
