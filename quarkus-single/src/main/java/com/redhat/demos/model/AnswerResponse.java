package com.redhat.demos.model;

/**
 * Immutable record representing the response to an answer submission.
 * Contains validation result, correct answer, original question, and next question for game flow.
 */
public record AnswerResponse(
    boolean isCorrect,
    String correctAnswer,
    Question originalQuestion,
    TriviaQuestion nextQuestion
) {
}
