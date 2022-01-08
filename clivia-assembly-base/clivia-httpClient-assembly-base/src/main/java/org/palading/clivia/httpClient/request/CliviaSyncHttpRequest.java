package org.palading.clivia.httpClient.request;


import org.palading.clivia.httpClient.request.CliviaHttpRequest;
import org.palading.clivia.httpClient.response.CliviaHttpResponse;

/**
 * @author palading_cr
 * @title CliviaSyncHttpRequest
 * @project clivia-gateway
 */
public interface CliviaSyncHttpRequest extends HttpRequest {

    CliviaHttpResponse execute(CliviaHttpRequest requestHttpEntity);

}
