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

/** @ClassName CliviaConstants @Description TODO @Author palading_cr @Version 1.0 */
public class CliviaConstants {
    public static final String X_FORWARDED_FOR = "x-forwarded-for";

    public static final String http_content_length = "content-length";

    public static final String clivia_support_request_method_get = "GET";

    public static final String clivia_support_request_method_post = "POST";

    public static final String clivia_request_modify_type_add = "0";

    public static final String clivia_request_modify_type_update = "1";

    public static final String clivia_request_modify_type_remove = "2";

    public static final String clivia_request_cache = "cliviaCacheRequest";
    public static final String request_context = "request_context";

    public static final String serverProperties = "cliviaServerProperties";

    public static final String filter_cache = "filter_cache";

    public static final String blackList_cache = "blackList_cache";

    public static final String api_cache = "api_cache";

    public static final String invoker_cache = "invoker_cache";

    public static final String rpc_type_websocket = "websocket";

    public static final String rpc_type_http = "http";

    public static final String rpc_type_springcloud = "springcloud";

    public static final String rpc_type_apachedubbo = "apachedubbo";

    public static final String rpc_type_alibabadubbo = "alibabadubbo";

    public static final String clivia_system_invoker = "system";

    public static final String clivia_invoker_waper = "clivia.server.config.invokerWraper";
    public static final String clivia_invoker_waper_default_value = "0";
    public static final String clivia_invoker_waper_metrics_value = "1";

    public static final String gateway_admin_fetch_token_url = "/admin/server/getToken";

    public static final String gateway_admin_switch_url = "/admin/server/getSwitch";

    public static final String gateway_admin_apiInfoList_method_all = "/admin/server/api/getAllApiList";

    public static final String gateway_node_token = "gateway_node_token";
    public static final String gateway_node_switch = "gateway_node_switch";

    public static final String gateway_admin_apiInfoList_method = "/admin/server/api/getApiList";

    public static final String gateway_admin_blacklist_all_method = "/admin/server/blacklist/getBlacklistAll";

    public static final String gateway_admin_blacklist_method = "/admin/server/blacklist/getBlacklist";

    public static final String api_service_type_interface = "1";

    public static final String api_service_type_system = "0";

    public static final String gateway_client_switch_off = "1";

    public static final String appKey_check_anonymous = "1";

    public static final String appKey_check_anonymous_not = "0";

    public static final String gateway_admin_config_type = "clivia.admin.config.type";



    public static final String gateway_admin_config_type_db = "0";

    public static final String gateway_admin_config_type_web = "1";

    public static final String gateway_admin_config_type_file = "2";

    // register listener order
    public static final int clivia_register_listener_order = 0;

    // cache listener order
    public static final int clivia_cache_listener_order = 1;

    // server listener order
    public static final int clivia_server_listener_order = 2;

    public static final int clivia_banner_listener_order = 3;

    // register listener invokeScheduler method default period
    public static final long clivia_register_default_period = 10;

    // cache listener invokeScheduler method default period
    public static final long clivia_cache_default_period = 30;

    // server listener invokeScheduler method default period
    public static final long clivia_server_default_period = 30;

    public static final String default_clivia_gateway_blacklist_file = "cliviaBlacklistConfig";
    public static final String default_clivia_gateway_clientSecurity_file = "cliviaClientSecurityConfig";
    public static final String default_clivia_gateway_api_file = "cliviaApiConfig";

    public static final String clivia_gateway_cache_type = "clivia.gateway.cache.type";
    public static final String clivia_gateway_cache_type_mem = "0";
    public static final String clivia_check_model_record = "record";
    public static final String gateway_metrics_config_type = "clivia.server.config.recordType";
    public static final String gateway_metrics_config_log = "log";
    public static final String gateway_metrics_config_mysql = "mysql";
    public static final String gateway_metrics_config_kafka = "kafka";
    public static int success = 200;

}
