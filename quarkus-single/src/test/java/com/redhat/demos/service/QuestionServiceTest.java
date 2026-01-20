package com.redhat.demos.service;

import com.redhat.demos.model.Answer;
import com.redhat.demos.model.Question;
import com.redhat.demos.repository.QuestionRepository;
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
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        questionRepository = Mockito.mock(QuestionRepository.class);
        questionService = new QuestionService(questionRepository);
    }

    @Test
    void shouldThrowExceptionWhenRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new QuestionService(null);
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

    private Question createMockQuestion(String questionText) {
        List<Answer> answers = List.of(
            new Answer(UUID.randomUUID(), "Answer 1", true),
            new Answer(UUID.randomUUID(), "Answer 2", false)
        );
        return new Question(UUID.randomUUID(), questionText, answers);
    }
}
