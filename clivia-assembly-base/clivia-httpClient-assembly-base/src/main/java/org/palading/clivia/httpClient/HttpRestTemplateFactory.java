package org.palading.clivia.httpClient;

/**
 * @author palading_cr
 * @title HttpRestTemplateFactory
 * @project clivia-gateway
 */
public interface HttpRestTemplateFactory {

    /**
     * @author palading_cr
     *
     */
    public CliviaSyncHttpRestTemplate buildHttpRestTemplate();

}
