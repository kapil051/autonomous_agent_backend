package com.kapil.autonomous_agent_backend.agent.controller;

import com.kapil.autonomous_agent_backend.agent.dto.request.CreateAgentRequest;
import com.kapil.autonomous_agent_backend.agent.dto.response.AgentResponse;
import com.kapil.autonomous_agent_backend.agent.model.Agent;
import com.kapil.autonomous_agent_backend.agent.service.AgentService;
import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createAgent(@RequestBody CreateAgentRequest request) {
        Agent agent = agentService.createAgent(request.name());
        return ResponseEntity.ok(ApiResponse.builder()
                .code(Constants.AGENT_CREATED).status(true)
                .data(new AgentResponse(agent.getId(), agent.getName()))
                .build());
    }
}