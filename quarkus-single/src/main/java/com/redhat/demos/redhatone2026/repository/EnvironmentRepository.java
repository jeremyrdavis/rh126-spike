package com.redhat.demos.redhatone2026.repository;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Repository for managing randomized Quarkus informational messages.
 * Provides four hardcoded messages that are selected randomly.
 */
@ApplicationScoped
public class EnvironmentRepository {

    private static final String MESSAGE_1 = "This question served by JBoss EAP 8 monolithic on OpenShift Virt.";
    private static final String MESSAGE_2 = "This question served by JBoss EAP 8 on OpenShitft Virt in combination with a Quarkus microservice on Kubernetes.";
    private static final String MESSAGE_3 = "This question served by Quarkus microservices on OpenShift.";
    private static final String MESSAGE_4 = "This question served by Quarkus microservices on OpenShift with the help of HuggingFaceH4/zephyr-7b-beta on OpenShift AI.";

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
