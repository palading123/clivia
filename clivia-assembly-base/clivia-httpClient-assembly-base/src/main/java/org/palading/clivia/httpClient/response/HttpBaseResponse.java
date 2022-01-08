package org.palading.clivia.httpClient.response;

import java.io.Serializable;

/**
 * @author palading_cr
 * @title HttpBaseResponse
 * @project clivia-gateway
 */
public class HttpBaseResponse<T> implements Serializable {
    private int resCode;

    private String resMsg;

    private T resData;

    public HttpBaseResponse(int resCode, String resMsg, T resData) {
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.resData = resData;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public Object getResData() {
        return resData;
    }

    public void setResData(T resData) {
        this.resData = resData;
    }
}
