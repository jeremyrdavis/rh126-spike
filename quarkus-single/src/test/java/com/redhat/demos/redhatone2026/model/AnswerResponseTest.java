package com.redhat.demos.redhatone2026.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AnswerResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateAnswerResponseRecord() {
        UUID questionId = UUID.randomUUID();
        Answer answer1 = new Answer(UUID.randomUUID(), "Paris", true);
        Answer answer2 = new Answer(UUID.randomUUID(), "London", false);
        Answer answer3 = new Answer(UUID.randomUUID(), "Berlin", false);
        Answer answer4 = new Answer(UUID.randomUUID(), "Madrid", false);
        Question question = new Question(questionId, "What is the capital of France?",
            List.of(answer1, answer2, answer3, answer4));
        TriviaQuestion nextQuestion = new TriviaQuestion(UUID.randomUUID(), 
            "Next question?", "A", "B", "C", "D", "Message"
        );

        AnswerResponse response = new AnswerResponse(
            true,
            "A",
            question,
            nextQuestion
        );

        assertNotNull(response);
        assertTrue(response.isCorrect());
        assertEquals("A", response.correctAnswer());
        assertEquals(question, response.originalQuestion());
        assertEquals(nextQuestion, response.nextQuestion());
    }

    @Test
    void shouldSerializeToJson() throws JsonProcessingException {
        UUID questionId = UUID.randomUUID();
        Answer answer1 = new Answer(UUID.randomUUID(), "True", true);
        Answer answer2 = new Answer(UUID.randomUUID(), "False", false);
        Answer answer3 = new Answer(UUID.randomUUID(), "Maybe", false);
        Answer answer4 = new Answer(UUID.randomUUID(), "Unknown", false);
        Question question = new Question(questionId, "Is this a test?",
            List.of(answer1, answer2, answer3, answer4));
        TriviaQuestion nextQuestion = new TriviaQuestion(UUID.randomUUID(), 
            "Next?", "1", "2", "3", "4", "Env"
        );

        AnswerResponse response = new AnswerResponse(false, "B", question, nextQuestion);
        String json = objectMapper.writeValueAsString(response);

        assertNotNull(json);
        assertTrue(json.contains("\"isCorrect\":false"));
        assertTrue(json.contains("\"correctAnswer\":\"B\""));
        assertTrue(json.contains("\"originalQuestion\""));
        assertTrue(json.contains("\"nextQuestion\""));
    }

    @Test
    void shouldHandleNullNextQuestion() {
        UUID questionId = UUID.randomUUID();
        Answer answer = new Answer(UUID.randomUUID(), "Test", true);
        Question question = new Question(questionId, "Test?", List.of(answer));

        AnswerResponse response = new AnswerResponse(true, "A", question, null);

        assertNotNull(response);
        assertTrue(response.isCorrect());
        assertNull(response.nextQuestion());
    }

    @Test
    void shouldHandleRecordEquality() {
        UUID questionId = UUID.randomUUID();
        Answer answer = new Answer(UUID.randomUUID(), "Answer", true);
        Question question = new Question(questionId, "Question?", List.of(answer));
        TriviaQuestion nextQuestion = new TriviaQuestion(UUID.randomUUID(), "Next?", "A", "B", "C", "D", "Msg");

        AnswerResponse response1 = new AnswerResponse(true, "A", question, nextQuestion);
        AnswerResponse response2 = new AnswerResponse(true, "A", question, nextQuestion);
        AnswerResponse response3 = new AnswerResponse(false, "B", question, nextQuestion);

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
    }
}
