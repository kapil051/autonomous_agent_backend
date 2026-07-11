package com.kapil.autonomous_agent_backend.github.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "agent_tool_connections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentToolConnection {

    @Id
    private String id;

    private String agentId;

    private String githubLogin;

    private String accessToken;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}