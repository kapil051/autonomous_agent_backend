package com.kapil.autonomous_agent_backend.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubCommitApiResponse(
        String sha,
        Tree tree
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tree(String sha) {
    }
}
