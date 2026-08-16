package com.kapil.autonomous_agent_backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Abstract parent for ALL custom exceptions in this application.
 *
 * WHY THIS DESIGN?
 *   - Every custom exception extends this class.
 *   - Each child provides its own errorCode (from Constants) and httpStatus.
 *   - The GlobalExceptionHandler catches ONLY this parent class.
 *   - One handler handles all custom exceptions — clean and scalable.
 *
 * HOW TO ADD A NEW EXCEPTION?
 *   1. Create a new class that extends AppException.
 *   2. Set the httpStatus (e.g., HttpStatus.CONFLICT).
 *   3. Throw it with a code from Constants.
 *   4. That's it — the GlobalExceptionHandler already catches it!
 */
public abstract class AppException extends RuntimeException {

    /**
     * Create an exception with a message.
     * The message is usually auto-resolved from Constants.RESPONSE map.
     *
     * @param message human-readable error message
     */
    protected AppException(String message) {
        super(message);
    }

    /**
     * Create an exception with a message and root cause.
     * Use this when wrapping another exception (e.g., database error).
     *
     * @param message human-readable error message
     * @param cause   the original exception that caused this
     */
    protected AppException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns the application-level error code (from Constants).
     * Used by GlobalExceptionHandler to build the ApiResponse.
     */
    public abstract int getErrorCode();

    /**
     * Returns the HTTP status to send in the response.
     * Each child exception decides its own HTTP status.
     */
    public abstract HttpStatus getHttpStatus();
}