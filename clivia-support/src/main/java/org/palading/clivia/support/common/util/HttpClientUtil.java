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
package org.palading.clivia.support.common.util;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.palading.clivia.support.common.domain.HttpStaticProperties;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author palading_cr
 * @title HttpClientUtil
 * @project clivia /8
 */
public class HttpClientUtil {

    static HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if (executionCount >= 3) {
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                return false;
            }
            if (exception instanceof UnknownHostException) {
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {
                return false;
            }
            if (exception instanceof SSLException) {
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            return !(request instanceof HttpEntityEnclosingRequest);
        }

    };

    private static RequestConfig requestConfig = null;

    private static Registry<ConnectionSocketFactory> registry = null;

    private static CloseableHttpClient httpClient;

    static {
        registry =
            RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(HttpStaticProperties.getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(HttpStaticProperties.getMaxPreRoute());
        requestConfig =
            RequestConfig.custom().setSocketTimeout(HttpStaticProperties.getSocketTimeout())
                .setConnectTimeout(HttpStaticProperties.getConnectionTimeout())
                .setConnectionRequestTimeout(HttpStaticProperties.getConnectionTimeout()).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connectionManager)
        // .setRetryHandler(myRetryHandler)
            .build();
    }

    private static String doHttp(HttpRequestBase httpRequestBase) throws Exception {
        CloseableHttpResponse response = null;
        String responseContent = null;
        try {
            httpRequestBase.setConfig(requestConfig);
            response = httpClient.execute(httpRequestBase);
            responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
        } finally {
            if (null != response)
                response.close();
        }
        return responseContent;
    }

    /**
     * sendHttpGet(url)
     *
     * @param url
     * @return
     */
    public static String sendHttpGet(String url) throws Exception {
        return doHttp(new HttpGet(url));
    }

    public static String sendHttpPostJson(String url, String json) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(json, ContentType.create("application/json", "UTF-8"));
        httpPost.setEntity(stringEntity);
        return doHttp(httpPost);
    }

    public static String sendHttpPost(String url, Map<String, Object> param) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator iterator = param.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> elem = (Map.Entry<String, String>)iterator.next();
            list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
        }
        if (list.size() > 0) {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            httpPost.setEntity(entity);
        }
        return doHttp(httpPost);
    }
}
