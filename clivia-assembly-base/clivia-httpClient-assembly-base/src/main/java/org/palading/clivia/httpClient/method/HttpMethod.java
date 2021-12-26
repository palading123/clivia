package org.palading.clivia.httpClient.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author palading_cr
 * @title Method
 * @project clivia-gateway
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HttpMethod {

    /**
     * http method name
     * 
     * @author palading_cr
     *
     */
    public String method() default "GET";
}
