package com.redhat.demos.redhatone2026.integration;

import com.redhat.demos.redhatone2026.model.Answer;
import com.redhat.demos.redhatone2026.model.Question;
import com.redhat.demos.redhatone2026.service.QuestionService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests verifying the complete data flow from JSON file
 * through repository to service layer.
 */
@QuarkusTest
class QuestionDataFlowTest {

    @Inject
    QuestionService questionService;

    @Test
    void shouldLoadQuestionsFromJsonOnStartup() {
        List<Question> questions = questionService.getAllQuestions();

        assertNotNull(questions, "Questions should not be null");
        assertEquals(15, questions.size(), "Should load exactly 15 questions from JSON");
    }

    @Test
    void shouldRetrieveQuestionWithCompleteStructure() {
        List<Question> questions = questionService.getAllQuestions();
        assertFalse(questions.isEmpty());

        Question question = questions.get(0);

        assertNotNull(question.id(), "Question ID should not be null");
        assertNotNull(question.questionText(), "Question text should not be null");
        assertNotNull(question.optionalAnswers(), "Answers should not be null");
        assertFalse(question.optionalAnswers().isEmpty(), "Question should have answers");
        assertTrue(question.optionalAnswers().size() >= 3, "Question should have at least 3 answers");
    }

    @Test
    void shouldEnsureEachQuestionHasExactlyOneCorrectAnswer() {
        List<Question> questions = questionService.getAllQuestions();

        for (Question question : questions) {
            long correctAnswerCount = question.optionalAnswers().stream()
                .filter(Answer::isCorrect)
                .count();

            assertEquals(1, correctAnswerCount,
                "Question '" + question.questionText() + "' should have exactly one correct answer");
        }
    }

    @Test
    void shouldRetrieveQuestionByIdFromLoadedData() {
        List<Question> allQuestions = questionService.getAllQuestions();
        assertFalse(allQuestions.isEmpty());

        Question expectedQuestion = allQuestions.get(0);
        Optional<Question> foundQuestion = questionService.getQuestionById(expectedQuestion.id());

        assertTrue(foundQuestion.isPresent(), "Question should be found by ID");
        assertEquals(expectedQuestion.id(), foundQuestion.get().id());
        assertEquals(expectedQuestion.questionText(), foundQuestion.get().questionText());
        assertEquals(expectedQuestion.optionalAnswers().size(), foundQuestion.get().optionalAnswers().size());
    }

    @Test
    void shouldReturnEmptyForNonExistentQuestionId() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Question> result = questionService.getQuestionById(nonExistentId);

        assertFalse(result.isPresent(), "Should return empty for non-existent ID");
    }

    @Test
    void shouldRetrieveRandomQuestionFromLoadedData() {
        Optional<Question> randomQuestion = questionService.getRandomQuestion();

        assertTrue(randomQuestion.isPresent(), "Random question should be present");
        assertNotNull(randomQuestion.get().id());
        assertNotNull(randomQuestion.get().questionText());
        assertFalse(randomQuestion.get().optionalAnswers().isEmpty());
    }

    @Test
    void shouldVerifyQuestionsAreAboutLasVegas() {
        List<Question> questions = questionService.getAllQuestions();

        // Verify at least some questions contain Las Vegas related keywords
        long vegasRelatedCount = questions.stream()
            .filter(q -> {
                String text = q.questionText().toLowerCase();
                return text.contains("las vegas") ||
                       text.contains("vegas") ||
                       text.contains("nevada") ||
                       text.contains("strip") ||
                       text.contains("casino") ||
                       text.contains("hotel");
            })
            .count();

        assertTrue(vegasRelatedCount >= 10,
            "At least 10 questions should be Las Vegas related");
    }

    @Test
    void shouldVerifyAnswersHaveUniqueIds() {
        List<Question> questions = questionService.getAllQuestions();

        for (Question question : questions) {
            List<UUID> answerIds = question.optionalAnswers().stream()
                .map(Answer::id)
                .toList();

            long uniqueIdCount = answerIds.stream().distinct().count();
            assertEquals(answerIds.size(), uniqueIdCount,
                "All answers in a question should have unique IDs");
        }
    }

    @Test
    void shouldVerifyQuestionsHaveUniqueIds() {
        List<Question> questions = questionService.getAllQuestions();

        List<UUID> questionIds = questions.stream()
            .map(Question::id)
            .toList();

        long uniqueIdCount = questionIds.stream().distinct().count();
        assertEquals(questions.size(), uniqueIdCount,
            "All questions should have unique IDs");
    }

    @Test
    void shouldVerifyDataImmutability() {
        List<Question> questions = questionService.getAllQuestions();
        assertFalse(questions.isEmpty());

        Question question = questions.get(0);
        List<Answer> answers = question.optionalAnswers();

        // Attempting to modify the answers list should throw an exception
        assertThrows(UnsupportedOperationException.class, () -> {
            answers.add(new Answer(UUID.randomUUID(), "New answer", false));
        }, "Answers list should be immutable");
    }
}
