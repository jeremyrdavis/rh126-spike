package com.redhat.demos.redhatone2026.service;

import com.redhat.demos.redhatone2026.model.LeaderboardEntry;
import com.redhat.demos.redhatone2026.repository.LeaderboardRepository;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LeaderboardServiceTest {

    @Test
    void shouldThrowExceptionWhenRepositoryIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new LeaderboardService(null)
        );

        assertEquals("LeaderboardRepository cannot be null", exception.getMessage());
    }

    @Test
    void shouldDelegateRecordAnswerToRepository() {
        LeaderboardRepository mockRepository = mock(LeaderboardRepository.class);
        LeaderboardService service = new LeaderboardService(mockRepository);
        UUID questionId = UUID.randomUUID();

        service.recordAnswer("testuser", questionId, true);

        verify(mockRepository, times(1)).recordAnswer("testuser", questionId, true);
    }

    @Test
    void shouldDelegateIncorrectAnswerToRepository() {
        LeaderboardRepository mockRepository = mock(LeaderboardRepository.class);
        LeaderboardService service = new LeaderboardService(mockRepository);
        UUID questionId = UUID.randomUUID();

        service.recordAnswer("testuser", questionId, false);

        verify(mockRepository, times(1)).recordAnswer("testuser", questionId, false);
    }

    @Test
    void shouldDelegateMultipleRecordAnswerCalls() {
        LeaderboardRepository mockRepository = mock(LeaderboardRepository.class);
        LeaderboardService service = new LeaderboardService(mockRepository);
        UUID question1 = UUID.randomUUID();
        UUID question2 = UUID.randomUUID();

        service.recordAnswer("user1", question1, true);
        service.recordAnswer("user2", question2, false);

        verify(mockRepository, times(1)).recordAnswer("user1", question1, true);
        verify(mockRepository, times(1)).recordAnswer("user2", question2, false);
    }

    @Test
    void shouldReturnEmptyLeaderboardWhenNoUsers() {
        LeaderboardRepository mockRepository = mock(LeaderboardRepository.class);
        when(mockRepository.getAllScores()).thenReturn(new HashMap<>());

        LeaderboardService service = new LeaderboardService(mockRepository);
        List<LeaderboardEntry> leaderboard = service.getLeaderboard();

        assertTrue(leaderboard.isEmpty());
    }

    @Test
    void shouldSortLeaderboardByScoreDescending() {
        LeaderboardRepository mockRepository = mock(LeaderboardRepository.class);
        HashMap<String, Integer> scores = new HashMap<>();
        scores.put("alice", 15);
        scores.put("bob", 20);
        scores.put("charlie", 10);

        when(mockRepository.getAllScores()).thenReturn(scores);
        when(mockRepository.getQuestionsAnsweredCorrectly("alice")).thenReturn(15);
        when(mockRepository.getQuestionsAnsweredCorrectly("bob")).thenReturn(20);
        when(mockRepository.getQuestionsAnsweredCorrectly("charlie")).thenReturn(10);

        LeaderboardService service = new LeaderboardService(mockRepository);
        List<LeaderboardEntry> leaderboard = service.getLeaderboard();

        assertEquals(3, leaderboard.size());
        assertEquals("bob", leaderboard.get(0).username());
        assertEquals("alice", leaderboard.get(1).username());
        assertEquals("charlie", leaderboard.get(2).username());
    }

    @Test
    void shouldApplyAlphabeticalTiebreaker() {
        LeaderboardRepository mockRepository = mock(LeaderboardRepository.class);
        HashMap<String, Integer> scores = new HashMap<>();
        scores.put("charlie", 10);
        scores.put("alice", 10);
        scores.put("bob", 10);

        when(mockRepository.getAllScores()).thenReturn(scores);
        when(mockRepository.getQuestionsAnsweredCorrectly("alice")).thenReturn(10);
        when(mockRepository.getQuestionsAnsweredCorrectly("bob")).thenReturn(10);
        when(mockRepository.getQuestionsAnsweredCorrectly("charlie")).thenReturn(10);

        LeaderboardService service = new LeaderboardService(mockRepository);
        List<LeaderboardEntry> leaderboard = service.getLeaderboard();

        assertEquals(3, leaderboard.size());
        assertEquals("alice", leaderboard.get(0).username());
        assertEquals("bob", leaderboard.get(1).username());
        assertEquals("charlie", leaderboard.get(2).username());
    }

    @Test
    void shouldAssignSequentialRanks() {
        LeaderboardRepository mockRepository = mock(LeaderboardRepository.class);
        HashMap<String, Integer> scores = new HashMap<>();
        scores.put("alice", 15);
        scores.put("bob", 12);
        scores.put("charlie", 12);

        when(mockRepository.getAllScores()).thenReturn(scores);
        when(mockRepository.getQuestionsAnsweredCorrectly("alice")).thenReturn(15);
        when(mockRepository.getQuestionsAnsweredCorrectly("bob")).thenReturn(12);
        when(mockRepository.getQuestionsAnsweredCorrectly("charlie")).thenReturn(12);

        LeaderboardService service = new LeaderboardService(mockRepository);
        List<LeaderboardEntry> leaderboard = service.getLeaderboard();

        assertEquals(1, leaderboard.get(0).rank());
        assertEquals(2, leaderboard.get(1).rank());
        assertEquals(3, leaderboard.get(2).rank());
    }

    @Test
    void shouldIncludeQuestionsAnsweredCorrectlyCount() {
        LeaderboardRepository mockRepository = mock(LeaderboardRepository.class);
        HashMap<String, Integer> scores = new HashMap<>();
        scores.put("alice", 15);

        when(mockRepository.getAllScores()).thenReturn(scores);
        when(mockRepository.getQuestionsAnsweredCorrectly("alice")).thenReturn(15);

        LeaderboardService service = new LeaderboardService(mockRepository);
        List<LeaderboardEntry> leaderboard = service.getLeaderboard();

        assertEquals(15, leaderboard.get(0).questionsAnsweredCorrectly());
    }
}
