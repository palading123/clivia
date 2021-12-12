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
package org.palading.clivia.fileCore;
import org.palading.clivia.config.api.CliviaFileConfigParser;
import org.palading.clivia.support.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * @author palading_cr
 * @title CliviaJsonFileConfigReader
 * @project clivia
 */
public class CliviaJsonFileConfigParser implements CliviaFileConfigParser {

    private static Logger logger = LoggerFactory.getLogger(CliviaJsonFileConfigParser.class);

    /**
     * @author palading_cr
     *
     */
    @Override
    public <T> List<T> readFileContent(File configFile, Class<?> clazz) {
        return JsonUtil.jsonStr2List(getJsonConfig(configFile), clazz);

    }

    /**
     * get json file
     *
     * @author palading_cr
     *
     */
    private String getJsonConfig(File configFile) {
        String jsonStr = "";
        String newContent = "";
        try {
            FileReader fileReader = new FileReader(configFile);
            Reader reader = new InputStreamReader(new FileInputStream(configFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char)ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            newContent = jsonStr.replaceAll("\r\n", "");
        } catch (Exception e) {
            logger.error("CliviaJsonFileConfigReader[getJsonConfig] error", e);
        }
        return newContent;
    }
}
