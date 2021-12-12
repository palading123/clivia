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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * clivia spi loader extendClass cache, the key is ClassType name,the inner key is spikey. one spikey can be more than
 * one implementation class
 * 
 * @author palading_cr
 * @title CliviaExtendClassLoader
 * @project clivia
 */
public class CliviaExtendClassLoader implements CliviaExtendClassLoaderFactory {

    private static ConcurrentHashMap<String, Map<String, List<?>>> extendClassInstances = new ConcurrentHashMap<>();

    private static CliviaExtendClassLoader cliviaExtendClassLoaderInstance = new CliviaExtendClassLoader();

    public static CliviaExtendClassLoader getCliviaExtendClassLoaderInstance() {
        return cliviaExtendClassLoaderInstance;
    }

    @Override
    public <T> List<T> getExtendClassInstanceList(Class<T> type, String spiKey) {
        try {
            if (null == type) {
                throw new Exception("CliviaExtendClassLoader[getExtendClassInstanceList] loadbalance type is null");
            }
            if (StringUtils.isEmpty(spiKey)) {
                throw new Exception("CliviaExtendClassLoader[getExtendClassInstanceList] spiKey is null");

            }
            Map<String, List<?>> extendInstanseMap = extendClassInstances.get(type.getName());
            if (null != extendInstanseMap && null != extendInstanseMap.get(spiKey)) {
                return (List<T>)extendInstanseMap.get(spiKey);
            }
            List<T> instances = doCreateInstance(type, getDefaultClassLoader(), spiKey);
            return instances;
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public <T> T getExtendClassInstance(Class<T> type, String spiKey) {
        return getExtendClassInstance(type, spiKey, 0);
    }

    private <T> T getExtendClassInstance(Class<T> type, String spiKey, int model) {
        return getExtendClassInstanceList(type, spiKey).get(model);
    }

    private synchronized <T> List<T> doCreateInstance(Class<T> type, ClassLoader classLoader, String spiKey) throws Exception {
        Map<String, List<String>> preparedInstance = CliviaClassFactoryLoader.loadExtendClassFactories(type, classLoader);
        List<T> instances = doCreateInstance(preparedInstance.get(spiKey));
        Map<String, List<?>> instanceMap = new HashMap<>();
        instanceMap.put(spiKey, instances);
        extendClassInstances.put(type.getName(), instanceMap);
        return instances;
    }

    private <T> List<T> doCreateInstance(List<String> names) throws Exception {
        List<T> instances = new ArrayList<>(names.size());
        for (String name : names) {
            try {
                Class<?> instanceClass = Class.forName(name);
                T instance = (T)instanceClass.newInstance();
                instances.add(instance);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Cannot instantiate :" + name, ex);
            }
        }
        return instances;
    }
}
