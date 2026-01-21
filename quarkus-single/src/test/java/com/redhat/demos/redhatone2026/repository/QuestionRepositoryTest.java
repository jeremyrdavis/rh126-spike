package com.redhat.demos.redhatone2026.repository;

import com.redhat.demos.redhatone2026.model.Answer;
import com.redhat.demos.redhatone2026.model.Question;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class QuestionRepositoryTest {

    @Inject
    QuestionRepository questionRepository;

    @Test
    void shouldLoadQuestionsOnStartup() {
        List<Question> questions = questionRepository.findAll();

        assertNotNull(questions);
        assertFalse(questions.isEmpty());
        assertTrue(questions.size() >= 10, "Should have at least 10 questions");
    }

    @Test
    void shouldFindAllQuestions() {
        List<Question> questions = questionRepository.findAll();

        assertNotNull(questions);
        for (Question question : questions) {
            assertNotNull(question.id());
            assertNotNull(question.questionText());
            assertFalse(question.optionalAnswers().isEmpty());
        }
    }

    @Test
    void shouldFindQuestionById() {
        List<Question> allQuestions = questionRepository.findAll();
        assertFalse(allQuestions.isEmpty());

        Question firstQuestion = allQuestions.get(0);
        Optional<Question> found = questionRepository.findById(firstQuestion.id());

        assertTrue(found.isPresent());
        assertEquals(firstQuestion.id(), found.get().id());
        assertEquals(firstQuestion.questionText(), found.get().questionText());
    }

    @Test
    void shouldReturnEmptyForNonExistentId() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Question> found = questionRepository.findById(nonExistentId);

        assertFalse(found.isPresent());
    }

    @Test
    void shouldFindRandomQuestion() {
        Optional<Question> randomQuestion = questionRepository.findRandom();

        assertTrue(randomQuestion.isPresent());
        assertNotNull(randomQuestion.get().id());
        assertNotNull(randomQuestion.get().questionText());
        assertFalse(randomQuestion.get().optionalAnswers().isEmpty());
    }

    @Test
    void shouldValidateQuestionStructure() {
        List<Question> questions = questionRepository.findAll();

        for (Question question : questions) {
            // Each question should have answers
            assertFalse(question.optionalAnswers().isEmpty());

            // Each question should have at least one correct answer
            long correctAnswerCount = question.optionalAnswers().stream()
                .filter(Answer::isCorrect)
                .count();
            assertTrue(correctAnswerCount >= 1, "Question should have at least one correct answer");
        }
    }
}
