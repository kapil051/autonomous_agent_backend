package com.kapil.autonomous_agent_backend.github.tool;

import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.exception.GitHubToolException;
import com.kapil.autonomous_agent_backend.github.dto.GithubFileContentApiResponse;
import com.kapil.autonomous_agent_backend.github.dto.GithubFileResult;
import com.kapil.autonomous_agent_backend.github.dto.GithubRepoDetailsApiResponse;
import com.kapil.autonomous_agent_backend.github.service.GithubOAuthService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class GitHubFetchFileTools {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final GithubOAuthService githubOAuthService;
    private final RestClient restClient;

    public GitHubFetchFileTools(GithubOAuthService githubOAuthService) {
        this.githubOAuthService = githubOAuthService;
        this.restClient = RestClient.create();
    }

    @Tool(description = "Fetch the decoded plain-text content of a file from a GitHub repository at its default branch")
    public GithubFileResult getFile(@ToolParam(description = "The id of the agent whose GitHub connection should be used") String agentId,
                                      @ToolParam(description = "The owner (user or organization) of the repository") String owner,
                                      @ToolParam(description = "The repository name") String repo,
                                      @ToolParam(description = "The path of the file within the repository") String path) {
        String accessToken = githubOAuthService.resolveAccessToken(agentId);

        String defaultBranch = fetchDefaultBranch(accessToken, owner, repo);
        GithubFileContentApiResponse fileContent = fetchFileContent(accessToken, owner, repo, path, defaultBranch);

        String decodedContent = decodeContent(fileContent.content());

        return new GithubFileResult(fileContent.path(), decodedContent);
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

    private GithubFileContentApiResponse fetchFileContent(String accessToken, String owner, String repo, String path, String branch) {
        String uri = "%s/repos/%s/%s/contents/%s?ref=%s".formatted(GITHUB_API_BASE_URL, owner, repo, path, branch);

        GithubFileContentApiResponse fileContent = restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(GithubFileContentApiResponse.class);

        if (fileContent == null) {
            throw new GitHubToolException(Constants.GITHUB_FILE_FETCH_FAILED);
        }

        return fileContent;
    }

    private String decodeContent(String base64Content) {
        byte[] decodedBytes = Base64.getMimeDecoder().decode(base64Content);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}