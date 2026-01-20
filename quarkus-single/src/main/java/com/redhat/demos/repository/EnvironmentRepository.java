package com.redhat.demos.repository;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Repository for managing randomized Quarkus informational messages.
 * Provides four hardcoded messages that are selected randomly.
 */
@ApplicationScoped
public class EnvironmentRepository {

    private static final String MESSAGE_1 = "Quarkus enables live reload in dev mode for instant feedback during development";
    private static final String MESSAGE_2 = "Quarkus delivers supersonic startup time and incredibly low memory footprint";
    private static final String MESSAGE_3 = "Quarkus provides developer joy with unified configuration and simplified testing";
    private static final String MESSAGE_4 = "Quarkus offers seamless integration with Kubernetes and cloud-native platforms";

    /**
     * Retrieves a random Quarkus informational message from the available set.
     *
     * @return A randomly selected message string
     */
    public String getRandomMessage() {
        int randomIndex = ThreadLocalRandom.current().nextInt(4);
        return switch (randomIndex) {
            case 0 -> MESSAGE_1;
            case 1 -> MESSAGE_2;
            case 2 -> MESSAGE_3;
            case 3 -> MESSAGE_4;
            default -> MESSAGE_1; // Fallback (should never occur)
        };
    }
}
