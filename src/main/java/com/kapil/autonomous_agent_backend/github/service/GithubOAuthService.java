package com.kapil.autonomous_agent_backend.github.service;

import com.kapil.autonomous_agent_backend.github.model.AgentToolConnection;

public interface GithubOAuthService {

    String buildAuthorizeUrl(String agentId);

    AgentToolConnection handleCallback(String code, String agentId);
}