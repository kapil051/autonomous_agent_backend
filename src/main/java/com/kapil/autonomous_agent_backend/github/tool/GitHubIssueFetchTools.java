package com.kapil.autonomous_agent_backend.github.tool;

import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.exception.GitHubToolException;
import com.kapil.autonomous_agent_backend.github.dto.GithubCommentApiResponse;
import com.kapil.autonomous_agent_backend.github.dto.GithubIssueApiResponse;
import com.kapil.autonomous_agent_backend.github.dto.GithubIssueComment;
import com.kapil.autonomous_agent_backend.github.dto.GithubIssueDetails;
import com.kapil.autonomous_agent_backend.github.service.GithubOAuthService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Component
public class GitHubIssueFetchTools {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final GithubOAuthService githubOAuthService;
    private final RestClient restClient;

    public GitHubIssueFetchTools(GithubOAuthService githubOAuthService) {
        this.githubOAuthService = githubOAuthService;
        this.restClient = RestClient.create();
    }

    @Tool(description = "Fetch a GitHub issue's title, body and comments for a given repository")
    public GithubIssueDetails getIssue(@ToolParam(description = "The id of the agent whose GitHub connection should be used") String agentId,
                                        @ToolParam(description = "The owner (user or organization) of the repository") String owner,
                                        @ToolParam(description = "The repository name") String repo,
                                        @ToolParam(description = "The issue number") int issueNumber) {
        String accessToken = githubOAuthService.resolveAccessToken(agentId);

        GithubIssueApiResponse issue = fetchIssue(accessToken, owner, repo, issueNumber);
        List<GithubIssueComment> comments = fetchComments(accessToken, owner, repo, issueNumber);

        return new GithubIssueDetails(issue.title(), issue.body(), comments);
    }

    private GithubIssueApiResponse fetchIssue(String accessToken, String owner, String repo, int issueNumber) {
        String uri = "%s/repos/%s/%s/issues/%d".formatted(GITHUB_API_BASE_URL, owner, repo, issueNumber);

        GithubIssueApiResponse issue = restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(GithubIssueApiResponse.class);

        if (issue == null) {
            throw new GitHubToolException(Constants.GITHUB_ISSUE_FETCH_FAILED);
        }

        return issue;
    }

    private List<GithubIssueComment> fetchComments(String accessToken, String owner, String repo, int issueNumber) {
        String uri = "%s/repos/%s/%s/issues/%d/comments".formatted(GITHUB_API_BASE_URL, owner, repo, issueNumber);

        GithubCommentApiResponse[] comments = restClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(GithubCommentApiResponse[].class);

        if (comments == null) {
            throw new GitHubToolException(Constants.GITHUB_ISSUE_FETCH_FAILED);
        }

        return Arrays.stream(comments)
                .map(comment -> new GithubIssueComment(
                        comment.user() != null ? comment.user().login() : null,
                        comment.body()))
                .toList();
    }
}
