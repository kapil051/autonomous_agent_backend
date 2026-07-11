package com.kapil.autonomous_agent_backend.github.repository;

import com.kapil.autonomous_agent_backend.github.model.AgentToolConnection;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AgentToolConnectionRepository extends MongoRepository<AgentToolConnection, String> {

    Optional<AgentToolConnection> findByAgentId(String agentId);
}