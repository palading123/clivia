package org.palading.clivia.httpClient.request;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.http.HttpEntity;

import org.palading.clivia.httpClient.ContentType;
import org.palading.clivia.httpClient.SupportContentType;


/**
 * requestEntity factory
 * 
 * @author palading_cr
 * @title CliviaRequestEntityFactory
 * @project clivia-gateway
 */
public class CliviaRequestEntityFactory {

    private static Map<String, HttpRequestEntity> HTTP_REQUEST_CACHE = new HashMap<>();

    private static CliviaRequestEntityFactory cliviaRequestEntityFactory = new CliviaRequestEntityFactory();

    private CliviaRequestEntityFactory() {
        initCache();
    }

    /**
     * load httpRequestEntity cache
     *
     * @author palading_cr
     *
     */
    private static void initCache() {
        ServiceLoader<HttpRequestEntity> serviceLoader = ServiceLoader.load(HttpRequestEntity.class);
        for (HttpRequestEntity httpRequestEntity : serviceLoader) {
            SupportContentType supportContentType = httpRequestEntity.getClass().getAnnotation(SupportContentType.class);
            String[] supportTypes = supportContentType.contentTypes();
            if (null == supportTypes || supportTypes.length < 1) {
                putDefaultContentTypeObject(httpRequestEntity);
            }
            for (String contentType : supportTypes) {
                putHttpRequestEntity(contentType, httpRequestEntity);
            }
        }
    }

    private static void putDefaultContentTypeObject(HttpRequestEntity httpRequestEntity) {
        putHttpRequestEntity(ContentType.APPLICATION_JSON, httpRequestEntity);
    }

    private static void putHttpRequestEntity(String key, HttpRequestEntity value) {
        HTTP_REQUEST_CACHE.put(key, value);
    }

    public static CliviaRequestEntityFactory getCliviaRequestEntityFactoryInstance() {
        return cliviaRequestEntityFactory;
    }

    /**
     * get HttpRequestEntity by content
     *
     * @author palading_cr
     *
     */
    public HttpRequestEntity getHttpRequestEntity(String contentType) {
        if (!HTTP_REQUEST_CACHE.containsKey(contentType)) {
            return new CliviaStringRequetEntity();
        }
        return HTTP_REQUEST_CACHE.get(contentType);
    }

    /**
     * @author palading_cr
     *
     */
    public HttpEntity getHttpEntity(HttpRequestEntity httpRequestEntity, Object param, String contentType, String charset)
        throws Exception {
        HttpEntity httpEntity = httpRequestEntity.getRequestEntity(contentType, param, charset);
        return httpEntity;
    }

}
