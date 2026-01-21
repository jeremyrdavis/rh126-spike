package com.redhat.demos.redhatone2026.model;

import java.util.UUID;

/**
 * Immutable record representing a trivia question for API responses.
 * Contains the question ID, question text, four answer options, and an environment message.
 * Does not include answer correctness information to avoid exposing the solution.
 */
public record TriviaQuestion(
    UUID id,
    String questionText,
    String option1,
    String option2,
    String option3,
    String option4,
    String environment
) {
}
