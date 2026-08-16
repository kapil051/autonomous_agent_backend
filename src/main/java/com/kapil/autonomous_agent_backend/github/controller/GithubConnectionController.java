package com.kapil.autonomous_agent_backend.github.controller;

import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.dto.response.ApiResponse;
import com.kapil.autonomous_agent_backend.github.model.AgentToolConnection;
import com.kapil.autonomous_agent_backend.github.service.GithubOAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/agent/tools/github")
public class GithubConnectionController {

    private final GithubOAuthService githubOAuthService;

    public GithubConnectionController(GithubOAuthService githubOAuthService) {
        this.githubOAuthService = githubOAuthService;
    }

    @GetMapping("/connect/{agentId}")
    public ResponseEntity<ApiResponse> connect(@PathVariable String agentId) {
        String authorizeUrl = githubOAuthService.buildAuthorizeUrl(agentId);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(Constants.GITHUB_AUTH_URL_GENERATED).status(true)
                .data(Map.of("authUrl", authorizeUrl))
                .build());
    }

    @GetMapping("/callback")
    public ResponseEntity<ApiResponse> callback(@RequestParam String code, @RequestParam String state) {
        AgentToolConnection connection = githubOAuthService.handleCallback(code, state);
        return ResponseEntity.ok(ApiResponse.builder()
                .code(Constants.GITHUB_CONNECT_SUCCESS).status(true)
                .data(Map.of(
                        "agentId", connection.getAgentId(),
                        "githubLogin", connection.getGithubLogin()
                ))
                .build());
    }
}