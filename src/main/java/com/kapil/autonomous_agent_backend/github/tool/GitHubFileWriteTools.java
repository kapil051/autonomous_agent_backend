package com.kapil.autonomous_agent_backend.github.tool;

import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.exception.GitHubToolException;
import com.kapil.autonomous_agent_backend.github.dto.GithubBranchApiResponse;
import com.kapil.autonomous_agent_backend.github.dto.GithubCommitApiResponse;
import com.kapil.autonomous_agent_backend.github.dto.GithubFileToWrite;
import com.kapil.autonomous_agent_backend.github.dto.GithubShaApiResponse;
import com.kapil.autonomous_agent_backend.github.service.GithubOAuthService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Writes a batch of files to a branch as a single atomic commit, via GitHub's Git Data API
 * (tree -> commit -> ref update). The branch ref only moves at the last step, so a failure
 * partway through leaves the branch untouched.
 */
@Component
public class GitHubFileWriteTools {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final GithubOAuthService githubOAuthService;
    private final RestClient restClient;

    public GitHubFileWriteTools(GithubOAuthService githubOAuthService) {
        this.githubOAuthService = githubOAuthService;
        this.restClient = RestClient.create();
    }

    @Tool(description = "Write one or more files to a GitHub branch as a single commit")
    public void writeFiles(@ToolParam(description = "The id of the agent whose GitHub connection should be used") String agentId,
                            @ToolParam(description = "The owner (user or organization) of the repository") String owner,
                            @ToolParam(description = "The repository name") String repo,
                            @ToolParam(description = "The branch to commit to") String branch,
                            @ToolParam(description = "The files to write, each with a path and its full content") List<GithubFileToWrite> files,
                            @ToolParam(description = "The commit message") String commitMessage) {
        String accessToken = githubOAuthService.resolveAccessToken(agentId);

        String baseCommitSha = fetchBranchHeadSha(accessToken, owner, repo, branch);
        String baseTreeSha = fetchCommitTreeSha(accessToken, owner, repo, baseCommitSha);

        String newTreeSha = createTree(accessToken, owner, repo, baseTreeSha, files);
        String newCommitSha = createCommit(accessToken, owner, repo, commitMessage, newTreeSha, baseCommitSha);

        updateBranchRef(accessToken, owner, repo, branch, newCommitSha);
    }

    private String fetchBranchHeadSha(String accessToken, String owner, String repo, String branch) {
        String uri = "%s/repos/%s/%s/branches/%s".formatted(GITHUB_API_BASE_URL, owner, repo, branch);

        GithubBranchApiResponse branchInfo = restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(GithubBranchApiResponse.class);

        if (branchInfo == null) {
            throw new GitHubToolException(Constants.GITHUB_FILE_WRITE_FAILED);
        }

        return branchInfo.commit().sha();
    }

    private String fetchCommitTreeSha(String accessToken, String owner, String repo, String commitSha) {
        String uri = "%s/repos/%s/%s/git/commits/%s".formatted(GITHUB_API_BASE_URL, owner, repo, commitSha);

        GithubCommitApiResponse commit = restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(GithubCommitApiResponse.class);

        if (commit == null) {
            throw new GitHubToolException(Constants.GITHUB_FILE_WRITE_FAILED);
        }

        return commit.tree().sha();
    }

    private String createTree(String accessToken, String owner, String repo, String baseTreeSha, List<GithubFileToWrite> files) {
        String uri = "%s/repos/%s/%s/git/trees".formatted(GITHUB_API_BASE_URL, owner, repo);

        List<TreeEntry> entries = files.stream()
                .map(file -> new TreeEntry(file.path(), "100644", "blob", file.content()))
                .toList();

        GithubShaApiResponse response = restClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .body(new CreateTreeRequest(baseTreeSha, entries))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, res) -> {
                    throw new GitHubToolException(Constants.GITHUB_FILE_WRITE_FAILED);
                })
                .body(GithubShaApiResponse.class);

        if (response == null) {
            throw new GitHubToolException(Constants.GITHUB_FILE_WRITE_FAILED);
        }

        return response.sha();
    }

    private String createCommit(String accessToken, String owner, String repo, String message, String treeSha, String parentSha) {
        String uri = "%s/repos/%s/%s/git/commits".formatted(GITHUB_API_BASE_URL, owner, repo);

        GithubShaApiResponse response = restClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .body(new CreateCommitRequest(message, treeSha, List.of(parentSha)))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, res) -> {
                    throw new GitHubToolException(Constants.GITHUB_FILE_WRITE_FAILED);
                })
                .body(GithubShaApiResponse.class);

        if (response == null) {
            throw new GitHubToolException(Constants.GITHUB_FILE_WRITE_FAILED);
        }

        return response.sha();
    }

    private void updateBranchRef(String accessToken, String owner, String repo, String branch, String newCommitSha) {
        String uri = "%s/repos/%s/%s/git/refs/heads/%s".formatted(GITHUB_API_BASE_URL, owner, repo, branch);

        restClient.patch()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .body(new UpdateRefRequest(newCommitSha, false))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, res) -> {
                    throw new GitHubToolException(Constants.GITHUB_FILE_WRITE_FAILED);
                })
                .toBodilessEntity();
    }

    private record TreeEntry(String path, String mode, String type, String content) {
    }

    private record CreateTreeRequest(String base_tree, List<TreeEntry> tree) {
    }

    private record CreateCommitRequest(String message, String tree, List<String> parents) {
    }

    private record UpdateRefRequest(String sha, boolean force) {
    }
}