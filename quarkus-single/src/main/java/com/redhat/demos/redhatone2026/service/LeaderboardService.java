package com.redhat.demos.redhatone2026.service;

import com.redhat.demos.redhatone2026.model.LeaderboardEntry;
import com.redhat.demos.redhatone2026.repository.LeaderboardRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.stream.Collectors;

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
        Log.debugf("Recording answer: username=%s, questionId=%s, isCorrect=%s", username, questionId, isCorrect);
        leaderboardRepository.recordAnswer(username, questionId, isCorrect);
    }

    /**
     * Retrieves the complete leaderboard with rankings.
     * Sorted by score descending, then username ascending as tiebreaker.
     * Ranks are calculated sequentially based on sorted position.
     *
     * @return List of LeaderboardEntry objects sorted and ranked
     */
    public List<LeaderboardEntry> getLeaderboard() {
        Log.debug("Building leaderboard");
        HashMap<String, Integer> allScores = leaderboardRepository.getAllScores();
        Log.debugf("Retrieved %d user scores from repository", allScores.size());

        if (allScores.isEmpty()) {
            Log.debug("No scores available, returning empty leaderboard");
            return List.of();
        }

        List<LeaderboardEntry> entries = allScores.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    int score = entry.getValue();
                    int questionsAnsweredCorrectly = leaderboardRepository.getQuestionsAnsweredCorrectly(username);
                    Log.debugf("User %s: score=%d, questionsCorrect=%d", username, score, questionsAnsweredCorrectly);
                    return new LeaderboardEntry(username, score, 0, questionsAnsweredCorrectly);
                })
                .sorted(Comparator.comparing(LeaderboardEntry::score).reversed()
                        .thenComparing(LeaderboardEntry::username))
                .collect(Collectors.toList());

        Log.debugf("Sorted %d entries, assigning ranks", entries.size());
        List<LeaderboardEntry> rankedEntries = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry entry = entries.get(i);
            rankedEntries.add(new LeaderboardEntry(
                    entry.username(),
                    entry.score(),
                    i + 1,
                    entry.questionsAnsweredCorrectly()
            ));
        }

        Log.debugf("Returning leaderboard with %d ranked entries", rankedEntries.size());
        return rankedEntries;
    }
}
