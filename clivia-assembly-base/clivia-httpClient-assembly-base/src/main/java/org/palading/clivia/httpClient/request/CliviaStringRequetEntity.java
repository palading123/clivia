package org.palading.clivia.httpClient.request;




import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import org.palading.clivia.httpClient.SupportContentType;

/**
 * @author palading_cr
 * @title CliviaStringRequetEntity
 * @project clivia-gateway
 */
@SupportContentType(contentTypes = {org.palading.clivia.httpClient.ContentType.APPLICATION_JSON})
public class CliviaStringRequetEntity implements HttpRequestEntity<StringEntity> {

    @Override
    public StringEntity getRequestEntity(String contentType, Object param, String charset) throws Exception {
        if (null != param) {
            if (param instanceof String) {
                return new StringEntity(String.valueOf(param), ContentType.create(contentType).getMimeType(), charset);
            }
        }
        return null;
    }

}
