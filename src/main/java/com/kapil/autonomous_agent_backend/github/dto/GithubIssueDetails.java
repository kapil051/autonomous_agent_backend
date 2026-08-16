package com.kapil.autonomous_agent_backend.github.dto;

import java.util.List;

public record GithubIssueDetails(
        String title,
        String body,
        List<GithubIssueComment> comments
) {
}
