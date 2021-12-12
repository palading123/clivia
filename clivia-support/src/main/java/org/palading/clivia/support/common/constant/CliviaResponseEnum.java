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
package org.palading.clivia.support.common.constant;

/**
 * @author palading_cr
 * @title CliviaResponseEnum
 * @project clivia
 */
public enum CliviaResponseEnum {

    success(200, "success"), down(100, "service offline"), error(500, "service error,please contact the administrator"), ip_rejected(
        101, "the requst ip has been rejected"), service_not_found(102, "service not found"), service_rejected(103,
        "service rejected,please try it later"), service_sign_empty(104, "service error,request signature is null"), service_sign_request_invalid(
        105, "service error,request invalid"), service_sign_not_pass(106, "service error,request verification failed"), service_method_not_support(
        107, "gateway only supports HTTP get and post requests"), service_http_invoke_error(108,
        "service execution error, http service execution failed"), service_config_httpOrspringcloud_error(109,
        "service execution error,http/springcloud config error"), service_springcloud_serviceId_error(110,
        "service execution error,the serviceId of spring cloud service not found "), service_gateway_config_error(111,
        "service execution error,abnormal configuration of spring cloud environment"), service_springcloud_serviceid_error(112,
        "service execution error,no serviceInstance found"), service_springcloud_invoke_error(113,
        "service execution error, springcloud service execution failed"), service_dubbo_invoke_error(114,
        "service execution error, dubbo service execution failed"), service_dubbo_not_exists(115,
        "service execution error,the dubbo service not exists"), error_token_not_exists(116, "token is not exists"), error_token_check_failed(
        117, "token check failed"), error_param_not_exists(120, "param is not exists"), error_request_limited(123,
        "service limited,please try it later"), error_reqSize_max(125, "the request size is more than the permissible size"), error_client_switch(
        130, "service offline,please try it later"), service_appKey_check_error(130,
        "service rejected, appkey verification failed");
    private int code;

    private String msg;

    CliviaResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
