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
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author palading_cr
 * @title CliviaAbstractDynamicFileRegistrationLoader
 * @project clivia
 */
public abstract class CliviaAbstractDynamicFileRegistrationLoader implements CliviaDynamicFileLoad {

    public static CliviaDynamicFileComplier cliviaDynamicFileComplier;

    public static FilenameFilter cliviaDynamicFilter;

    /**
     * load dynamic file
     *
     * @author palading_cr
     *
     */
    protected abstract void loadCliviaDynamicFile(ApplicationContext applicationContext, CliviaCache cliviaCache, File file)
        throws Exception;

    /**
     * add dynamic object to spring facatory
     *
     * @author palading_cr
     *
     */
    protected abstract Object getCliviaFileBySpring(ApplicationContext applicationContext, Class clazz) throws Exception;

    /**
     * dynamic type
     *
     * @author palading_cr
     *
     */
    protected abstract String getDynamicType();

    protected abstract void loadCacheByFile(File codeFile, CliviaCache cliviaCache, String dynamicKey,
        ApplicationContext applicationContext) throws Exception;

    /**
     * register bean
     * 
     * @author palading_cr
     *
     */
    protected void registerSpringBeanDefinition(String dynamicTypeName, ApplicationContext applicationContext, Class clazz) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        BeanDefinition beanDefinitionFilter = null;
        try {
            beanDefinitionFilter = beanFactory.getBeanDefinition(dynamicTypeName + clazz.getName());
        } catch (NoSuchBeanDefinitionException e) {
        }
        if (null != beanDefinitionFilter) {
            beanFactory.destroySingleton(dynamicTypeName + clazz.getName());
        }
        beanFactory.registerBeanDefinition(dynamicTypeName + clazz.getName(), buildGenericBeanDefinition(clazz));
    }

    private GenericBeanDefinition buildGenericBeanDefinition(Class clazz) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setScope("singleton");
        beanDefinition.setLazyInit(false);
        beanDefinition.setAutowireCandidate(true);
        return beanDefinition;
    }

    /**
     * load dynamic files
     * 
     * @author palading_cr
     *
     */
    public void loadDynamicFiles(FilenameFilter filenameFilter, ApplicationContext applicationContext, CliviaCache cache,
        String... directories) throws Exception {
        List<File> aFiles = getCliviaDynamicFiles(filenameFilter, directories);
        loadDynamicFiles(applicationContext, cache, aFiles);
    }

    /**
     * @author palading_cr
     *
     */
    private void loadDynamicFiles(ApplicationContext applicationContext, CliviaCache cliviaCache, List<File> cliviaDynamicFiles)
        throws Exception {
        for (File dynamicFile : cliviaDynamicFiles) {
            loadCliviaDynamicFile(applicationContext, cliviaCache, dynamicFile);
        }
    }

    /**
     * @author palading_cr
     *
     */
    private File getDirectory(String sPath) {
        File directory = new File(sPath);
        if (!directory.isDirectory()) {
            URL resource = CliviaAbstractDynamicFileRunner.CliviaDynamicFileLoader.class.getClassLoader().getResource(sPath);
            try {
                directory = new File(resource.toURI());
            } catch (Exception e) {
            }
            if (!directory.isDirectory()) {
                throw new RuntimeException(directory.getAbsolutePath() + " is not a valid directory");
            }
        }
        return directory;
    }

    /**
     * @author palading_cr
     *
     */
    private List<File> getCliviaDynamicFiles(FilenameFilter filenameFilter, String... aDirectories) {
        List<File> cliviaDynamicFile = new ArrayList<File>();
        for (String sDirectory : aDirectories) {
            if (sDirectory != null) {
                File directory = getDirectory(sDirectory);

                File[] aFiles = null != filenameFilter ? directory.listFiles(filenameFilter) : directory.listFiles();
                if (aFiles != null && aFiles.length > 0) {
                    cliviaDynamicFile.addAll(Arrays.asList(aFiles));
                }
            }
        }
        return cliviaDynamicFile;
    }

    public <T extends FilenameFilter> void setFilenameFilter(T t) {
        cliviaDynamicFilter = t;
    }

    public <T extends CliviaDynamicFileComplier> void setCliviaDynamicFileComplier(T t) {
        cliviaDynamicFileComplier = t;
    }
}
