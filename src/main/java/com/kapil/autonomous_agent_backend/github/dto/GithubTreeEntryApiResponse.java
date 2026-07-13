package com.kapil.autonomous_agent_backend.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubTreeEntryApiResponse(
        String path,
        String type,
        Long size
) {
}