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

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.cache.api.CliviaCache;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.spi.CliviaExtendClassLoader;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Optional;

/**
 * @author palading_cr
 * @title CliviaAbstractDynamicFileRunner
 * @project clivia
 */
public abstract class CliviaAbstractDynamicFileRunner implements CliviaDynamicFileRunner {

    protected abstract String getDynamicFilePath(CliviaServerProperties cliviaServerProperties);

    // protected abstract void dynamicFileLoad(CliviaServerProperties cliviaServerProperties, ApplicationContext
    // applicationContext,
    // CliviaCache cliviaCache, String dynamicPath) throws Exception;
    //
    /**
     * load the dynamic filters
     *
     * @author palading_cr
     *
     */
    protected void dynamicFileLoad(CliviaServerProperties cliviaServerProperties, ApplicationContext applicationContext,
                                   CliviaCache cliviaCache, String dynamicPath) throws Exception {
        loadDyanmic(cliviaServerProperties, applicationContext, cliviaCache, dynamicPath, getCliviaDynamicFileLoad());
    }

    protected abstract CliviaDynamicFileLoad getCliviaDynamicFileLoad();

    /**
     * check file path
     *
     * @author palading_cr /3
     */
    private void cliviaDynamicFileCheck(String dynamicFilePath) throws Exception {
        Optional
            .of(dynamicFilePath)
            .filter(StringUtils::isNotEmpty)
            .orElseThrow(
                () -> new Exception("CliviaAbstractDynamicFileRunner[cliviaDynamicFileCheck] path[" + dynamicFilePath
                    + "] not exists"));
        Optional
            .of(new File(dynamicFilePath.split(",")[0]))
            .filter(File::isDirectory)
            .orElseThrow(
                () -> new Exception("CliviaAbstractDynamicFileRunner[cliviaDynamicFileCheck]dynamicFilePath is not file path"));
    }

    /**
     * load dynamic file
     *
     * @author palading_cr
     *
     */
    protected void loadDyanmic(CliviaServerProperties cliviaServerProperties, ApplicationContext applicationContext,
        CliviaCache cliviaCache, String dynamicPath, CliviaDynamicFileLoad cliviaDynamicFileLoad) throws Exception {
        CliviaDynamicFileFactory cliviaDynamicFileFactory = new CliviaDynamicFileFactory(cliviaServerProperties);
        CliviaDynamicFileLoader cliviaDynamicFileLoader = new CliviaDynamicFileLoader(cliviaDynamicFileLoad);
        cliviaDynamicFileLoader.registerScheduledDynamicFileManager(cliviaDynamicFileFactory, applicationContext, cliviaCache,
            dynamicPath);
    }

    /**
     * @author palading_cr
     *
     */
    @Override
    public <T> void loadDynamicFile(T t, CliviaServerProperties cliviaServerProperties, ApplicationContext applicationContext)
        throws Exception {
        try {
            CliviaCache cliviaCache = (CliviaCache)t;
            String dynamicPath = getDynamicFilePath(cliviaServerProperties);
            cliviaDynamicFileCheck(dynamicPath);
            dynamicFileLoad(cliviaServerProperties, applicationContext, cliviaCache, dynamicPath);
        } catch (Exception e) {
            throw e;
        }
    }

    class CliviaDynamicFileLoader {

        private CliviaDynamicFileLoad cliviaDynamicFileLoad;

        public CliviaDynamicFileLoader(CliviaDynamicFileLoad cliviaDynamicFileLoad) {
            this.cliviaDynamicFileLoad = cliviaDynamicFileLoad;
        }

        public <T> void registerScheduledDynamicFileManager(CliviaDynamicFileFactory cliviaDynamicFileFactory, T t,
            CliviaCache cliviaCache, String... directories) throws Exception {
            cliviaDynamicFileLoad.setFilenameFilter(cliviaDynamicFileFactory.getFilenameFilter());
            cliviaDynamicFileLoad.setCliviaDynamicFileComplier(cliviaDynamicFileFactory.getCliviaDynamicFileComplier());
            cliviaDynamicFileLoad.registerScheduledDynamicFileManager(t, cliviaCache, directories);
        }

    }

    class CliviaDynamicFileFactory {
        private static final String cliviaGroovyFilterType = "groovy";
        private FilenameFilter filenameFilter;
        private CliviaDynamicFileComplier cliviaDynamicFileComplier;

        public CliviaDynamicFileFactory(CliviaServerProperties cliviaServerProperties) {
            String cliviaDynamicFileType =
                Optional.ofNullable(cliviaServerProperties.getDynamicFileType()).orElse(cliviaGroovyFilterType);
            if (cliviaDynamicFileType.equals(cliviaGroovyFilterType)) {
                this.filenameFilter = new CliviaGroovyCheckFilter();
            }
            this.cliviaDynamicFileComplier = getCliviaDynamicFileComplier(cliviaDynamicFileType);
        }

        private CliviaDynamicFileComplier getCliviaDynamicFileComplier(String cliviaDynamicFileType) {
            return CliviaExtendClassLoader.getCliviaExtendClassLoaderInstance().getExtendClassInstance(
                CliviaDynamicFileComplier.class, cliviaDynamicFileType);
        }

        public FilenameFilter getFilenameFilter() {
            return filenameFilter;
        }

        public CliviaDynamicFileComplier getCliviaDynamicFileComplier() {
            return cliviaDynamicFileComplier;
        }
    }

}
