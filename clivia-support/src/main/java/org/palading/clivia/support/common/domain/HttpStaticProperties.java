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

/**
 * @author palading_cr
 * @title HttpStaticProperties
 * @project clivia /8
 */
public class HttpStaticProperties {

    private static int maxTotal = 8;

    private static int maxPreRoute = 8;

    private static int socketTimeout = 10000;

    private static int connectionTimeout = 3000;

    private static int requestTimeout = 2500;

    public static int getMaxTotal() {
        return maxTotal;
    }

    public static void setMaxTotal(int maxTotal) {
        HttpStaticProperties.maxTotal = maxTotal;
    }

    public static int getMaxPreRoute() {
        return maxPreRoute;
    }

    public static void setMaxPreRoute(int maxPreRoute) {
        HttpStaticProperties.maxPreRoute = maxPreRoute;
    }

    public static int getSocketTimeout() {
        return socketTimeout;
    }

    public static void setSocketTimeout(int socketTimeout) {
        HttpStaticProperties.socketTimeout = socketTimeout;
    }

    public static int getConnectionTimeout() {
        return connectionTimeout;
    }

    public static void setConnectionTimeout(int connectionTimeout) {
        HttpStaticProperties.connectionTimeout = connectionTimeout;
    }

    public static int getRequestTimeout() {
        return requestTimeout;
    }

    public static void setRequestTimeout(int requestTimeout) {
        HttpStaticProperties.requestTimeout = requestTimeout;
    }
}
