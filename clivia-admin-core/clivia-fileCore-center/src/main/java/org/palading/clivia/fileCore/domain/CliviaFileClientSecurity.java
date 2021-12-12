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
package org.palading.clivia.fileCore.domain;

import org.palading.clivia.support.common.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class CliviaFileClientSecurity {

    private String id;

    private String clientName;
    private String clientPwd;

    private String token;

    private String state;

    public static void main(String[] args) {
        List<CliviaFileClientSecurity> tblClientSecurityList = new ArrayList<>();
        CliviaFileClientSecurity tblClientSecurity = new CliviaFileClientSecurity();
        tblClientSecurity.setId("1");
        tblClientSecurity.setClientName("clivia_mode1");
        tblClientSecurity.setClientPwd("asdasdasdasdasdasd");
        tblClientSecurity.setToken("asdasdase2qweqw");
        tblClientSecurity.setState("0");
        CliviaFileClientSecurity tblClientSecurity2 = new CliviaFileClientSecurity();
        tblClientSecurity2.setId("1");
        tblClientSecurity2.setClientName("clivia_mode1");
        tblClientSecurity2.setClientPwd("asdasdasdasdasdasd");
        tblClientSecurity2.setToken("asdasdase2qweqw");
        tblClientSecurity2.setState("0");
        tblClientSecurityList.add(tblClientSecurity);
        tblClientSecurityList.add(tblClientSecurity2);
        System.out.println(JsonUtil.toJson(tblClientSecurityList));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPwd() {
        return clientPwd;
    }

    public void setClientPwd(String clientPwd) {
        this.clientPwd = clientPwd;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}