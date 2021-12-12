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
import org.palading.clivia.fileCore.domain.CliviaFileClientSecurity;
import org.palading.clivia.spi.CliviaExtendClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author palading_cr
 * @title CliviaFileConfigCommonSerice
 * @project clivia
 */
public class CliviaFileConfigCommonService {

    private static Logger logger = LoggerFactory.getLogger(CliviaFileConfigCommonService.class);

    private volatile static CliviaFileConfigParser cliviaFileConfigParser;

    @Value("${clivia.admin.file.config.type:json}")
    private String cliviaAdminFileConfigType;

    /**
     * @author palading_cr
     *
     */
    protected <T> List<T> readCliviaCacheListFromFile(File configFile, Class<?> clazz) {
        CliviaFileConfigParser cliviaFileConfigParser = instanceCliviaFileConfigParser();
        return cliviaFileConfigParser.readFileContent(configFile, clazz);
    }

    /**
     * @author palading_cr
     *
     */
    protected int getTokenCount(String token, File configFile) {
        int count = 0;
        List<CliviaFileClientSecurity> tblClientSecurityList =
            readCliviaCacheListFromFile(configFile, CliviaFileClientSecurity.class);
        for (CliviaFileClientSecurity tblClientSecurity : tblClientSecurityList) {
            if (tblClientSecurity.getToken().equals(token)) {
                count++;
            }
        }
        return count;
    }

    /**
     * instance cliviaFileConfigParser by spi
     *
     * @author palading_cr
     *
     */
    protected CliviaFileConfigParser instanceCliviaFileConfigParser() {
        if (null == cliviaFileConfigParser) {
            synchronized (CliviaFileConfigCommonService.class) {
                if (null == cliviaFileConfigParser) {
                    cliviaFileConfigParser =
                        CliviaExtendClassLoader.getCliviaExtendClassLoaderInstance().getExtendClassInstance(
                            CliviaFileConfigParser.class, cliviaAdminFileConfigType);
                }

            }
        }
        return cliviaFileConfigParser;
    }

    /**
     * get client state
     *
     * @author palading_cr
     *
     */
    protected String getClientState(String token, File configFile) {
        LinkedList<String> linkedList = new LinkedList<>();
        List<CliviaFileClientSecurity> cliviaFileClientSecurityList =
            readCliviaCacheListFromFile(configFile, CliviaFileClientSecurity.class);
        for (CliviaFileClientSecurity cliviaFileClientSecurity : cliviaFileClientSecurityList) {
            if (cliviaFileClientSecurity.getToken().equals(token)) {
                linkedList.add(cliviaFileClientSecurity.getState());
            }
        }
        if (linkedList.size() == 0) {
            logger.error("CliviaFileConfigCommonSerice[getTokenState] error,fileName[" + configFile.getName() + "],token["
                + token + "] no data found");
        }
        if (linkedList.size() > 1) {
            logger.error("CliviaFileConfigCommonSerice[getTokenState] error,:fileName[" + configFile.getName() + "],token["
                + token + "] duplicate data found");
        }
        return linkedList.getFirst();
    }

    /**
     * get token by client and clientPwd
     * 
     * @author palading_cr
     *
     */
    protected String getToken(String clientName, String clientPwd, File configFile) {
        List<CliviaFileClientSecurity> cliviaFileClientSecurityList =
            readCliviaCacheListFromFile(configFile, CliviaFileClientSecurity.class);
        String token = "";
        for (CliviaFileClientSecurity cliviaFileClientSecurity : cliviaFileClientSecurityList) {
            if ("1".equals(cliviaFileClientSecurity.getState())) {
                continue;
            }
            if (clientName.equals(cliviaFileClientSecurity.getClientName())
                && clientPwd.equals(cliviaFileClientSecurity.getClientPwd())) {
                token = cliviaFileClientSecurity.getToken();
                break;
            }
        }
        return token;
    }
}
