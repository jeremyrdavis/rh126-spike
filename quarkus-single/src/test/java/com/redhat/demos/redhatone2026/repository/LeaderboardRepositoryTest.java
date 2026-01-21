package com.redhat.demos.redhatone2026.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardRepositoryTest {

    private LeaderboardRepository repository;

    @BeforeEach
    void setUp() {
        repository = new LeaderboardRepository();
    }

    @Test
    void shouldIncrementScoreOnFirstCorrectAnswer() {
        UUID questionId = UUID.randomUUID();

        repository.recordAnswer("user1", questionId, true);

        assertEquals(1, repository.getScore("user1"));
    }

    @Test
    void shouldNotIncrementScoreOnDuplicateCorrectAnswer() {
        UUID questionId = UUID.randomUUID();

        repository.recordAnswer("user1", questionId, true);
        repository.recordAnswer("user1", questionId, true);

        assertEquals(1, repository.getScore("user1"));
    }

    @Test
    void shouldNotIncrementScoreOnIncorrectAnswer() {
        UUID questionId = UUID.randomUUID();

        repository.recordAnswer("user1", questionId, false);

        assertEquals(0, repository.getScore("user1"));
    }

    @Test
    void shouldTrackMultipleUsersIndependently() {
        UUID question1 = UUID.randomUUID();
        UUID question2 = UUID.randomUUID();

        repository.recordAnswer("user1", question1, true);
        repository.recordAnswer("user2", question2, true);
        repository.recordAnswer("user1", question2, true);

        assertEquals(2, repository.getScore("user1"));
        assertEquals(1, repository.getScore("user2"));
    }

    @Test
    void shouldIncrementScoreForMultipleCorrectAnswersToDistinctQuestions() {
        UUID question1 = UUID.randomUUID();
        UUID question2 = UUID.randomUUID();
        UUID question3 = UUID.randomUUID();

        repository.recordAnswer("user1", question1, true);
        repository.recordAnswer("user1", question2, true);
        repository.recordAnswer("user1", question3, true);

        assertEquals(3, repository.getScore("user1"));
    }

    @Test
    void shouldReturnZeroForUserWithNoCorrectAnswers() {
        assertEquals(0, repository.getScore("newuser"));
    }

    @Test
    void shouldHandleMixOfCorrectAndIncorrectAnswers() {
        UUID question1 = UUID.randomUUID();
        UUID question2 = UUID.randomUUID();
        UUID question3 = UUID.randomUUID();

        repository.recordAnswer("user1", question1, true);
        repository.recordAnswer("user1", question2, false);
        repository.recordAnswer("user1", question3, true);
        repository.recordAnswer("user1", question1, true); // duplicate

        assertEquals(2, repository.getScore("user1"));
    }

    @Test
    void shouldReturnAllScores() {
        UUID question1 = UUID.randomUUID();
        UUID question2 = UUID.randomUUID();

        repository.recordAnswer("user1", question1, true);
        repository.recordAnswer("user2", question2, true);

        var allScores = repository.getAllScores();

        assertEquals(2, allScores.size());
        assertEquals(1, allScores.get("user1"));
        assertEquals(1, allScores.get("user2"));
    }

    @Test
    void shouldReturnQuestionsAnsweredCorrectlyForNewUser() {
        int count = repository.getQuestionsAnsweredCorrectly("newuser");
        assertEquals(0, count);
    }

    @Test
    void shouldReturnQuestionsAnsweredCorrectlyAfterCorrectAnswers() {
        UUID questionId1 = UUID.randomUUID();
        UUID questionId2 = UUID.randomUUID();

        repository.recordAnswer("alice", questionId1, true);
        repository.recordAnswer("alice", questionId2, true);

        int count = repository.getQuestionsAnsweredCorrectly("alice");
        assertEquals(2, count);
    }

    @Test
    void shouldNotCountDuplicateQuestionsAnsweredCorrectly() {
        UUID questionId = UUID.randomUUID();

        repository.recordAnswer("bob", questionId, true);
        repository.recordAnswer("bob", questionId, true);

        int count = repository.getQuestionsAnsweredCorrectly("bob");
        assertEquals(1, count);
    }

    @Test
    void shouldReturnDefensiveCopyOfAllScores() {
        UUID questionId = UUID.randomUUID();
        repository.recordAnswer("charlie", questionId, true);

        HashMap<String, Integer> scores1 = repository.getAllScores();
        HashMap<String, Integer> scores2 = repository.getAllScores();

        assertNotSame(scores1, scores2);
        assertEquals(scores1, scores2);

        scores1.put("tampered", 999);
        HashMap<String, Integer> scores3 = repository.getAllScores();

        assertFalse(scores3.containsKey("tampered"));
    }

    @Test
    void shouldReturnEmptyMapWhenNoUsers() {
        HashMap<String, Integer> scores = repository.getAllScores();
        assertTrue(scores.isEmpty());
    }
}
