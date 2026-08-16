package com.kapil.autonomous_agent_backend.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubTokenResponse(
        String access_token,
        String token_type,
        String scope,
        String error,
        String error_description
) {
}