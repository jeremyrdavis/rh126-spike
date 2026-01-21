package com.redhat.demos.redhatone2026.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {

    @Test
    void shouldCreateAnswerWithAllFields() {
        UUID id = UUID.randomUUID();
        String text = "Las Vegas";
        boolean isCorrect = true;

        Answer answer = new Answer(id, text, isCorrect);

        assertEquals(id, answer.id());
        assertEquals(text, answer.text());
        assertTrue(answer.isCorrect());
    }

    @Test
    void shouldCreateIncorrectAnswer() {
        UUID id = UUID.randomUUID();
        String text = "Phoenix";
        boolean isCorrect = false;

        Answer answer = new Answer(id, text, isCorrect);

        assertEquals(id, answer.id());
        assertEquals(text, answer.text());
        assertFalse(answer.isCorrect());
    }

    @Test
    void shouldSupportRecordEquality() {
        UUID id = UUID.randomUUID();
        Answer answer1 = new Answer(id, "Las Vegas", true);
        Answer answer2 = new Answer(id, "Las Vegas", true);

        assertEquals(answer1, answer2);
        assertEquals(answer1.hashCode(), answer2.hashCode());
    }

    @Test
    void shouldProvideToStringRepresentation() {
        UUID id = UUID.randomUUID();
        Answer answer = new Answer(id, "Las Vegas", true);

        String toString = answer.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Las Vegas"));
    }
}
