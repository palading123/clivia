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
package org.palading.clivia.invoke.alibabadubbo.refrence;


import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.support.common.domain.ApiNonHttpRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author palading_cr
 * @title CliviaRefrenceConfig
 * @project clivia
 */
public class CliviaDubboGenericService {

    private static Logger logger = LoggerFactory.getLogger(CliviaDubboGenericService.class);

    private static CliviaDubboGenericService cliviaDubboGenericServiceInstance = new CliviaDubboGenericService();

    public static CliviaDubboGenericService getCliviaDubboGenericServiceInstance() {
        return cliviaDubboGenericServiceInstance;
    }

    public GenericService getGenericService(ApiNonHttpRoute apiNonHttpRoute) {
        ApplicationConfig applicationConfig = new ApplicationConfig("clivia-apache-dubbo");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig = new RegistryConfig();
        registryConfig.setProtocol(StringUtils.isNotEmpty(apiNonHttpRoute.getProtocol()) ? apiNonHttpRoute.getProtocol()
            : "dubbo");
        registryConfig.setAddress(apiNonHttpRoute.getRegistry());
        registryConfig.setCheck(false);
        registryConfig.setGroup(apiNonHttpRoute.getGroupName());
        registryConfig.setVersion(apiNonHttpRoute.getVersionName());
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setGeneric("true");
        reference.setRegistry(registryConfig);
        reference.setInterface(apiNonHttpRoute.getServiceName());
        reference.setRetries(apiNonHttpRoute.getRetryTimes());
        logger.info("CliviaRefrenceConfig[getGenericService] request:grpup[" + apiNonHttpRoute.getGroupName() + "],serviceName["
            + apiNonHttpRoute.getServiceName() + ",methdoName[" + apiNonHttpRoute.getMethodName() + "]]");
        // DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        // bootstrap.application(applicationConfig).registry(registryConfig).reference(reference).start();
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);
        if (genericService == null) {
            cache.destroy(reference);
            logger.error("CliviaRefrenceConfig[getGenericService]  dubbo service not found");
            return null;
        }
        return genericService;
    }
}
