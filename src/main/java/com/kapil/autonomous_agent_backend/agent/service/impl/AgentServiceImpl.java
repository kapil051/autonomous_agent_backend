package com.kapil.autonomous_agent_backend.agent.service.impl;

import com.kapil.autonomous_agent_backend.agent.model.Agent;
import com.kapil.autonomous_agent_backend.agent.repository.AgentRepository;
import com.kapil.autonomous_agent_backend.agent.service.AgentService;
import com.kapil.autonomous_agent_backend.constant.Constants;
import com.kapil.autonomous_agent_backend.exception.AgentException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;

    public AgentServiceImpl(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Override
    public Agent createAgent(String name) {
        if (name == null || name.isBlank()) {
            throw new AgentException(Constants.AGENT_NAME_REQUIRED, HttpStatus.BAD_REQUEST);
        }

        if (agentRepository.existsByName(name)) {
            throw new AgentException(Constants.AGENT_ALREADY_EXISTS, HttpStatus.CONFLICT);
        }

        Agent agent = Agent.builder().name(name).build();
        return agentRepository.save(agent);
    }
}