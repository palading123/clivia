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
package org.palading.clivia.dynamic;

import org.palading.clivia.cache.api.CliviaCache;
import org.palading.clivia.invoker.api.CliviaInvoker;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author palading_cr
 * @title CliviaDynamicInvokerRegistrationLoader
 * @project clivia
 */
public class CliviaDynamicInvokerRegistrationLoader extends CliviaAbstractDynamicFileRegistrationLoader implements
        CliviaDynamicFileLoad {

    private static final String dynamic_invoke_type = "dynamic-invoke";

    private static final CliviaDynamicInvokerRegistrationLoader cliviaDynamicInvokerRegistrationLoader =
        new CliviaDynamicInvokerRegistrationLoader();

    private static Logger logger = LoggerFactory.getLogger(CliviaDynamicInvokerRegistrationLoader.class);

    private static ConcurrentHashMap<String, Long> cliviaInvokerClassLastModified = new ConcurrentHashMap<String, Long>();

    public static CliviaDynamicInvokerRegistrationLoader getCliviaDynamicInvokerRegistrationLoader() {
        return cliviaDynamicInvokerRegistrationLoader;
    }

    /**
     * load dynamic filter
     *
     * @author palading_cr
     *
     */
    public <T> void registerScheduledDynamicFileManager(T t, CliviaCache cliviaCache, String... directories) throws Exception {
        ApplicationContext applicationContext = (ApplicationContext)t;
        loadDynamicFiles(cliviaDynamicFilter, applicationContext, cliviaCache, directories);
    }

    /*
     * Judge whether the current file has changed according to the file lastModified. If the file has changed, it needs to be reloaded
     *
     * @author palading_cr
     *
     */
    public void loadCliviaDynamicFile(ApplicationContext applicationContext, CliviaCache cliviaCache, File file) throws Exception {
        if (null != file) {
            try {
                String cliviaHasDynamicKey = file.getAbsolutePath() + file.getName();
                if (cliviaInvokerClassLastModified.get(cliviaHasDynamicKey) != null
                    && (file.lastModified() != cliviaInvokerClassLastModified.get(cliviaHasDynamicKey))) {
                    logger.info("reloading invoker:" + cliviaHasDynamicKey);
                    cliviaInvokerClassLastModified.remove(cliviaHasDynamicKey);
                }
                if (null == cliviaInvokerClassLastModified.get(cliviaHasDynamicKey)) {
                    loadCacheByFile(file, cliviaCache, cliviaHasDynamicKey, applicationContext);
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }

    @Override
    protected void loadCacheByFile(File codeFile, CliviaCache cliviaCache, String dynamicKey,
        ApplicationContext applicationContext) throws Exception {
        Class<CliviaInvoker> clazz = cliviaDynamicFileComplier.compile(codeFile);
        if (!Modifier.isAbstract(clazz.getModifiers())) {
            CliviaInvoker cliviaInvoke = (CliviaInvoker)getCliviaFileBySpring(applicationContext, clazz);
            if (null != cliviaInvoke) {
                Map<String, CliviaInvoker> cliviaInvokerMap = (Map)cliviaCache.get(CliviaConstants.invoker_cache);
                cliviaInvokerMap.put(cliviaInvoke.getRpcType(), cliviaInvoke);
                cliviaInvokerClassLastModified.putIfAbsent(dynamicKey, codeFile.lastModified());
            }
        }
    }

    /**
     * manually add the class object to the spring factory and return the instance. throws cliviaException with no
     * annotation of Order
     *
     * @author palading_cr
     */
    public Object getCliviaFileBySpring(ApplicationContext applicationContext, Class clazz) throws Exception {
        registerSpringBeanDefinition(getDynamicType(), applicationContext, clazz);
        return applicationContext.getBean(getDynamicType() + clazz.getName());
    }

    @Override
    protected String getDynamicType() {
        return dynamic_invoke_type;
    }
}
