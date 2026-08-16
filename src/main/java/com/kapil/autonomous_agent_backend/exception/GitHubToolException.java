package com.kapil.autonomous_agent_backend.exception;

import com.kapil.autonomous_agent_backend.constant.Constants;
import org.springframework.http.HttpStatus;

public class GitHubToolException extends AppException {

    private final int errorCode;
    private final HttpStatus httpStatus;

    public GitHubToolException(int errorCode) {
        this(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public GitHubToolException(int errorCode, HttpStatus httpStatus) {
        super(Constants.RESPONSE.get(errorCode));
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public GitHubToolException(int errorCode, Throwable cause) {
        super(Constants.RESPONSE.get(errorCode), cause);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
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