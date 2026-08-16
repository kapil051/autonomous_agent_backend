package com.kapil.autonomous_agent_backend.github.service;

public interface EncryptionService {

    String encrypt(String plainText);

    String decrypt(String encryptedText);
}