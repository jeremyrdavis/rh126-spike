package com.redhat.demos.redhatone2026;

import com.redhat.demos.redhatone2026.model.*;
import com.redhat.demos.redhatone2026.service.AnswerService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnswerResourceTest {

    @Test
    void shouldThrowExceptionWhenServiceIsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new AnswerResource(null)
        );

        assertEquals("AnswerService cannot be null", exception.getMessage());
    }

    @Test
    void shouldReturnHttp200WithValidSubmission() {
        AnswerService mockService = mock(AnswerService.class);
        AnswerResource resource = new AnswerResource(mockService);

        UUID questionId = UUID.randomUUID();
        Answer answer1 = new Answer(UUID.randomUUID(), "Paris", true);
        Answer answer2 = new Answer(UUID.randomUUID(), "London", false);
        Answer answer3 = new Answer(UUID.randomUUID(), "Berlin", false);
        Answer answer4 = new Answer(UUID.randomUUID(), "Madrid", false);
        Question question = new Question(questionId, "Capital?", List.of(answer1, answer2, answer3, answer4));
        TriviaQuestion nextQuestion = new TriviaQuestion(UUID.randomUUID(), "Next?", "A", "B", "C", "D", "Msg");
        AnswerResponse answerResponse = new AnswerResponse(true, "A", question, nextQuestion);

        AnswerSubmission submission = new AnswerSubmission("user", questionId, "A");
        when(mockService.submitAnswer(submission)).thenReturn(Optional.of(answerResponse));

        Response response = resource.submitAnswer(submission);

        assertEquals(200, response.getStatus());
        assertEquals(answerResponse, response.getEntity());
    }

    @Test
    void shouldReturnHttp404WhenQuestionNotFound() {
        AnswerService mockService = mock(AnswerService.class);
        AnswerResource resource = new AnswerResource(mockService);

        UUID questionId = UUID.randomUUID();
        AnswerSubmission submission = new AnswerSubmission("user", questionId, "A");
        when(mockService.submitAnswer(submission)).thenReturn(Optional.empty());

        Response response = resource.submitAnswer(submission);

        assertEquals(404, response.getStatus());
        assertEquals("Question not found", response.getEntity());
    }

    @Test
    void shouldReturnHttp400ForInvalidUsername() {
        AnswerService mockService = mock(AnswerService.class);
        AnswerResource resource = new AnswerResource(mockService);

        UUID questionId = UUID.randomUUID();
        AnswerSubmission submission = new AnswerSubmission("", questionId, "A");
        when(mockService.submitAnswer(submission))
            .thenThrow(new IllegalArgumentException("Invalid username"));

        Response response = resource.submitAnswer(submission);

        assertEquals(400, response.getStatus());
        assertEquals("Invalid username", response.getEntity());
    }

    @Test
    void shouldReturnHttp400ForInvalidQuestionId() {
        AnswerService mockService = mock(AnswerService.class);
        AnswerResource resource = new AnswerResource(mockService);

        AnswerSubmission submission = new AnswerSubmission("user", null, "A");
        when(mockService.submitAnswer(submission))
            .thenThrow(new IllegalArgumentException("Invalid questionId"));

        Response response = resource.submitAnswer(submission);

        assertEquals(400, response.getStatus());
        assertEquals("Invalid questionId", response.getEntity());
    }

    @Test
    void shouldReturnHttp400ForInvalidSelectedAnswer() {
        AnswerService mockService = mock(AnswerService.class);
        AnswerResource resource = new AnswerResource(mockService);

        UUID questionId = UUID.randomUUID();
        AnswerSubmission submission = new AnswerSubmission("user", questionId, "Z");
        when(mockService.submitAnswer(submission))
            .thenThrow(new IllegalArgumentException("Invalid answer selection"));

        Response response = resource.submitAnswer(submission);

        assertEquals(400, response.getStatus());
        assertEquals("Invalid answer selection", response.getEntity());
    }

    @Test
    void shouldReturnHttp500ForUnexpectedError() {
        AnswerService mockService = mock(AnswerService.class);
        AnswerResource resource = new AnswerResource(mockService);

        UUID questionId = UUID.randomUUID();
        AnswerSubmission submission = new AnswerSubmission("user", questionId, "A");
        when(mockService.submitAnswer(submission))
            .thenThrow(new RuntimeException("Unexpected error"));

        Response response = resource.submitAnswer(submission);

        assertEquals(500, response.getStatus());
        assertEquals("Internal server error", response.getEntity());
    }

    @Test
    void shouldReturnIncorrectAnswerResponse() {
        AnswerService mockService = mock(AnswerService.class);
        AnswerResource resource = new AnswerResource(mockService);

        UUID questionId = UUID.randomUUID();
        Answer answer1 = new Answer(UUID.randomUUID(), "Paris", false);
        Answer answer2 = new Answer(UUID.randomUUID(), "London", true);
        Answer answer3 = new Answer(UUID.randomUUID(), "Berlin", false);
        Answer answer4 = new Answer(UUID.randomUUID(), "Madrid", false);
        Question question = new Question(questionId, "Capital?", List.of(answer1, answer2, answer3, answer4));
        TriviaQuestion nextQuestion = new TriviaQuestion(UUID.randomUUID(), "Next?", "A", "B", "C", "D", "Msg");
        AnswerResponse answerResponse = new AnswerResponse(false, "B", question, nextQuestion);

        AnswerSubmission submission = new AnswerSubmission("user", questionId, "A");
        when(mockService.submitAnswer(submission)).thenReturn(Optional.of(answerResponse));

        Response response = resource.submitAnswer(submission);

        assertEquals(200, response.getStatus());
        AnswerResponse result = (AnswerResponse) response.getEntity();
        assertFalse(result.isCorrect());
        assertEquals("B", result.correctAnswer());
    }
}
