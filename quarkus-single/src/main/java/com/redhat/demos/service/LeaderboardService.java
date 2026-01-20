package com.redhat.demos.service;

import com.redhat.demos.repository.LeaderboardRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

/**
 * Service layer for leaderboard business logic.
 * Delegates to LeaderboardRepository for data access.
 */
@ApplicationScoped
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    public LeaderboardService(LeaderboardRepository leaderboardRepository) {
        if (leaderboardRepository == null) {
            throw new IllegalArgumentException("LeaderboardRepository cannot be null");
        }
        this.leaderboardRepository = leaderboardRepository;
    }

    /**
     * Records an answer submission for a user.
     * Delegates to the repository for persistence.
     *
     * @param username the username
     * @param questionId the question ID
     * @param isCorrect whether the answer was correct
     */
    public void recordAnswer(String username, UUID questionId, boolean isCorrect) {
        leaderboardRepository.recordAnswer(username, questionId, isCorrect);
    }
}
