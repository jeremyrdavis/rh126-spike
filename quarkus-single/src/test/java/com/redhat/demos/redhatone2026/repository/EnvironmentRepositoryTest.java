package com.redhat.demos.redhatone2026.repository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EnvironmentRepositoryTest {

    @Inject
    EnvironmentRepository environmentRepository;

    @Test
    void shouldReturnNonNullMessage() {
        String message = environmentRepository.getRandomMessage();

        assertNotNull(message);
        assertFalse(message.isEmpty());
    }

    @Test
    void shouldReturnOneOfFourExpectedMessages() {
        String message = environmentRepository.getRandomMessage();

        assertNotNull(message);
        assertTrue(message.contains("Quarkus"),
            "Message should contain information about Quarkus");
    }

    @Test
    void shouldDemonstrateRandomization() {
        Set<String> uniqueMessages = new HashSet<>();

        // Call getRandomMessage multiple times to collect unique messages
        for (int i = 0; i < 50; i++) {
            String message = environmentRepository.getRandomMessage();
            uniqueMessages.add(message);
        }

        // Should have found at least 2 different messages (highly probable with 50 calls)
        assertTrue(uniqueMessages.size() >= 2,
            "Expected to see at least 2 different messages from randomization");
    }

    @Test
    void shouldReturnConsistentLengthMessages() {
        String message = environmentRepository.getRandomMessage();

        assertNotNull(message);
        assertTrue(message.length() > 10,
            "Message should be reasonably descriptive (more than 10 characters)");
    }
}
