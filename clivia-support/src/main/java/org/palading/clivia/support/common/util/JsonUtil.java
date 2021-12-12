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
package org.palading.clivia.support.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        /*		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
         */mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * @author palading_cr
     *
     */
    public static String toJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static <T> T toObject(String json, Class<T> t) {
        try {
            return mapper.readValue(json, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T toComplexMapObject(String json, Class<?> clazz) {
        try {
            TypeFactory t = TypeFactory.defaultInstance();
            return mapper.readValue(json, t.constructMapLikeType(Map.class, String.class, clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> jsonStr2List(String jsonArray, Class<?> clazz) {
        List<T> list = Collections.emptyList();
        try {
            TypeFactory t = TypeFactory.defaultInstance();
            list = mapper.readValue(jsonArray, t.constructCollectionType(ArrayList.class, clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
