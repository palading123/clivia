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
package org.palading.clivia.cache;
import org.palading.clivia.cache.api.CliviaCache;

/**
 * @author palading_cr
 * @title CliviaStandandCacheFactory
 * @project clivia
 */
public class CliviaStandandCacheFactory implements CliviaCacheFactory {

    private static CliviaCache cache = new CliviaLocaleCache();

    private static CliviaStandandCacheFactory cliviaStandandCacheFactory = new CliviaStandandCacheFactory();

    public static CliviaStandandCacheFactory getCliviaStandandCacheFactory() {
        return cliviaStandandCacheFactory;
    }

    @Override
    public CliviaCache getCache() {
        return this.cache;
    }

    @Override
    public void loadCache() {

    }

    public String getString(String k) {
        return String.valueOf(get(k));
    }

    public Object get(String k) {
        return cache.get(k);
    }

    public void put(String k, Object v) {
        cache.put(k, v);
    }
    public void putIfAbsent(String k,Object v){
        cache.putIfAbsent(k,v);
    }
}
