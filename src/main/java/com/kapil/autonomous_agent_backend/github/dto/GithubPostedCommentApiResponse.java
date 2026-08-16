package com.kapil.autonomous_agent_backend.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubPostedCommentApiResponse(
        long id,
        String body,
        String html_url
) {
}