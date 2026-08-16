package com.kapil.autonomous_agent_backend.github.dto;

import java.util.List;

public record GithubRepoTreeResult(
        List<GithubRepoTreeResultEntry> entries,
        boolean truncated
) {
}