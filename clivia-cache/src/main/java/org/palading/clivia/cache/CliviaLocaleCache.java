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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CliviaLocaleCache<K, V> implements CliviaCache<K, V> {

    private Map<K, V> CACHES = new ConcurrentHashMap<>();

    public void put(K k, V v) {
        CACHES.put(k, v);
    }

    public V get(K k) {
        return CACHES.get(k);
    }

    public void clear() {
        CACHES.clear();
    }

    @Override
    public boolean isEmpty() {
        return CACHES.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return CACHES.keySet();
    }

    @Override
    public void remove(K k) {
        CACHES.remove(k);
    }

    @Override
    public Object get() {
        return CACHES;
    }

    @Override
    public V getDefault(K k, V v1) {
        V v = CACHES.get(k);
        return null == v ? v1 : v;
    }

    @Override
    public int size() {
        return CACHES.size();
    }

    @Override
    public void putIfAbsent(K k, V v) {
        CACHES.putIfAbsent(k,v);
    }
}
