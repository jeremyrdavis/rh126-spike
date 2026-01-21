package com.redhat.demos.redhatone2026.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AnswerSubmissionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateAnswerSubmissionRecord() {
        UUID questionId = UUID.randomUUID();
        AnswerSubmission submission = new AnswerSubmission(
            "testuser",
            questionId,
            "A"
        );

        assertNotNull(submission);
        assertEquals("testuser", submission.username());
        assertEquals(questionId, submission.questionId());
        assertEquals("A", submission.selectedAnswer());
    }

    @Test
    void shouldDeserializeFromJson() throws JsonProcessingException {
        String questionId = "550e8400-e29b-41d4-a716-446655440000";
        String json = "{\"username\":\"john\",\"questionId\":\"" + questionId + "\",\"selectedAnswer\":\"B\"}";

        AnswerSubmission submission = objectMapper.readValue(json, AnswerSubmission.class);

        assertNotNull(submission);
        assertEquals("john", submission.username());
        assertEquals(UUID.fromString(questionId), submission.questionId());
        assertEquals("B", submission.selectedAnswer());
    }

    @Test
    void shouldHandleRecordEquality() {
        UUID questionId = UUID.randomUUID();
        AnswerSubmission submission1 = new AnswerSubmission("user1", questionId, "C");
        AnswerSubmission submission2 = new AnswerSubmission("user1", questionId, "C");
        AnswerSubmission submission3 = new AnswerSubmission("user2", questionId, "C");

        assertEquals(submission1, submission2);
        assertNotEquals(submission1, submission3);
    }

    @Test
    void shouldSerializeToJson() throws JsonProcessingException {
        UUID questionId = UUID.randomUUID();
        AnswerSubmission submission = new AnswerSubmission("jane", questionId, "D");

        String json = objectMapper.writeValueAsString(submission);

        assertNotNull(json);
        assertTrue(json.contains("\"username\":\"jane\""));
        assertTrue(json.contains("\"questionId\":\"" + questionId + "\""));
        assertTrue(json.contains("\"selectedAnswer\":\"D\""));
    }
}
