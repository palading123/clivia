package org.palading.clivia.httpClient;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author palading_cr
 * @title SupportContentType
 * @project clivia-gateway
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SupportContentType {

    /**
     * supported contentType
     *
     * @author palading_cr
     *
     */
    public String[] contentTypes();
}
