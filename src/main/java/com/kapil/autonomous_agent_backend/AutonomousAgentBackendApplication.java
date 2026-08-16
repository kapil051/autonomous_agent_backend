package com.kapil.autonomous_agent_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class AutonomousAgentBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutonomousAgentBackendApplication.class, args);
	}

}
