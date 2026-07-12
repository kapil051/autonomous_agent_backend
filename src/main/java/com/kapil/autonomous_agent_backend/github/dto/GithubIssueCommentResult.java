package com.kapil.autonomous_agent_backend.github.dto;

public record GithubIssueCommentResult(
        long id,
        String body,
        String htmlUrl
) {
}