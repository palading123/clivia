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

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.event.api.listener.CliviaListenerCallable;
import org.palading.clivia.spi.CliviaExtendClassLoader;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;

/**
 * when the spring environment is loaded successfully, the class loading system is called to cache data. After the data
 * of each event is loaded successfully, the scheduled data refresh is performed. If an event is not executed
 * successfully, the gateway will exit the loading logic and need to check the parameters. When the gateway becomes a
 * private gateway component of multiple projects, it is recommended to customize the system parameter check logic
 * through SPI. Generally, the user-defined logo appears on the interface, indicating that there is no exception in the
 * system cache loading
 * 
 * @author palading_cr
 * @title CliviaCacheInitializingBean
 * @project clivia /21
 */
public class CliviaEventLoadlistener extends AbstractCliviaEvent implements ApplicationListener<ContextRefreshedEvent>,
    InitializingBean {

    private final List<CliviaListenerCallable> cliviaListenerList;
    private CliviaSystemChecker cliviaSystemChecker;
    private CliviaSystemArgChecker cliviaSystemArgChecker;
    private CliviaServerProperties cliviaServerProperties;

    public CliviaEventLoadlistener(final List<CliviaListenerCallable> cliviaListenerList,
        CliviaServerProperties cliviaServerProperties) {
        this.cliviaListenerList = cliviaListenerList;
        this.cliviaServerProperties = cliviaServerProperties;
        cliviaSystemChecker = new DefaultCliviaLoadErrorChecker();
        cliviaSystemArgChecker = cliviaSystemChecker.buildErrorChecker();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cliviaSystemArgChecker.check(cliviaServerProperties);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        invokeEvent(cliviaListenerList, contextRefreshedEvent.getApplicationContext());
    }

    /**
     * Execute the custom event call. If the first invokEevent method return false, the loop will not be executed
     * .so,you must check your code
     *
     * @author palading_cr
     *
     */
    public void invokeEvent(final List<CliviaListenerCallable> cliviaListenerList, ApplicationContext applicationContext) {
        for (CliviaListenerCallable cliviaListenerCallable : cliviaListenerList) {
            if (!invokeEvent(cliviaListenerCallable, applicationContext)) {
                break;
            } else {
                invokSchedulEvent(cliviaListenerCallable, applicationContext);
            }
        }
    }

    /**
     * load CliviaSystemArgChecker by SPI
     *
     * @author palading_cr
     *
     */
    class DefaultCliviaLoadErrorChecker implements CliviaSystemChecker {

        @Override
        public CliviaSystemArgChecker buildErrorChecker() {
            return cliviaSystemArgChecker(cliviaServerProperties.getCheckType(), CliviaConstants.clivia_check_model_record);
        }

        @Override
        public <T> void checkError(CliviaSystemArgChecker cliviaSystemArgChecker, T t) {
            cliviaSystemArgChecker.check(t);
        }

        private CliviaSystemArgChecker cliviaSystemArgChecker(String checkType, String defaultCheckType) {
            String checkRealType = StringUtils.isNotEmpty(checkType) ? checkType : defaultCheckType;
            return cliviaSystemArgChecker(checkRealType);
        }

        private CliviaSystemArgChecker cliviaSystemArgChecker(String checkType) {
            return CliviaExtendClassLoader.getCliviaExtendClassLoaderInstance().getExtendClassInstance(
                CliviaSystemArgChecker.class, checkType);
        }

    }
}
