package com.kapil.autonomous_agent_backend.exception;

import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Catches ALL custom exceptions (GitHubOAuthException, EncryptionException, etc.).
     * Since they all extend AppException, this one handler covers everything.
     * The errorCode and httpStatus come from the exception itself.
     */
    @ExceptionHandler(AppException.class)
    public final ResponseEntity<ApiResponse> handleAppException(AppException ex) {
        log.warn("App exception [code={}, status={}]: {}", ex.getErrorCode(), ex.getHttpStatus(), ex.getMessage(), ex);
        return new ResponseEntity<>(ApiResponse.builder().code(ex.getErrorCode()).status(false).build(), ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiResponse> handleException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                ApiResponse.builder().code(Constants.INTERNAL_SERVER_ERROR).status(false).build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}