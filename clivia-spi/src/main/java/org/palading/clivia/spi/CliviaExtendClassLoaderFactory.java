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
package org.palading.clivia.spi;

import java.util.List;

/**
 * CliviaExtendClassLoaderFactory
 * 
 * @author palading_cr
 * @title CliviaExtendClassFactory
 * @project clivia
 */
public interface CliviaExtendClassLoaderFactory {

    /**
     * get single extendClass instance. the spiKey is config key of spi configuration file and the type is extend
     * interface
     * 
     * @author palading_cr
     *
     */
    public <T> T getExtendClassInstance(Class<T> type, String spiKey);

    /**
     * get the instance of the collection extension class
     * 
     * @author palading_cr
     *
     */
    public <T> List<T> getExtendClassInstanceList(Class<T> type, String spiKey);

    /**
     * get default classLoader
     *
     * @author palading_cr
     *
     */
    default ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = getClass().getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }
}
