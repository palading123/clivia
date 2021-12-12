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
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Load the dynamic filter under the file address and add the filter to the cache. Currently, only one type of dynamic
 * file filter is supported.
 *
 * @author palading_cr
 * @title CliviaGroovyFileLoader
 * @project clivia
 *
 */
public class CliviaDynamicFilterRegistrationLoader extends CliviaAbstractDynamicFileRegistrationLoader implements
    CliviaDynamicFileLoad {

    private static final String dynamic_filter_type = "dynamic-filter";

    private static final CliviaDynamicFilterRegistrationLoader cliviaFilterFileRegistrationLoader =
        new CliviaDynamicFilterRegistrationLoader();

    private static Logger logger = LoggerFactory.getLogger(CliviaDynamicFilterRegistrationLoader.class);

    private static ConcurrentHashMap<String, Long> cliviafilterClassLastModified = new ConcurrentHashMap<String, Long>();

    public static CliviaDynamicFilterRegistrationLoader getCliviaFilterFileRegistrationLoader() {
        return cliviaFilterFileRegistrationLoader;
    }

    /**
     * load dynamic filter
     *
     * @author palading_cr
     *
     */
    @Override
    public <T> void registerScheduledDynamicFileManager(T t, CliviaCache cliviaCache, String... directories) throws Exception {
        ApplicationContext applicationContext = (ApplicationContext)t;
        loadDynamicFiles(cliviaDynamicFilter, applicationContext, cliviaCache, directories);
    }

    /**
     * loadCacheByFile
     *
     * @author palading_cr
     *
     */
    @Override
    protected void loadCacheByFile(File codeFile, CliviaCache cliviaCache, String dynamicKey,
        ApplicationContext applicationContext) throws Exception {
        Class<CliviaFilter> clazz = cliviaDynamicFileComplier.compile(codeFile);
        if (!Modifier.isAbstract(clazz.getModifiers())) {
            CliviaFilter cliviaFilter = (CliviaFilter)getCliviaFileBySpring(applicationContext, clazz);
            if (null != cliviaFilter) {
                List<CliviaFilter> cliviaFilters = (List)cliviaCache.get(CliviaConstants.filter_cache);
                List<CliviaFilter> copyList = new ArrayList<>(cliviaFilters);
                copyList.add(cliviaFilter);
                copyList = copyList.stream().sorted(Comparator.comparing(e -> e.getOrder())).collect(Collectors.toList());
                cliviaFilters = copyList;
                cliviaCache.put(CliviaConstants.filter_cache, cliviaFilters);
                copyList = null;
                cliviafilterClassLastModified.putIfAbsent(dynamicKey, codeFile.lastModified());
            }
        }
    }

    /*
    * Judge whether the current file has changed according to the file lastModified. If the file has changed, it needs to be reloaded
    *
      * @author palading_cr
     *
     */
    @Override
    public void loadCliviaDynamicFile(ApplicationContext applicationContext, CliviaCache cliviaCache, File file) throws Exception {
        if (null != file) {
            try {
                String cliviaHasDynamicFilterKey = file.getAbsolutePath() + file.getName();
                if (cliviafilterClassLastModified.get(cliviaHasDynamicFilterKey) != null
                    && (file.lastModified() != cliviafilterClassLastModified.get(cliviaHasDynamicFilterKey))) {
                    logger.info("reloading filter:" + cliviaHasDynamicFilterKey);
                    cliviafilterClassLastModified.remove(cliviaHasDynamicFilterKey);
                }
                if (null == cliviafilterClassLastModified.get(cliviaHasDynamicFilterKey)) {
                    loadCacheByFile(file, cliviaCache, cliviaHasDynamicFilterKey, applicationContext);
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * manually add the class object to the spring factory and return the instance. throws cliviaException with no
     * annotation of Order
     *
     * @author palading_cr
     */
    @Override
    public Object getCliviaFileBySpring(ApplicationContext applicationContext, Class clazz) throws Exception {
        registerSpringBeanDefinition(getDynamicType(), applicationContext, clazz);
        return applicationContext.getBean(getDynamicType() + clazz.getName());
    }

    @Override
    protected String getDynamicType() {
        return dynamic_filter_type;
    }

}
