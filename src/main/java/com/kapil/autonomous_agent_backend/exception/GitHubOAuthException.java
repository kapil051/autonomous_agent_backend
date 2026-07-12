package com.kapil.autonomous_agent_backend.exception;

import com.kapil.autonomous_agent_backend.constant.Constants;
import org.springframework.http.HttpStatus;

public class GitHubOAuthException extends AppException {

    private final int errorCode;
    private final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public GitHubOAuthException(int errorCode) {
        super(Constants.RESPONSE.get(errorCode));
        this.errorCode = errorCode;
    }

    public GitHubOAuthException(int errorCode, Throwable cause) {
        super(Constants.RESPONSE.get(errorCode), cause);
        this.errorCode = errorCode;
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