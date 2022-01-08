package org.palading.clivia.httpClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.*;
import org.palading.clivia.httpClient.request.HttpRequestBaseFactory;

/**
 * @author palading_cr
 * @title HttpMethod
 * @project clivia-gateway
 */
public class CliviaHttpRequestBaseFactory implements HttpRequestBaseFactory {

    /**
     * init http method cache
     *
     * @author palading_cr
     *
     */
    private Map<String, java.lang.reflect.Method> METHODS = new HashMap<>();

    public CliviaHttpRequestBaseFactory() {
        initMethodCache();
    }

    /**
     * httpmethod cache
     *
     * @author palading_cr
     *
     */
    private Map<String, java.lang.reflect.Method> initMethodCache() {
        if (null == METHODS || METHODS.size() == 0) {
            Class clazz = CliviaHttpRequestBaseFactory.class;
            java.lang.reflect.Method[] methods = clazz.getMethods();
            for (java.lang.reflect.Method method : methods) {
                HttpMethod httpMethod = method.getAnnotation(HttpMethod.class);
                if (null != httpMethod) {
                    METHODS.putIfAbsent(httpMethod.method(), method);
                }
            }
        }
        return METHODS;
    }

    /**
     * set header and return HttpRequestBase
     *
     * @author palading_cr
     *
     */
    @Override
    public HttpRequestBase setHeader(String headName, String headValue, HttpRequestBase httpRequestBase) {
        httpRequestBase.setHeader(headName, headValue);
        return httpRequestBase;
    }

    @Override
    public HttpRequestBase setHttpRequestEntity(HttpEntity httpEntity, HttpRequestBase httpRequestBase) {
        if (httpRequestBase instanceof HttpEntityEnclosingRequest) {
            ((HttpEntityEnclosingRequest)httpRequestBase).setEntity(httpEntity);
        }
        return httpRequestBase;
    }

    /**
     * invoke http method and return HttpRequestBase
     *
     * @author palading_cr
     *
     */
    @Override
    public HttpRequestBase getHttpRequestBase(String method, String uri) {
        String methodName = method.toUpperCase();
        Method method1 = METHODS.get(methodName);
        if (null != method1) {
            try {
                return (HttpRequestBase)method1.invoke(this, uri);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @HttpMethod(method = "GET")
    public HttpRequestBase get(String url) {
        return new HttpGet(url);
    }

    @HttpMethod(method = "POST")
    public HttpRequestBase post(String url) {
        return new HttpPost(url);
    }

    @HttpMethod(method = "HEAD")
    public HttpRequestBase head(String url) {
        return new HttpHead(url);
    }

    @HttpMethod(method = "PUT")
    public HttpRequestBase put(String url) {
        return new HttpPut(url);
    }

    @HttpMethod(method = "DELETE")
    public HttpRequestBase delete(String url) {
        return new HttpDelete(url);
    }
}
