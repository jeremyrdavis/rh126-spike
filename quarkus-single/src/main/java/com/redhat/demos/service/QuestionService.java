package com.redhat.demos.service;

import com.redhat.demos.model.Question;
import com.redhat.demos.repository.QuestionRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for trivia question business logic.
 * Delegates to QuestionRepository for data access.
 */
@ApplicationScoped
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        if (questionRepository == null) {
            throw new IllegalArgumentException("QuestionRepository cannot be null");
        }
        this.questionRepository = questionRepository;
    }

    /**
     * Retrieves all questions.
     *
     * @return List of all questions
     */
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    /**
     * Retrieves a question by its ID.
     *
     * @param id the question ID
     * @return Optional containing the question if found, empty otherwise
     * @throws IllegalArgumentException if id is null
     */
    public Optional<Question> getQuestionById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Question ID cannot be null");
        }
        return questionRepository.findById(id);
    }

    /**
     * Retrieves a random question.
     *
     * @return Optional containing a random question if any exist, empty otherwise
     */
    public Optional<Question> getRandomQuestion() {
        return questionRepository.findRandom();
    }
}
