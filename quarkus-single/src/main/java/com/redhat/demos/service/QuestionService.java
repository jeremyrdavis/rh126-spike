package com.redhat.demos.service;

import com.redhat.demos.model.Answer;
import com.redhat.demos.model.Question;
import com.redhat.demos.model.TriviaQuestion;
import com.redhat.demos.repository.EnvironmentRepository;
import com.redhat.demos.repository.QuestionRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service layer for trivia question business logic.
 * Delegates to QuestionRepository for data access and EnvironmentRepository for messaging.
 */
@ApplicationScoped
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final EnvironmentRepository environmentRepository;

    public QuestionService(QuestionRepository questionRepository, EnvironmentRepository environmentRepository) {
        if (questionRepository == null) {
            throw new IllegalArgumentException("QuestionRepository cannot be null");
        }
        if (environmentRepository == null) {
            throw new IllegalArgumentException("EnvironmentRepository cannot be null");
        }
        this.questionRepository = questionRepository;
        this.environmentRepository = environmentRepository;
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

    /**
     * Retrieves a random trivia question mapped to a DTO with environment message.
     * Maps the Question domain model to TriviaQuestion DTO, extracting answer text
     * without exposing correctness information, and includes a random environment message.
     *
     * @return Optional containing a TriviaQuestion DTO if a question exists, empty otherwise
     */
    public Optional<TriviaQuestion> getRandomTriviaQuestion() {
        Optional<Question> questionOptional = questionRepository.findRandom();

        if (questionOptional.isEmpty()) {
            return Optional.empty();
        }

        Question question = questionOptional.get();
        List<Answer> answers = question.optionalAnswers();
        String environmentMessage = environmentRepository.getRandomMessage();

        TriviaQuestion triviaQuestion = new TriviaQuestion(
            question.id(),
            question.questionText(),
            answers.get(0).text(),
            answers.get(1).text(),
            answers.get(2).text(),
            answers.get(3).text(),
            environmentMessage
        );

        return Optional.of(triviaQuestion);
    }
}
