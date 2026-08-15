package com.kapil.autonomous_agent_backend.constant;

import java.util.Map;

/**
 * Central place for all application constants.
 *
 * WHY USE CONSTANTS?
 *   - Avoids hardcoded strings scattered across the codebase.
 *   - Easy to change a message in one place instead of hunting through every file.
 *   - Response codes help the frontend identify the exact situation programmatically.
 *
 * NAMING CONVENTION:
 *   - 2xxx = Success responses
 *   - 4xxx = Failure responses (auth errors, validation errors, resource errors, server errors)
 */
public class Constants {

    private Constants() {
        throw new IllegalArgumentException("Could not initialize this class");
    }

    // ==================== SUCCESS CODES (2xxx) ====================

    public static final int GITHUB_AUTH_URL_GENERATED = 2014;
    public static final int GITHUB_CONNECT_SUCCESS = 2015;
    public static final int AGENT_CREATED = 2016;

    // ==================== FAILURE CODES (4xxx) ====================

    public static final int INTERNAL_SERVER_ERROR = 4006;
    public static final int GITHUB_TOKEN_EXCHANGE_FAILED = 4037;
    public static final int GITHUB_USER_FETCH_FAILED = 4038;
    public static final int ENCRYPTION_FAILED = 4039;
    public static final int DECRYPTION_FAILED = 4040;
    public static final int GITHUB_OAUTH_INVALID_STATE = 4041;
    public static final int AGENT_NAME_REQUIRED = 4042;
    public static final int AGENT_ALREADY_EXISTS = 4043;
    public static final int GITHUB_CONNECTION_NOT_FOUND = 4044;
    public static final int GITHUB_ISSUE_FETCH_FAILED = 4045;
    public static final int GITHUB_COMMENT_POST_FAILED = 4046;
    public static final int GITHUB_REPO_TREE_FETCH_FAILED = 4047;
    public static final int GITHUB_REPO_DETAILS_FETCH_FAILED = 4048;
    public static final int GITHUB_FILE_FETCH_FAILED = 4049;

    // ==================== RESPONSE MESSAGE MAP ====================

    /**
     * Maps each response code to its human-readable message.
     * ApiResponse uses this map to auto-resolve messages from codes.
     */
    public static final Map<Integer, String> RESPONSE = Map.ofEntries(

            // ---------> Success messages
            Map.entry(GITHUB_AUTH_URL_GENERATED, "GitHub auth URL generated"),
            Map.entry(GITHUB_CONNECT_SUCCESS, "GitHub account connected successfully"),
            Map.entry(AGENT_CREATED, "Agent created successfully"),

            // ---------> Failure messages
            Map.entry(INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later"),
            Map.entry(GITHUB_TOKEN_EXCHANGE_FAILED, "Failed to obtain GitHub access token"),
            Map.entry(GITHUB_USER_FETCH_FAILED, "Failed to fetch GitHub user"),
            Map.entry(ENCRYPTION_FAILED, "Failed to encrypt value"),
            Map.entry(DECRYPTION_FAILED, "Failed to decrypt value"),
            Map.entry(GITHUB_OAUTH_INVALID_STATE, "Invalid or tampered OAuth state"),
            Map.entry(AGENT_NAME_REQUIRED, "Agent name is required"),
            Map.entry(AGENT_ALREADY_EXISTS, "Agent with this name already exists"),
            Map.entry(GITHUB_CONNECTION_NOT_FOUND, "No GitHub connection found for this agent"),
            Map.entry(GITHUB_ISSUE_FETCH_FAILED, "Failed to fetch GitHub issue"),
            Map.entry(GITHUB_COMMENT_POST_FAILED, "Failed to post GitHub issue comment"),
            Map.entry(GITHUB_REPO_TREE_FETCH_FAILED, "Failed to fetch GitHub repository tree"),
            Map.entry(GITHUB_REPO_DETAILS_FETCH_FAILED, "Failed to fetch GitHub repository details"),
            Map.entry(GITHUB_FILE_FETCH_FAILED, "Failed to fetch GitHub file content")
    );
}