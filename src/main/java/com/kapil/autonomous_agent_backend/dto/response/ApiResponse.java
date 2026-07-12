package com.kapil.autonomous_agent_backend.dto.response;

import com.kapil.autonomous_agent_backend.constant.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;

/**
 * Generic API response wrapper.
 * Used to return a consistent JSON structure for all API endpoints.
 *
 * Fields:
 *   - code    : Application-level response code (defined in Constants)
 *   - status  : true = success, false = error
 *   - message : Auto-resolved from Constants.RESPONSE map if not set
 *   - data    : Response payload (defaults to empty map if not set)
 *
 * Usage:
 *   new ResponseEntity<>(ApiResponse.builder().code(Constants.LOGIN_SUCCESS).status(true).data(authResponse).build(), HttpStatus.OK);
 *
 * The message is auto-resolved from Constants.RESPONSE when not explicitly set.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private int code;
    private boolean status;
    private String message;
    private Object data;

    /**
     * Custom builder method.
     * Lombok generates the builder from this static method.
     * If message is not set, it auto-resolves from Constants.RESPONSE using the code.
     * If data is not set, it defaults to an empty map.
     */
    @Builder
    private static ApiResponse create(int code, boolean status, String message, Object data) {
        ApiResponse response = new ApiResponse();
        response.code = code;
        response.status = status;
        response.message = (message != null) ? message : Constants.RESPONSE.get(code);
        response.data = (data != null) ? data : Collections.emptyMap();
        return response;
    }
}