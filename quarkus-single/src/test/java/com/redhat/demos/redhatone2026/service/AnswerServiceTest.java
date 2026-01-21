package com.redhat.demos.redhatone2026.service;

import com.redhat.demos.redhatone2026.model.*;
import com.redhat.demos.redhatone2026.repository.EnvironmentRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnswerServiceTest {

    @Test
    void shouldThrowExceptionWhenQuestionServiceIsNull() {
        LeaderboardService leaderboardService = mock(LeaderboardService.class);
        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new AnswerService(null, leaderboardService, environmentRepository)
        );

        assertEquals("QuestionService cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLeaderboardServiceIsNull() {
        QuestionService questionService = mock(QuestionService.class);
        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new AnswerService(questionService, null, environmentRepository)
        );

        assertEquals("LeaderboardService cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEnvironmentRepositoryIsNull() {
        QuestionService questionService = mock(QuestionService.class);
        LeaderboardService leaderboardService = mock(LeaderboardService.class);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new AnswerService(questionService, leaderboardService, null)
        );

        assertEquals("EnvironmentRepository cannot be null", exception.getMessage());
    }

    @Test
    void shouldValidateCorrectAnswer() {
        QuestionService questionService = mock(QuestionService.class);
        LeaderboardService leaderboardService = mock(LeaderboardService.class);
        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
        AnswerService service = new AnswerService(questionService, leaderboardService, environmentRepository);

        UUID questionId = UUID.randomUUID();
        Answer answer1 = new Answer(UUID.randomUUID(), "Paris", true);
        Answer answer2 = new Answer(UUID.randomUUID(), "London", false);
        Answer answer3 = new Answer(UUID.randomUUID(), "Berlin", false);
        Answer answer4 = new Answer(UUID.randomUUID(), "Madrid", false);
        Question question = new Question(questionId, "Capital of France?", List.of(answer1, answer2, answer3, answer4));

        TriviaQuestion nextQuestion = new TriviaQuestion(UUID.randomUUID(), "Next?", "A", "B", "C", "D", "Msg");

        when(questionService.getQuestionById(questionId)).thenReturn(Optional.of(question));
        when(questionService.getRandomTriviaQuestion()).thenReturn(Optional.of(nextQuestion));

        AnswerSubmission submission = new AnswerSubmission("testuser", questionId, "A");
        Optional<AnswerResponse> response = service.submitAnswer(submission);

        assertTrue(response.isPresent());
        assertTrue(response.get().isCorrect());
        assertEquals("A", response.get().correctAnswer());
        assertEquals(question, response.get().originalQuestion());
        assertEquals(nextQuestion, response.get().nextQuestion());
        verify(leaderboardService, times(1)).recordAnswer("testuser", questionId, true);
    }

    @Test
    void shouldValidateIncorrectAnswer() {
        QuestionService questionService = mock(QuestionService.class);
        LeaderboardService leaderboardService = mock(LeaderboardService.class);
        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
        AnswerService service = new AnswerService(questionService, leaderboardService, environmentRepository);

        UUID questionId = UUID.randomUUID();
        Answer answer1 = new Answer(UUID.randomUUID(), "Paris", false);
        Answer answer2 = new Answer(UUID.randomUUID(), "London", true);
        Answer answer3 = new Answer(UUID.randomUUID(), "Berlin", false);
        Answer answer4 = new Answer(UUID.randomUUID(), "Madrid", false);
        Question question = new Question(questionId, "Capital of UK?", List.of(answer1, answer2, answer3, answer4));

        TriviaQuestion nextQuestion = new TriviaQuestion(UUID.randomUUID(), "Next?", "A", "B", "C", "D", "Msg");

        when(questionService.getQuestionById(questionId)).thenReturn(Optional.of(question));
        when(questionService.getRandomTriviaQuestion()).thenReturn(Optional.of(nextQuestion));

        AnswerSubmission submission = new AnswerSubmission("testuser", questionId, "A");
        Optional<AnswerResponse> response = service.submitAnswer(submission);

        assertTrue(response.isPresent());
        assertFalse(response.get().isCorrect());
        assertEquals("B", response.get().correctAnswer());
        verify(leaderboardService, times(1)).recordAnswer("testuser", questionId, false);
    }

    @Test
    void shouldThrowExceptionForInvalidUsername() {
        QuestionService questionService = mock(QuestionService.class);
        LeaderboardService leaderboardService = mock(LeaderboardService.class);
        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
        AnswerService service = new AnswerService(questionService, leaderboardService, environmentRepository);

        UUID questionId = UUID.randomUUID();
        AnswerSubmission submission = new AnswerSubmission("", questionId, "A");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.submitAnswer(submission)
        );

        assertEquals("Invalid username", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullQuestionId() {
        QuestionService questionService = mock(QuestionService.class);
        LeaderboardService leaderboardService = mock(LeaderboardService.class);
        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
        AnswerService service = new AnswerService(questionService, leaderboardService, environmentRepository);

        AnswerSubmission submission = new AnswerSubmission("user", null, "A");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.submitAnswer(submission)
        );

        assertEquals("Invalid questionId", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidSelectedAnswer() {
        QuestionService questionService = mock(QuestionService.class);
        LeaderboardService leaderboardService = mock(LeaderboardService.class);
        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
        AnswerService service = new AnswerService(questionService, leaderboardService, environmentRepository);

        UUID questionId = UUID.randomUUID();
        AnswerSubmission submission = new AnswerSubmission("user", questionId, "Z");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.submitAnswer(submission)
        );

        assertEquals("Invalid answer selection", exception.getMessage());
    }

    @Test
    void shouldReturnEmptyWhenQuestionNotFound() {
        QuestionService questionService = mock(QuestionService.class);
        LeaderboardService leaderboardService = mock(LeaderboardService.class);
        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
        AnswerService service = new AnswerService(questionService, leaderboardService, environmentRepository);

        UUID questionId = UUID.randomUUID();
        when(questionService.getQuestionById(questionId)).thenReturn(Optional.empty());

        AnswerSubmission submission = new AnswerSubmission("user", questionId, "A");
        Optional<AnswerResponse> response = service.submitAnswer(submission);

        assertTrue(response.isEmpty());
    }

    @Test
    void shouldIncludeNextQuestionInResponse() {
        QuestionService questionService = mock(QuestionService.class);
        LeaderboardService leaderboardService = mock(LeaderboardService.class);
        EnvironmentRepository environmentRepository = mock(EnvironmentRepository.class);
        AnswerService service = new AnswerService(questionService, leaderboardService, environmentRepository);

        UUID questionId = UUID.randomUUID();
        Answer answer1 = new Answer(UUID.randomUUID(), "A1", true);
        Answer answer2 = new Answer(UUID.randomUUID(), "A2", false);
        Answer answer3 = new Answer(UUID.randomUUID(), "A3", false);
        Answer answer4 = new Answer(UUID.randomUUID(), "A4", false);
        Question question = new Question(questionId, "Q1?", List.of(answer1, answer2, answer3, answer4));

        TriviaQuestion nextQuestion = new TriviaQuestion(UUID.randomUUID(), "Q2?", "B1", "B2", "B3", "B4", "Env");

        when(questionService.getQuestionById(questionId)).thenReturn(Optional.of(question));
        when(questionService.getRandomTriviaQuestion()).thenReturn(Optional.of(nextQuestion));

        AnswerSubmission submission = new AnswerSubmission("user", questionId, "A");
        Optional<AnswerResponse> response = service.submitAnswer(submission);

        assertTrue(response.isPresent());
        assertNotNull(response.get().nextQuestion());
        assertEquals(nextQuestion, response.get().nextQuestion());
    }
}
