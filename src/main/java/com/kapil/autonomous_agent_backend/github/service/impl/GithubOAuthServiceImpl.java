package com.kapil.autonomous_agent_backend.github.service.impl;

import com.kapil.autonomous_agent_backend.agent.repository.AgentRepository;
import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.exception.GitHubOAuthException;
import com.kapil.autonomous_agent_backend.github.dto.GithubTokenResponse;
import com.kapil.autonomous_agent_backend.github.dto.GithubUserResponse;
import com.kapil.autonomous_agent_backend.github.model.AgentToolConnection;
import com.kapil.autonomous_agent_backend.github.repository.AgentToolConnectionRepository;
import com.kapil.autonomous_agent_backend.github.service.EncryptionService;
import com.kapil.autonomous_agent_backend.github.service.GithubOAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class GithubOAuthServiceImpl implements GithubOAuthService {

    private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize";
    private static final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String USER_URL = "https://api.github.com/user";

    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_CLIENT_SECRET = "client_secret";
    private static final String PARAM_CODE = "code";
    private static final String PARAM_REDIRECT_URI = "redirect_uri";
    private static final String PARAM_SCOPE = "scope";
    private static final String PARAM_STATE = "state";

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    @Value("${github.redirect-uri}")
    private String redirectUri;

    @Value("${github.scope}")
    private String scope;

    private final AgentRepository agentRepository;
    private final AgentToolConnectionRepository agentToolConnectionRepository;
    private final EncryptionService encryptionService;
    private final RestClient restClient;

    public GithubOAuthServiceImpl(AgentRepository agentRepository,
                                   AgentToolConnectionRepository agentToolConnectionRepository,
                                   EncryptionService encryptionService) {
        this.agentRepository = agentRepository;
        this.agentToolConnectionRepository = agentToolConnectionRepository;
        this.encryptionService = encryptionService;
        this.restClient = RestClient.create();
    }

    @Override
    public String buildAuthorizeUrl(String agentId) {
        return UriComponentsBuilder.fromUriString(AUTHORIZE_URL)
                .queryParam(PARAM_CLIENT_ID, clientId)
                .queryParam(PARAM_REDIRECT_URI, redirectUri)
                .queryParam(PARAM_SCOPE, scope)
                .queryParam(PARAM_STATE, agentId)
                .build()
                .toUriString();
    }

    @Override
    public AgentToolConnection handleCallback(String code, String agentId) {
        if (!agentRepository.existsById(agentId)) {
            throw new GitHubOAuthException(Constants.GITHUB_OAUTH_INVALID_STATE, HttpStatus.BAD_REQUEST);
        }

        String accessToken = exchangeCodeForToken(code);
        String githubLogin = fetchGithubUsername(accessToken);

        AgentToolConnection connection = agentToolConnectionRepository.findByAgentId(agentId)
                .orElseGet(() -> AgentToolConnection.builder().agentId(agentId).build());

        connection.setGithubLogin(githubLogin);
        connection.setAccessToken(encryptionService.encrypt(accessToken));

        return agentToolConnectionRepository.save(connection);
    }

    @Override
    public String resolveAccessToken(String agentId) {
        AgentToolConnection connection = agentToolConnectionRepository.findByAgentId(agentId)
                .orElseThrow(() -> new GitHubOAuthException(Constants.GITHUB_CONNECTION_NOT_FOUND, HttpStatus.NOT_FOUND));

        return encryptionService.decrypt(connection.getAccessToken());
    }

    private String exchangeCodeForToken(String code) {
        GithubTokenResponse tokenResponse = restClient.post()
                .uri(TOKEN_URL)
                .header("Accept", "application/json")
                .body(Map.of(
                        PARAM_CLIENT_ID, clientId,
                        PARAM_CLIENT_SECRET, clientSecret,
                        PARAM_CODE, code,
                        PARAM_REDIRECT_URI, redirectUri
                ))
                .retrieve()
                .body(GithubTokenResponse.class);

        if (tokenResponse == null || tokenResponse.access_token() == null) {
            throw new GitHubOAuthException(Constants.GITHUB_TOKEN_EXCHANGE_FAILED);
        }

        return tokenResponse.access_token();
    }

    private String fetchGithubUsername(String accessToken) {
        GithubUserResponse userResponse = restClient.get()
                .uri(USER_URL)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .body(GithubUserResponse.class);

        if (userResponse == null || userResponse.login() == null) {
            throw new GitHubOAuthException(Constants.GITHUB_USER_FETCH_FAILED);
        }

        return userResponse.login();
    }
}