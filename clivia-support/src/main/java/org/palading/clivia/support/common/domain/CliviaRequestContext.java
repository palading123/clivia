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
package org.palading.clivia.support.common.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author palading_cr
 * @title CliviaRequestContext
 * @project clivia
 */
public class CliviaRequestContext implements Serializable {

    private String group;

    private String path;

    private String version;

    private LocalDateTime requestTime;

    private String sign;

    private String requestParam;

    private ApiDetail appInfo;

    private String requestId;

    private String client;

    private String rewritePath;

    private String appKey;

    private String rpcType;
    private String nonce;

    public CliviaRequestContext(String group,String version,String path){
        this.path = path;
        this.group = group;
        this.version = version;
    }
    public String getNonce() {
        return nonce;
    }

    public CliviaRequestContext nonce(String nonce) {
        this.nonce = nonce;
        return this;
    }

    public String getAppKey() {
        return appKey;
    }

    public CliviaRequestContext appKey(String appKey) {
        this.appKey = appKey;
        return this;
    }

    public String getRewritePath() {
        return rewritePath;
    }

    public void setRewritePath(String rewritePath) {
        this.rewritePath = rewritePath;
    }

    public String getClient() {
        return client;
    }

    public CliviaRequestContext client(String client) {
        this.client = client;
        return this;
    }

    public CliviaRequestContext rpcType(String rpcType) {
        this.rpcType = rpcType;
        return this;
    }

    public String getRpcType() {
        return rpcType;
    }

    public CliviaRequestContext requestParam(String param) {
        this.requestParam = param;
        return this;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public String getPath() {
        return path;
    }

    public CliviaRequestContext path(String path) {
        this.path = path;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public CliviaRequestContext group(String group) {
        this.group = group;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public CliviaRequestContext version(String version) {
        this.version = version;
        return this;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public CliviaRequestContext requestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
        return this;
    }

    public ApiDetail getAppInfo() {
        if (null == appInfo) {
            return null;
        }
        return appInfo;
    }

    public CliviaRequestContext appInfo(ApiDetail appInfo) {
        this.appInfo = appInfo;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public CliviaRequestContext requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }


    public String getCacheKey() {
        return group.concat("@").concat("@").concat(version).concat(path);
    }

    public String getOriginSignParam() {
        return getCacheKey().concat("@").concat(requestId);
    }

    public String getSign() {
        return sign;
    }

    public CliviaRequestContext sign(String sign) {
        this.sign = sign;
        return this;
    }
}
