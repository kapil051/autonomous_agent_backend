package com.kapil.autonomous_agent_backend.github.dto;

public record GithubFileResult(
        String path,
        String content
) {
}