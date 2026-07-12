package com.kapil.autonomous_agent_backend.exception;

import com.kapil.autonomous_agent_backend.constant.Constants;
import org.springframework.http.HttpStatus;

public class AgentException extends AppException {

    private final int errorCode;
    private final HttpStatus httpStatus;

    public AgentException(int errorCode, HttpStatus httpStatus) {
        super(Constants.RESPONSE.get(errorCode));
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}