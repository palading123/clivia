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
package org.palading.clivia.dbCore;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * config type match condition
 * 
 * @author palading_cr
 * @title CliviaConfigCondition
 * @project clivia
 */
public class CliviaConfigCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String conditionType = context.getEnvironment().getProperty(CliviaConstants.gateway_admin_config_type);
        conditionType = StringUtils.isEmpty(conditionType) ? CliviaConstants.gateway_admin_config_type_db : conditionType;
        return conditionType.equals(CliviaConstants.gateway_admin_config_type_db)
            || conditionType.equals(CliviaConstants.gateway_admin_config_type_web);
    }
}
