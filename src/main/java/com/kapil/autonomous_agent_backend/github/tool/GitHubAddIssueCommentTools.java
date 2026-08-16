package com.kapil.autonomous_agent_backend.github.tool;

import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.exception.GitHubToolException;
import com.kapil.autonomous_agent_backend.github.dto.GithubIssueCommentResult;
import com.kapil.autonomous_agent_backend.github.dto.GithubPostedCommentApiResponse;
import com.kapil.autonomous_agent_backend.github.service.GithubOAuthService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class GitHubAddIssueCommentTools {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private final GithubOAuthService githubOAuthService;
    private final RestClient restClient;

    public GitHubAddIssueCommentTools(GithubOAuthService githubOAuthService) {
        this.githubOAuthService = githubOAuthService;
        this.restClient = RestClient.create();
    }

    @Tool(description = "Post a comment on a GitHub issue for a given repository")
    public GithubIssueCommentResult addIssueComment(@ToolParam(description = "The id of the agent whose GitHub connection should be used") String agentId,
                                                      @ToolParam(description = "The owner (user or organization) of the repository") String owner,
                                                      @ToolParam(description = "The repository name") String repo,
                                                      @ToolParam(description = "The issue number") int issueNumber,
                                                      @ToolParam(description = "The comment text to post") String commentBody) {
        String accessToken = githubOAuthService.resolveAccessToken(agentId);

        GithubPostedCommentApiResponse postedComment = postComment(accessToken, owner, repo, issueNumber, commentBody);

        return new GithubIssueCommentResult(postedComment.id(), postedComment.body(), postedComment.html_url());
    }

    private GithubPostedCommentApiResponse postComment(String accessToken, String owner, String repo, int issueNumber, String commentBody) {
        String uri = "%s/repos/%s/%s/issues/%d/comments".formatted(GITHUB_API_BASE_URL, owner, repo, issueNumber);

        GithubPostedCommentApiResponse postedComment = restClient.post()
                .uri(uri)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .body(Map.of("body", commentBody))
                .retrieve()
                .body(GithubPostedCommentApiResponse.class);

        if (postedComment == null) {
            throw new GitHubToolException(Constants.GITHUB_COMMENT_POST_FAILED);
        }

        return postedComment;
    }
}