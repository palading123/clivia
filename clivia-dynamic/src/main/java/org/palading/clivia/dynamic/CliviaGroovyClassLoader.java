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

/**
 * @author palading_cr
 * @title CliviaGroovyClassLoader
 * @project clivia /19
 */
public class CliviaGroovyClassLoader implements CliviaDyanmicClassLoader {

    private static CliviaGroovyClassLoader cliviaGroovyClassLoader = null;;

    private CliviaGroovyClassLoader() {}

    public static CliviaGroovyClassLoader getCliviaGroovyClassLoader() {
        if (null == cliviaGroovyClassLoader) {
            return new CliviaGroovyClassLoader();
        }
        return cliviaGroovyClassLoader;
    }

    @Override
    public <T extends ClassLoader> T getClassLoader(Class<T> tClass) throws Exception {
        return tClass.newInstance();
    }
}
