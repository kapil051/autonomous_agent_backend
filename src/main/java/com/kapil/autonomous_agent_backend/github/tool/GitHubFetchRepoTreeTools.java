package com.kapil.autonomous_agent_backend.github.tool;

import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.exception.GitHubToolException;
import com.kapil.autonomous_agent_backend.github.dto.GithubRepoDetailsApiResponse;
import com.kapil.autonomous_agent_backend.github.dto.GithubRepoTreeResultEntry;
import com.kapil.autonomous_agent_backend.github.dto.GithubRepoTreeResult;
import com.kapil.autonomous_agent_backend.github.dto.GithubTreeApiResponse;
import com.kapil.autonomous_agent_backend.github.service.GithubOAuthService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GitHubFetchRepoTreeTools {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final GithubOAuthService githubOAuthService;
    private final RestClient restClient;

    public GitHubFetchRepoTreeTools(GithubOAuthService githubOAuthService) {
        this.githubOAuthService = githubOAuthService;
        this.restClient = RestClient.create();
    }

    @Tool(description = "Fetch the recursive file tree (paths and types) of a GitHub repository at its default branch. The result indicates whether the tree was truncated by GitHub due to repository size")
    public GithubRepoTreeResult getRepoTree(@ToolParam(description = "The id of the agent whose GitHub connection should be used") String agentId,
                                              @ToolParam(description = "The owner (user or organization) of the repository") String owner,
                                              @ToolParam(description = "The repository name") String repo) {
        String accessToken = githubOAuthService.resolveAccessToken(agentId);

        String defaultBranch = fetchDefaultBranch(accessToken, owner, repo);
        GithubTreeApiResponse tree = fetchRepoTree(accessToken, owner, repo, defaultBranch);

        List<GithubRepoTreeResultEntry> entries = tree.tree().stream()
                .map(entry -> new GithubRepoTreeResultEntry(entry.path(), entry.type(), entry.size()))
                .toList();

        return new GithubRepoTreeResult(entries, tree.truncated());
    }

    private String fetchDefaultBranch(String accessToken, String owner, String repo) {
        String uri = "%s/repos/%s/%s".formatted(GITHUB_API_BASE_URL, owner, repo);

        GithubRepoDetailsApiResponse repoDetails = restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(GithubRepoDetailsApiResponse.class);

        if (repoDetails == null) {
            throw new GitHubToolException(Constants.GITHUB_REPO_DETAILS_FETCH_FAILED);
        }

        return repoDetails.default_branch();
    }

    private GithubTreeApiResponse fetchRepoTree(String accessToken, String owner, String repo, String branch) {
        String uri = "%s/repos/%s/%s/git/trees/%s?recursive=1".formatted(GITHUB_API_BASE_URL, owner, repo, branch);

        GithubTreeApiResponse tree = restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(GithubTreeApiResponse.class);

        if (tree == null) {
            throw new GitHubToolException(Constants.GITHUB_REPO_TREE_FETCH_FAILED);
        }

        return tree;
    }
}