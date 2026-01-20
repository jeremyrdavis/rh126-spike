package com.redhat.demos.service;

import com.redhat.demos.model.LeaderboardEntry;
import com.redhat.demos.repository.LeaderboardRepository;
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
        HashMap<String, Integer> allScores = leaderboardRepository.getAllScores();

        if (allScores.isEmpty()) {
            return List.of();
        }

        List<LeaderboardEntry> entries = allScores.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    int score = entry.getValue();
                    int questionsAnsweredCorrectly = leaderboardRepository.getQuestionsAnsweredCorrectly(username);
                    return new LeaderboardEntry(username, score, 0, questionsAnsweredCorrectly);
                })
                .sorted(Comparator.comparing(LeaderboardEntry::score).reversed()
                        .thenComparing(LeaderboardEntry::username))
                .collect(Collectors.toList());

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

        return rankedEntries;
    }
}
