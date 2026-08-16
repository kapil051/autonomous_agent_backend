package com.kapil.autonomous_agent_backend.exception;

import com.kapil.autonomous_agent_backend.constant.Constants;
import org.springframework.http.HttpStatus;

public class GitHubOAuthException extends AppException {

    private final int errorCode;
    private final HttpStatus httpStatus;

    public GitHubOAuthException(int errorCode) {
        this(errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public GitHubOAuthException(int errorCode, HttpStatus httpStatus) {
        super(Constants.RESPONSE.get(errorCode));
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public GitHubOAuthException(int errorCode, Throwable cause) {
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