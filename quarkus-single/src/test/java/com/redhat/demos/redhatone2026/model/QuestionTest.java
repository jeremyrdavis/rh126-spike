package com.redhat.demos.redhatone2026.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    void shouldCreateQuestionWithAllFields() {
        UUID questionId = UUID.randomUUID();
        String questionText = "What is the largest city in Nevada?";
        List<Answer> answers = List.of(
            new Answer(UUID.randomUUID(), "Las Vegas", true),
            new Answer(UUID.randomUUID(), "Reno", false),
            new Answer(UUID.randomUUID(), "Henderson", false)
        );

        Question question = new Question(questionId, questionText, answers);

        assertEquals(questionId, question.id());
        assertEquals(questionText, question.questionText());
        assertEquals(3, question.optionalAnswers().size());
    }

    @Test
    void shouldMakeAnswersListImmutable() {
        UUID questionId = UUID.randomUUID();
        String questionText = "Test question";
        List<Answer> mutableList = new ArrayList<>();
        mutableList.add(new Answer(UUID.randomUUID(), "Answer 1", true));
        mutableList.add(new Answer(UUID.randomUUID(), "Answer 2", false));

        Question question = new Question(questionId, questionText, mutableList);

        // Modifying the original list should not affect the question's answers
        mutableList.add(new Answer(UUID.randomUUID(), "Answer 3", false));

        assertEquals(2, question.optionalAnswers().size());

        // The question's answers list should be immutable
        assertThrows(UnsupportedOperationException.class, () -> {
            question.optionalAnswers().add(new Answer(UUID.randomUUID(), "Answer 4", false));
        });
    }

    @Test
    void shouldHandleNullAnswersList() {
        UUID questionId = UUID.randomUUID();
        String questionText = "Test question";

        Question question = new Question(questionId, questionText, null);

        assertNotNull(question.optionalAnswers());
        assertTrue(question.optionalAnswers().isEmpty());
    }

    @Test
    void shouldSupportRecordEquality() {
        UUID questionId = UUID.randomUUID();
        List<Answer> answers = List.of(
            new Answer(UUID.randomUUID(), "Answer 1", true)
        );

        Question question1 = new Question(questionId, "Test question", answers);
        Question question2 = new Question(questionId, "Test question", answers);

        assertEquals(question1, question2);
        assertEquals(question1.hashCode(), question2.hashCode());
    }
}
