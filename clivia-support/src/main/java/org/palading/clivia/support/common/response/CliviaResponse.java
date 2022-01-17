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
package org.palading.clivia.support.common.response;
import org.palading.clivia.support.common.constant.CliviaResponseEnum;

import java.io.Serializable;

/**
 * @author palading_cr
 *
 */
public class CliviaResponse implements Serializable {

    private int resCode;

    private String resMsg;

    private Object resData;

    public CliviaResponse(int resCode, String resMsg, Object resData) {
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.resData = resData;
    }

    public CliviaResponse() {}

    public static CliviaResponse success() {
        return success(null);
    }

    public static CliviaResponse success(Object data) {
        return new CliviaResponse(CliviaResponseEnum.success.getCode(), CliviaResponseEnum.success.getMsg(), data);
    }

    public static CliviaResponse down() {
        return new CliviaResponse(CliviaResponseEnum.down.getCode(), CliviaResponseEnum.down.getMsg(), null);
    }

    public static CliviaResponse error() {
        return new CliviaResponse(CliviaResponseEnum.error.getCode(), CliviaResponseEnum.error.getMsg(), null);
    }

    public static CliviaResponse error_service_not_found() {
        return new CliviaResponse(CliviaResponseEnum.service_not_found.getCode(), CliviaResponseEnum.service_not_found.getMsg(),
            null);
    }

    public static CliviaResponse error_request_limited() {
        return new CliviaResponse(CliviaResponseEnum.error_request_limited.getCode(),
            CliviaResponseEnum.error_request_limited.getMsg(), null);
    }

    public static CliviaResponse error_token_not_exists() {
        return new CliviaResponse(CliviaResponseEnum.error_token_not_exists.getCode(),
            CliviaResponseEnum.error_token_not_exists.getMsg(), null);
    }

    public static CliviaResponse error_token_check_failed() {
        return new CliviaResponse(CliviaResponseEnum.error_token_check_failed.getCode(),
            CliviaResponseEnum.error_token_check_failed.getMsg(), null);
    }

    public static CliviaResponse error_sign_empty() {
        return new CliviaResponse(CliviaResponseEnum.service_sign_empty.getCode(),
            CliviaResponseEnum.service_sign_empty.getMsg(), null);
    }

    public static CliviaResponse error_method_type() {
        return new CliviaResponse(CliviaResponseEnum.service_method_not_support.getCode(),
            CliviaResponseEnum.service_method_not_support.getMsg(), null);
    }

    public static CliviaResponse error_sign_not_pass() {
        return new CliviaResponse(CliviaResponseEnum.service_sign_not_pass.getCode(),
            CliviaResponseEnum.service_sign_not_pass.getMsg(), null);
    }

    public static CliviaResponse error_sign_request_invalid() {
        return new CliviaResponse(CliviaResponseEnum.service_sign_request_invalid.getCode(),
            CliviaResponseEnum.service_sign_request_invalid.getMsg(), null);
    }

    public static CliviaResponse rejected() {
        return new CliviaResponse(CliviaResponseEnum.service_rejected.getCode(), CliviaResponseEnum.service_rejected.getMsg(),
            null);
    }

    public static CliviaResponse fail(int resCode, String resMsg, Object resData) {
        return new CliviaResponse(resCode, resMsg, resData);
    }

    public static CliviaResponse ip_rejected() {
        return new CliviaResponse(CliviaResponseEnum.ip_rejected.getCode(), CliviaResponseEnum.ip_rejected.getMsg(), null);
    }

    public static CliviaResponse invoke_http_fail() {
        return new CliviaResponse(CliviaResponseEnum.service_http_invoke_error.getCode(),
            CliviaResponseEnum.service_http_invoke_error.getMsg(), null);
    }

    public static CliviaResponse invoke_http_router_not_exists() {
        return new CliviaResponse(CliviaResponseEnum.service_http_router_error.getCode(),
                CliviaResponseEnum.service_http_router_error.getMsg(), null);
    }

    public static CliviaResponse invoke_http_upstream_not_exists() {
        return new CliviaResponse(CliviaResponseEnum.service_http_upstream_error.getCode(),
                CliviaResponseEnum.service_http_upstream_error.getMsg(), null);
    }

    public static CliviaResponse invoke_springcloud_fail() {
        return new CliviaResponse(CliviaResponseEnum.service_springcloud_invoke_error.getCode(),
            CliviaResponseEnum.service_springcloud_invoke_error.getMsg(), null);
    }

    public static CliviaResponse invoke_dubbo_fail() {
        return new CliviaResponse(CliviaResponseEnum.service_dubbo_invoke_error.getCode(),
            CliviaResponseEnum.service_dubbo_invoke_error.getMsg(), null);
    }

    public static CliviaResponse no_dubbo_service_error() {
        return new CliviaResponse(CliviaResponseEnum.service_dubbo_not_exists.getCode(),
            CliviaResponseEnum.service_dubbo_not_exists.getMsg(), null);
    }

    public static CliviaResponse error_param_empty() {
        return new CliviaResponse(CliviaResponseEnum.error_param_not_exists.getCode(),
            CliviaResponseEnum.error_param_not_exists.getMsg(), null);
    }

    public static CliviaResponse error_reqSize_max() {
        return new CliviaResponse(CliviaResponseEnum.error_reqSize_max.getCode(), CliviaResponseEnum.error_reqSize_max.getMsg(),
            null);
    }

    public static CliviaResponse error_client_switch() {
        return new CliviaResponse(CliviaResponseEnum.error_client_switch.getCode(),
            CliviaResponseEnum.error_client_switch.getMsg(), null);
    }

    public static CliviaResponse error_appKey_verify() {
        return new CliviaResponse(CliviaResponseEnum.service_appKey_check_error.getCode(),
            CliviaResponseEnum.service_appKey_check_error.getMsg(), null);
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public Object getResData() {
        return resData;
    }

    public void setResData(Object resData) {
        this.resData = resData;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }
}
