package org.palading.clivia.httpClient.request;

import org.apache.http.HttpEntity;

/**
 * @author palading_cr
 * @title RequestEntity
 * @project clivia-gateway
 */
public interface HttpRequestEntity<T extends HttpEntity> {

    public T getRequestEntity(String contentType, Object param, String charset) throws Exception;

}
