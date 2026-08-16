package com.kapil.autonomous_agent_backend.github.dto;

public record GithubFileToWrite(
        String path,
        String content
) {
}
