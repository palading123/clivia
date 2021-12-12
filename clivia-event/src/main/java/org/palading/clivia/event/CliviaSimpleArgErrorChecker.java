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
package org.palading.clivia.event;
import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toList;

/**
 * Check the properties in the checklist and print them to remind the user whether the configuration parameters of the
 * gateway do not exist
 * 
 * @author palading_cr
 * @title DefaultCliviaArgErrorRecorder
 * @project clivia
 */
public class CliviaSimpleArgErrorChecker implements CliviaSystemArgChecker {

    /**
     * property checklist
     *
     * @author palading_cr
     *
     */
    private static final List<String> cliviaArgCheckList = Stream.of("cliviaAdminUrl", "dynamicFilterPath", "dynamicInvokerPath",
        "dynamicFileType", "cliviaClientName", "cliviaClientPwd", "redisType", "redisPassword", "redisUrl").collect(toList());

    private static Logger logger = LoggerFactory.getLogger(CliviaSimpleArgErrorChecker.class);

    private String check_arg_prefix = "clivia.server.config.";

    /**
     * @author palading_cr
     *
     */
    @Override
    public <T> void check(T t) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("****** clivia gateway client arg check record begin ******");
        builder.append("\n");
        formatCheckString(builder, (CliviaServerProperties)t);
        builder.append("****** clivia gateway client arg check record end ******");
        builder.append("\n");
        if (logger.isInfoEnabled()) {
            logger.info(builder.toString());
        }
    }

    private StringBuilder formatCheckString(StringBuilder builder, CliviaServerProperties cliviaServerProperties) {
        for (String checkName : cliviaArgCheckList) {
            builder.append(check_arg_prefix.concat(checkName));
            builder.append(StringUtils.isEmpty(getFieldValueByFieldName(checkName, cliviaServerProperties)) ? ":YES" : ":NONE");
            builder.append("\n");
        }
        return builder;
    }

    private String getFieldValueByFieldName(String fieldName, CliviaServerProperties object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (String)field.get(fieldName);
        } catch (Exception e) {
            return null;
        }
    }

}
