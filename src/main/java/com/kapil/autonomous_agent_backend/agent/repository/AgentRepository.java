package com.kapil.autonomous_agent_backend.agent.repository;

import com.kapil.autonomous_agent_backend.agent.model.Agent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AgentRepository extends MongoRepository<Agent, String> {

    boolean existsByName(String name);
}