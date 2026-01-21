package com.redhat.demos.redhatone2026.service;

import com.redhat.demos.redhatone2026.model.Answer;
import com.redhat.demos.redhatone2026.model.Question;
import com.redhat.demos.redhatone2026.model.TriviaQuestion;
import com.redhat.demos.redhatone2026.repository.EnvironmentRepository;
import com.redhat.demos.redhatone2026.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionServiceTest {

    private QuestionRepository questionRepository;
    private EnvironmentRepository environmentRepository;
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        questionRepository = Mockito.mock(QuestionRepository.class);
        environmentRepository = Mockito.mock(EnvironmentRepository.class);
        questionService = new QuestionService(questionRepository, environmentRepository);
    }

    @Test
    void shouldThrowExceptionWhenQuestionRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new QuestionService(null, environmentRepository);
        });
    }

    @Test
    void shouldThrowExceptionWhenEnvironmentRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new QuestionService(questionRepository, null);
        });
    }

    @Test
    void shouldGetAllQuestions() {
        List<Question> mockQuestions = List.of(
            createMockQuestion("Question 1"),
            createMockQuestion("Question 2")
        );
        when(questionRepository.findAll()).thenReturn(mockQuestions);

        List<Question> result = questionService.getAllQuestions();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    void shouldGetQuestionById() {
        UUID questionId = UUID.randomUUID();
        Question mockQuestion = createMockQuestion("Test question");
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(mockQuestion));

        Optional<Question> result = questionService.getQuestionById(questionId);

        assertTrue(result.isPresent());
        assertEquals("Test question", result.get().questionText());
        verify(questionRepository, times(1)).findById(questionId);
    }

    @Test
    void shouldReturnEmptyWhenQuestionNotFound() {
        UUID questionId = UUID.randomUUID();
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        Optional<Question> result = questionService.getQuestionById(questionId);

        assertFalse(result.isPresent());
        verify(questionRepository, times(1)).findById(questionId);
    }

    @Test
    void shouldThrowExceptionWhenQuestionIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            questionService.getQuestionById(null);
        });
    }

    @Test
    void shouldGetRandomQuestion() {
        Question mockQuestion = createMockQuestion("Random question");
        when(questionRepository.findRandom()).thenReturn(Optional.of(mockQuestion));

        Optional<Question> result = questionService.getRandomQuestion();

        assertTrue(result.isPresent());
        assertEquals("Random question", result.get().questionText());
        verify(questionRepository, times(1)).findRandom();
    }

    @Test
    void shouldReturnEmptyWhenNoQuestionsExist() {
        when(questionRepository.findRandom()).thenReturn(Optional.empty());

        Optional<Question> result = questionService.getRandomQuestion();

        assertFalse(result.isPresent());
        verify(questionRepository, times(1)).findRandom();
    }

    @Test
    void shouldGetRandomTriviaQuestionWithEnvironmentMessage() {
        Question mockQuestion = createMockQuestionWithFourAnswers(
            "What is Quarkus?",
            "A framework", "A database", "An IDE", "A server"
        );
        when(questionRepository.findRandom()).thenReturn(Optional.of(mockQuestion));
        when(environmentRepository.getRandomMessage()).thenReturn("Quarkus is awesome");

        Optional<TriviaQuestion> result = questionService.getRandomTriviaQuestion();

        assertTrue(result.isPresent());
        TriviaQuestion triviaQuestion = result.get();
        assertEquals("What is Quarkus?", triviaQuestion.questionText());
        assertEquals("A framework", triviaQuestion.option1());
        assertEquals("A database", triviaQuestion.option2());
        assertEquals("An IDE", triviaQuestion.option3());
        assertEquals("A server", triviaQuestion.option4());
        assertEquals("Quarkus is awesome", triviaQuestion.environment());
        verify(questionRepository, times(1)).findRandom();
        verify(environmentRepository, times(1)).getRandomMessage();
    }

    @Test
    void shouldReturnEmptyTriviaQuestionWhenNoQuestionsAvailable() {
        when(questionRepository.findRandom()).thenReturn(Optional.empty());

        Optional<TriviaQuestion> result = questionService.getRandomTriviaQuestion();

        assertFalse(result.isPresent());
        verify(questionRepository, times(1)).findRandom();
        verify(environmentRepository, never()).getRandomMessage();
    }

    @Test
    void shouldMapAnswersCorrectlyWithoutExposingIsCorrect() {
        Question mockQuestion = createMockQuestionWithFourAnswers(
            "Test question", "Option A", "Option B", "Option C", "Option D"
        );
        when(questionRepository.findRandom()).thenReturn(Optional.of(mockQuestion));
        when(environmentRepository.getRandomMessage()).thenReturn("Test message");

        Optional<TriviaQuestion> result = questionService.getRandomTriviaQuestion();

        assertTrue(result.isPresent());
        TriviaQuestion triviaQuestion = result.get();
        assertEquals("Option A", triviaQuestion.option1());
        assertEquals("Option B", triviaQuestion.option2());
        assertEquals("Option C", triviaQuestion.option3());
        assertEquals("Option D", triviaQuestion.option4());
    }

    private Question createMockQuestion(String questionText) {
        List<Answer> answers = List.of(
            new Answer(UUID.randomUUID(), "Answer 1", true),
            new Answer(UUID.randomUUID(), "Answer 2", false)
        );
        return new Question(UUID.randomUUID(), questionText, answers);
    }

    private Question createMockQuestionWithFourAnswers(
        String questionText, String ans1, String ans2, String ans3, String ans4
    ) {
        List<Answer> answers = List.of(
            new Answer(UUID.randomUUID(), ans1, true),
            new Answer(UUID.randomUUID(), ans2, false),
            new Answer(UUID.randomUUID(), ans3, false),
            new Answer(UUID.randomUUID(), ans4, false)
        );
        return new Question(UUID.randomUUID(), questionText, answers);
    }
}
