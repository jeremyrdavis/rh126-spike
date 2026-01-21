package com.redhat.demos.redhatone2026.model;

/**
 * Immutable record representing a leaderboard entry for API responses.
 * Contains username, score, rank, and count of questions answered correctly.
 */
public record LeaderboardEntry(
    String username,
    int score,
    int rank,
    int questionsAnsweredCorrectly
) {
}
