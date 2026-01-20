package com.redhat.demos.service;

import com.redhat.demos.repository.LeaderboardRepository;
import org.junit.jupiter.api.Test;

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
}
