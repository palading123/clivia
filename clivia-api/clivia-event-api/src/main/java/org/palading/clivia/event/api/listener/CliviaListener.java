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

import org.palading.clivia.common.api.CliviaServerProperties;
import org.springframework.context.ApplicationContext;

/**
 * @author palading_cr
 * @title CliviaListener
 * @project clivia
 */
public interface CliviaListener {

    /**
     * CliviaListener排序
     *
     * @author palading_cr
     *
     */
    int getOrder();

    /**
     * 获取CliviaListener 类名
     *
     * @author palading_cr
     *
     */
    default String getListenerName() {
        return getClass().getSimpleName();
    }

    /**
     * 获取CliviaServerProperties
     *
     * @author palading_cr
     *
     */
    default CliviaServerProperties getCliviaServerProperties(ApplicationContext applicationContext) throws Exception {
        CliviaServerProperties cliviaServerProperties =
            applicationContext.getBean("cliviaServerProperties", CliviaServerProperties.class);
        return cliviaServerProperties;
    }
}
