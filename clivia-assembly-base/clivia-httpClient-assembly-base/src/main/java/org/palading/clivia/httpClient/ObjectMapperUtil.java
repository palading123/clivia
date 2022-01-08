package org.palading.clivia.httpClient;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author palading_cr
 * @title ObjectMapperUtil
 * @project clivia-gateway
 */
public class ObjectMapperUtil {
    public static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        /*		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
         */mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static <T> T inputStreamToObject(InputStream inputStream, Class<T> clazz) {
        try {
            return mapper.readValue(inputStream, mapper.constructType(clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
