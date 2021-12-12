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

public class CliviaFileBlacklistInfo {

    private String blacklistId;

    private String blacklistIp;

    private String state;

    private String groupId;

    public static void main(String[] args) {
        List<CliviaFileBlacklistInfo> tblBlacklistInfos = new ArrayList<>();
        CliviaFileBlacklistInfo tblBlacklistInfo = new CliviaFileBlacklistInfo();
        tblBlacklistInfo.setBlacklistId("1");
        tblBlacklistInfo.setBlacklistIp("172.10.100.*");
        tblBlacklistInfo.setGroupId("1");
        tblBlacklistInfo.setState("0");
        CliviaFileBlacklistInfo tblBlacklistInfo2 = new CliviaFileBlacklistInfo();
        tblBlacklistInfo2.setBlacklistId("2");
        tblBlacklistInfo2.setBlacklistIp("179.10.100.255");
        tblBlacklistInfo2.setGroupId("1");
        tblBlacklistInfo2.setState("0");
        tblBlacklistInfos.add(tblBlacklistInfo);
        tblBlacklistInfos.add(tblBlacklistInfo2);
        System.out.println(JsonUtil.toJson(tblBlacklistInfos));
    }

    public String getBlacklistId() {
        return blacklistId;
    }

    public void setBlacklistId(String blacklistId) {
        this.blacklistId = blacklistId;
    }

    public String getBlacklistIp() {
        return blacklistIp;
    }

    public void setBlacklistIp(String blacklistIp) {
        this.blacklistIp = blacklistIp;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}