package com.redhat.demos.model;

/**
 * Immutable record representing a trivia question for API responses.
 * Contains the question text, four answer options, and an environment message.
 * Does not include answer correctness information to avoid exposing the solution.
 */
public record TriviaQuestion(
    String questionText,
    String option1,
    String option2,
    String option3,
    String option4,
    String environment
) {
}
