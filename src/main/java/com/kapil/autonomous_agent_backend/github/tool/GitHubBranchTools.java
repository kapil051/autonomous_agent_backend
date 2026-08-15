package com.kapil.autonomous_agent_backend.github.tool;

import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.exception.GitHubToolException;
import com.kapil.autonomous_agent_backend.github.dto.GithubBranchApiResponse;
import com.kapil.autonomous_agent_backend.github.dto.GithubRepoDetailsApiResponse;
import com.kapil.autonomous_agent_backend.github.service.GithubOAuthService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubBranchTools {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final GithubOAuthService githubOAuthService;
    private final RestClient restClient;

    public GitHubBranchTools(GithubOAuthService githubOAuthService) {
        this.githubOAuthService = githubOAuthService;
        this.restClient = RestClient.create();
    }

    @Tool(description = "Check whether a branch already exists in a GitHub repository")
    public boolean branchExists(@ToolParam(description = "The id of the agent whose GitHub connection should be used") String agentId,
                                 @ToolParam(description = "The owner (user or organization) of the repository") String owner,
                                 @ToolParam(description = "The repository name") String repo,
                                 @ToolParam(description = "The branch name to check") String branchName) {
        String accessToken = githubOAuthService.resolveAccessToken(agentId);
        return fetchBranchSha(accessToken, owner, repo, branchName) != null;
    }

    @Tool(description = "Create a new branch in a GitHub repository, branching off the repository's default branch")
    public void createBranch(@ToolParam(description = "The id of the agent whose GitHub connection should be used") String agentId,
                              @ToolParam(description = "The owner (user or organization) of the repository") String owner,
                              @ToolParam(description = "The repository name") String repo,
                              @ToolParam(description = "The name of the new branch to create") String branchName) {
        String accessToken = githubOAuthService.resolveAccessToken(agentId);

        String defaultBranch = fetchDefaultBranch(accessToken, owner, repo);
        String baseSha = fetchBranchSha(accessToken, owner, repo, defaultBranch);

        if (baseSha == null) {
            throw new GitHubToolException(Constants.GITHUB_BRANCH_CREATE_FAILED);
        }

        createBranchRef(accessToken, owner, repo, branchName, baseSha);
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

    private String fetchBranchSha(String accessToken, String owner, String repo, String branchName) {
        String uri = "%s/repos/%s/%s/branches/%s".formatted(GITHUB_API_BASE_URL, owner, repo, branchName);

        GithubBranchApiResponse branch = restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    if (response.getStatusCode().value() != 404) {
                        throw new GitHubToolException(Constants.GITHUB_BRANCH_CHECK_FAILED);
                    }
                })
                .body(GithubBranchApiResponse.class);

        return branch == null ? null : branch.commit().sha();
    }

    private void createBranchRef(String accessToken, String owner, String repo, String branchName, String fromSha) {
        String uri = "%s/repos/%s/%s/git/refs".formatted(GITHUB_API_BASE_URL, owner, repo);

        restClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .body(new CreateRefRequest("refs/heads/" + branchName, fromSha))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw new GitHubToolException(Constants.GITHUB_BRANCH_CREATE_FAILED);
                })
                .toBodilessEntity();
    }

    private record CreateRefRequest(String ref, String sha) {
    }
}
