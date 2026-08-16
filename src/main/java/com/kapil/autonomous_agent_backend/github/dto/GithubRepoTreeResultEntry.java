package com.kapil.autonomous_agent_backend.github.dto;

public record GithubRepoTreeResultEntry(
        String path,
        String type,
        Long size
) {
}