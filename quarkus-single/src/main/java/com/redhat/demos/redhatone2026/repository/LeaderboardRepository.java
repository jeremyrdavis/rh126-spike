package com.redhat.demos.redhatone2026.repository;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Repository for managing leaderboard data using in-memory HashMap storage.
 * Tracks user scores and which questions each user has answered correctly.
 */
@ApplicationScoped
public class LeaderboardRepository {

    private final HashMap<String, Set<UUID>> userCorrectQuestions;
    private final HashMap<String, Integer> userScores;

    public LeaderboardRepository() {
        this.userCorrectQuestions = new HashMap<>();
        this.userScores = new HashMap<>();
    }

    /**
     * Records an answer submission for a user.
     * If the answer is correct and this is the first correct answer for this question,
     * increments the user's score. Duplicate correct answers do not increment score.
     * Incorrect answers have no effect on score.
     *
     * @param username the username
     * @param questionId the question ID
     * @param isCorrect whether the answer was correct
     */
    public void recordAnswer(String username, UUID questionId, boolean isCorrect) {
        Log.debugf("LeaderboardRepository.recordAnswer: username=%s, questionId=%s, isCorrect=%s",
            username, questionId, isCorrect);

        if (!isCorrect) {
            Log.debugf("Answer was incorrect, no score update for %s", username);
            return;
        }

        Set<UUID> correctQuestions = userCorrectQuestions.computeIfAbsent(username, k -> new HashSet<>());
        boolean isNewCorrectAnswer = correctQuestions.add(questionId);

        if (isNewCorrectAnswer) {
            userScores.merge(username, 1, Integer::sum);
            int newScore = userScores.get(username);
            Log.debugf("First correct answer for question %s by %s. New score: %d", questionId, username, newScore);
        } else {
            Log.debugf("Duplicate correct answer for question %s by %s. Score unchanged.", questionId, username);
        }
    }

    /**
     * Retrieves the score for a user.
     *
     * @param username the username
     * @return the user's score, or 0 if the user has not answered any questions correctly
     */
    public int getScore(String username) {
        return userScores.getOrDefault(username, 0);
    }

    /**
     * Retrieves all user scores.
     *
     * @return HashMap mapping usernames to scores
     */
    public HashMap<String, Integer> getAllScores() {
        return new HashMap<>(userScores);
    }

    /**
     * Retrieves the count of questions answered correctly for a user.
     *
     * @param username the username
     * @return the number of unique questions answered correctly, or 0 if the user has not answered any questions correctly
     */
    public int getQuestionsAnsweredCorrectly(String username) {
        return userCorrectQuestions.getOrDefault(username, Set.of()).size();
    }
}
