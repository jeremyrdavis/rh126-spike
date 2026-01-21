package com.redhat.demos.redhatone2026.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TriviaQuestionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateTriviaQuestionRecord() {
        TriviaQuestion question = new TriviaQuestion(UUID.randomUUID(), 
            "What is the capital of France?",
            "Paris",
            "London",
            "Berlin",
            "Madrid",
            "Quarkus is awesome"
        );

        assertNotNull(question);
        assertEquals("What is the capital of France?", question.questionText());
        assertEquals("Paris", question.option1());
        assertEquals("London", question.option2());
        assertEquals("Berlin", question.option3());
        assertEquals("Madrid", question.option4());
        assertEquals("Quarkus is awesome", question.environment());
    }

    @Test
    void shouldSerializeToJson() throws JsonProcessingException {
        TriviaQuestion question = new TriviaQuestion(UUID.randomUUID(), 
            "Test Question",
            "Option A",
            "Option B",
            "Option C",
            "Option D",
            "Test Environment Message"
        );

        String json = objectMapper.writeValueAsString(question);

        assertNotNull(json);
        assertTrue(json.contains("\"questionText\":\"Test Question\""));
        assertTrue(json.contains("\"option1\":\"Option A\""));
        assertTrue(json.contains("\"option2\":\"Option B\""));
        assertTrue(json.contains("\"option3\":\"Option C\""));
        assertTrue(json.contains("\"option4\":\"Option D\""));
        assertTrue(json.contains("\"environment\":\"Test Environment Message\""));
    }

    @Test
    void shouldDeserializeFromJson() throws JsonProcessingException {
        String json = "{\"questionText\":\"Sample Question\",\"option1\":\"A\",\"option2\":\"B\"," +
                      "\"option3\":\"C\",\"option4\":\"D\",\"environment\":\"Sample Message\"}";

        TriviaQuestion question = objectMapper.readValue(json, TriviaQuestion.class);

        assertNotNull(question);
        assertEquals("Sample Question", question.questionText());
        assertEquals("A", question.option1());
        assertEquals("B", question.option2());
        assertEquals("C", question.option3());
        assertEquals("D", question.option4());
        assertEquals("Sample Message", question.environment());
    }

    @Test
    void shouldHandleRecordEquality() {
        UUID sameId = UUID.randomUUID();
        TriviaQuestion question1 = new TriviaQuestion(sameId,
            "Question", "A", "B", "C", "D", "Env"
        );
        TriviaQuestion question2 = new TriviaQuestion(sameId,
            "Question", "A", "B", "C", "D", "Env"
        );
        TriviaQuestion question3 = new TriviaQuestion(UUID.randomUUID(),
            "Different", "A", "B", "C", "D", "Env"
        );

        assertEquals(question1, question2);
        assertNotEquals(question1, question3);
    }
}
