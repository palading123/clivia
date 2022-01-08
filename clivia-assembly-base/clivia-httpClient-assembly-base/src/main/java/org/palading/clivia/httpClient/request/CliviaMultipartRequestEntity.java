package org.palading.clivia.httpClient.request;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;

import org.palading.clivia.httpClient.ContentType;
import org.palading.clivia.httpClient.SupportContentType;


/**
 * @author palading_cr
 * @title CliviaFileRequestEntity
 * @project clivia-gateway
 */
@SupportContentType(contentTypes = {ContentType.MULTIPART_FORM_DATA})
public class CliviaMultipartRequestEntity implements HttpRequestEntity {

    /**
     * @author palading_cr
     *
     */
    @Override
    public HttpEntity getRequestEntity(String contentType, Object param, String charset) throws Exception {
        if (param instanceof HashMap) {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            Map<String, ContentBody> contentMap = (HashMap)param;
            for (Map.Entry<String, ContentBody> entry : contentMap.entrySet()) {
                multipartEntityBuilder.addPart(entry.getKey(), entry.getValue());
            }
            return multipartEntityBuilder.build();
        }
        return null;
    }
}
