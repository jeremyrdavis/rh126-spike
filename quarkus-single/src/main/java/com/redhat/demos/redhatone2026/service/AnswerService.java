package com.redhat.demos.redhatone2026.service;

import com.redhat.demos.redhatone2026.model.Answer;
import com.redhat.demos.redhatone2026.model.AnswerResponse;
import com.redhat.demos.redhatone2026.model.AnswerSubmission;
import com.redhat.demos.redhatone2026.model.Question;
import com.redhat.demos.redhatone2026.model.TriviaQuestion;
import com.redhat.demos.redhatone2026.repository.EnvironmentRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service layer for answer validation business logic.
 * Validates user answers, tracks scores, and constructs responses.
 */
@ApplicationScoped
public class AnswerService {

    private final QuestionService questionService;
    private final LeaderboardService leaderboardService;
    private final EnvironmentRepository environmentRepository;

    public AnswerService(QuestionService questionService,
                        LeaderboardService leaderboardService,
                        EnvironmentRepository environmentRepository) {
        if (questionService == null) {
            throw new IllegalArgumentException("QuestionService cannot be null");
        }
        if (leaderboardService == null) {
            throw new IllegalArgumentException("LeaderboardService cannot be null");
        }
        if (environmentRepository == null) {
            throw new IllegalArgumentException("EnvironmentRepository cannot be null");
        }
        this.questionService = questionService;
        this.leaderboardService = leaderboardService;
        this.environmentRepository = environmentRepository;
    }

    /**
     * Submits an answer and returns validation result with next question.
     *
     * @param submission the answer submission
     * @return Optional containing AnswerResponse if question found, empty otherwise
     * @throws IllegalArgumentException if validation fails
     */
    public Optional<AnswerResponse> submitAnswer(AnswerSubmission submission) {
        Log.debugf("Processing answer submission: username=%s, questionId=%s, selectedAnswer=%s",
            submission.username(), submission.questionId(), submission.selectedAnswer());

        // Validate input
        if (submission.username() == null || submission.username().isEmpty()) {
            Log.debugf("Invalid username: %s", submission.username());
            throw new IllegalArgumentException("Invalid username");
        }
        if (submission.questionId() == null) {
            Log.debug("Invalid questionId: null");
            throw new IllegalArgumentException("Invalid questionId");
        }
        if (submission.selectedAnswer() == null ||
            !Set.of("A", "B", "C", "D").contains(submission.selectedAnswer())) {
            Log.debugf("Invalid answer selection: %s", submission.selectedAnswer());
            throw new IllegalArgumentException("Invalid answer selection");
        }

        // Retrieve question
        Optional<Question> questionOptional = questionService.getQuestionById(submission.questionId());
        if (questionOptional.isEmpty()) {
            Log.debugf("Question not found: %s", submission.questionId());
            return Optional.empty();
        }

        Question question = questionOptional.get();
        Log.debugf("Found question: %s", question.questionText());

        // Find correct answer and determine its letter
        String correctAnswer = determineCorrectAnswer(question);
        Log.debugf("Correct answer is: %s", correctAnswer);

        // Validate user's answer
        boolean isCorrect = submission.selectedAnswer().equals(correctAnswer);
        Log.debugf("User answer %s is %s", submission.selectedAnswer(), isCorrect ? "CORRECT" : "INCORRECT");

        // Record answer in leaderboard
        leaderboardService.recordAnswer(submission.username(), submission.questionId(), isCorrect);

        // Get next question
        Optional<TriviaQuestion> nextQuestionOptional = questionService.getRandomTriviaQuestion();
        TriviaQuestion nextQuestion = nextQuestionOptional.orElse(null);
        Log.debugf("Next question: %s", nextQuestion != null ? nextQuestion.questionText() : "none");

        // Construct response
        AnswerResponse response = new AnswerResponse(
            isCorrect,
            correctAnswer,
            question,
            nextQuestion
        );

        Log.debugf("Returning answer response: isCorrect=%s, correctAnswer=%s", isCorrect, correctAnswer);
        return Optional.of(response);
    }

    /**
     * Determines the correct answer letter (A, B, C, or D) for a question.
     *
     * @param question the question
     * @return the correct answer letter
     */
    private String determineCorrectAnswer(Question question) {
        List<Answer> answers = question.optionalAnswers();
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).isCorrect()) {
                return switch (i) {
                    case 0 -> "A";
                    case 1 -> "B";
                    case 2 -> "C";
                    case 3 -> "D";
                    default -> throw new IllegalStateException("Invalid answer index: " + i);
                };
            }
        }
        throw new IllegalStateException("No correct answer found for question: " + question.id());
    }
}
