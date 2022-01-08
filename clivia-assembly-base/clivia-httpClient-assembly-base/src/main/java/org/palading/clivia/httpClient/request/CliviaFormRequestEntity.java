package org.palading.clivia.httpClient.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;


import org.palading.clivia.httpClient.ContentType;
import org.palading.clivia.httpClient.SupportContentType;


/**
 * @author palading_cr
 * @title CliviaFormRequestEntity
 * @project clivia-gateway
 */
@SupportContentType(contentTypes = {ContentType.APPLICATION_FORM_URLENCODED})
public class CliviaFormRequestEntity implements HttpRequestEntity<UrlEncodedFormEntity> {

    @Override
    public UrlEncodedFormEntity getRequestEntity(String contentType, Object param, String charset) throws Exception {
        if (param != null) {
            if (param instanceof Map) {
                Map<String, String> map = (HashMap)param;
                List<NameValuePair> params = new ArrayList<NameValuePair>(map.size());
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                return new UrlEncodedFormEntity(params, charset);
            }
        }
        return null;
    }

}
