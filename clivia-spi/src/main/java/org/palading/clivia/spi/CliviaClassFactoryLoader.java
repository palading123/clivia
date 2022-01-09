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

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author palading_cr
 * @title CliviaClassFactoryLoader
 * @project clivia
 */
public final class CliviaClassFactoryLoader {

    public static final String SPI_RESOURCE_LOCATION = "META-INF/clivia/";

    private static final String[] EMPTY_STRING_ARRAY = {};

    private static final ConcurrentHashMap<String, Map<String, List<String>>> cache = new ConcurrentHashMap<>();

    public static Map<String, List<String>> loadExtendClassFactories(Class clazz, ClassLoader classLoader) {
        Map<String, List<String>> result = cache.get(clazz.getName());
        if (result != null) {
            return result;
        }
        try {
            Enumeration<URL> urls = classLoader.getResources(SPI_RESOURCE_LOCATION + clazz.getName());
            result = new LinkedHashMap<>();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = new Properties();
                properties.load(url.openStream());
                for (Map.Entry<?, ?> entry : properties.entrySet()) {
                    String spiKey = ((String)entry.getKey()).trim();
                    String[] delimitedArray;
                    if (((String)entry.getValue()).contains(",")) {
                        delimitedArray = delimitedListToStringArray((String)entry.getValue(), ",");
                    } else {
                        delimitedArray = new String[] {(String)entry.getValue()};
                    }
                    result.put(spiKey, Arrays.asList(delimitedArray));
                }
            }
            cache.put(clazz.getName(), result);
            return result;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load spi class from location [" + SPI_RESOURCE_LOCATION + "]", ex);
        }
    }

    public static String[] delimitedListToStringArray(String str, String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
        if (str == null) {
            return EMPTY_STRING_ARRAY;
        }
        if (delimiter == null) {
            return new String[] {str};
        }
        List<String> result = new ArrayList<>();
        if (delimiter.isEmpty()) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int pos = 0;
            int delPos;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                // Add rest of String, but not in case of empty input.
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }

    /**
     * Delete any character in a given {@code String}.
     * 
     * @param inString
     *            the original {@code String}
     * @param charsToDelete
     *            a set of characters to delete. E.g. "az\n" will delete 'a's, 'z's and new lines.
     * @return the resulting {@code String}
     */
    public static String deleteAny(String inString, String charsToDelete) {
        if (!StringUtils.isEmpty(inString) || !StringUtils.isEmpty(charsToDelete)) {
            return inString;
        }
        StringBuilder sb = new StringBuilder(inString.length());
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String[] toStringArray(Collection<String> collection) {
        return (null != collection && collection.size() > 0 ? collection.toArray(EMPTY_STRING_ARRAY) : EMPTY_STRING_ARRAY);
    }

}
