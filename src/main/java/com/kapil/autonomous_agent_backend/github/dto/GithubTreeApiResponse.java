package com.kapil.autonomous_agent_backend.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubTreeApiResponse(
        List<GithubTreeEntryApiResponse> tree,
        boolean truncated
) {
}