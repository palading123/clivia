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
package org.palading.clivia.invoke.apachedubbo.refrence;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.palading.clivia.cache.CliviaLocalGuavaCacheFactory;
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

    private static CliviaLocalGuavaCacheFactory cliviaLocalGuavaCacheFactory = CliviaLocalGuavaCacheFactory
        .getCliviaStandandCacheFactory();

    public static CliviaDubboGenericService getCliviaDubboGenericServiceInstance() {
        return cliviaDubboGenericServiceInstance;
    }

    public GenericService getGenericService(ApiNonHttpRoute apiNonHttpRoute) {
        ApplicationConfig applicationConfig = new ApplicationConfig("clivia-apache-dubbo");
        RegistryConfig registryConfig =
            buildRegistryConfig(StringUtils.isNotEmpty(apiNonHttpRoute.getProtocol()) ? apiNonHttpRoute.getProtocol() : "dubbo",
                apiNonHttpRoute.getRegistry(), false, apiNonHttpRoute.getGroupName(), apiNonHttpRoute.getVersionName());
        ReferenceConfig<GenericService> reference =
            buildReferenceConfig(true, registryConfig, apiNonHttpRoute.getServiceName(), apiNonHttpRoute.getRetryTimes());
        logger.info("CliviaRefrenceConfig[getGenericService] request:grpup[" + apiNonHttpRoute.getGroupName() + "],serviceName["
            + apiNonHttpRoute.getServiceName() + ",methdoName[" + apiNonHttpRoute.getMethodName() + "]]");
        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(applicationConfig).registry(registryConfig).reference(reference).start();
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);
        if (genericService == null) {
            cache.destroy(reference);
            logger.error("CliviaRefrenceConfig[getGenericService]  dubbo service not found");
            return null;
        }
        return genericService;
    }

    private ReferenceConfig<GenericService> buildReferenceConfig(boolean generic, RegistryConfig registryConfig,
        String interfaceName, int retries) {
        String cacheKey = "dubbo".concat(interfaceName).concat(registryConfig.getAddress());
        ReferenceConfig<GenericService> referenceConfigCache =
            (ReferenceConfig)cliviaLocalGuavaCacheFactory.getCache().get(cacheKey);
        if (null != referenceConfigCache) {
            return referenceConfigCache;
        }
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setGeneric(String.valueOf(generic));
        reference.setRegistry(registryConfig);
        reference.setInterface(interfaceName);
        reference.setRetries(retries);
        cliviaLocalGuavaCacheFactory.getCache().put(
            "dubboReferenceConfig".concat(interfaceName).concat(registryConfig.getAddress()), reference);
        return reference;
    }

    private RegistryConfig buildRegistryConfig(String protocol, String registryAddress, boolean check, String group,
        String version) {
        String cacheKey = "dubbo".concat(protocol).concat(registryAddress).concat(group).concat(version);
        RegistryConfig registryConfigCache = (RegistryConfig)cliviaLocalGuavaCacheFactory.getCache().get(cacheKey);
        if (null != registryConfigCache) {
            return registryConfigCache;
        }
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol(protocol);
        registryConfig.setAddress(registryAddress);
        registryConfig.setCheck(check);
        registryConfig.setGroup(group);
        registryConfig.setVersion(version);
        cliviaLocalGuavaCacheFactory.getCache().put(
            "dubboRegistryConfig".concat(protocol).concat(registryAddress).concat(group).concat(version), registryConfig);
        return registryConfig;

    }
}
