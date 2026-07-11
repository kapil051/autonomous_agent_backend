package com.kapil.autonomous_agent_backend.github.controller;

import com.kapil.autonomous_agent_backend.github.model.AgentToolConnection;
import com.kapil.autonomous_agent_backend.github.service.GithubOAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/agent/tools/github")
public class GithubConnectionController {

    private final GithubOAuthService githubOAuthService;

    public GithubConnectionController(GithubOAuthService githubOAuthService) {
        this.githubOAuthService = githubOAuthService;
    }

    @GetMapping("/connect/{agentId}")
    public ResponseEntity<Void> connect(@PathVariable String agentId) {
        String authorizeUrl = githubOAuthService.buildAuthorizeUrl(agentId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(authorizeUrl))
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam String code, @RequestParam String state) {
        AgentToolConnection connection = githubOAuthService.handleCallback(code, state);
        return ResponseEntity.ok("GitHub account '" + connection.getGithubLogin() + "' connected successfully for agent " + connection.getAgentId());
    }
}