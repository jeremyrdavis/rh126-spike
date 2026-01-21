package com.redhat.demos.redhatone2026.model;

import java.util.UUID;

/**
 * Immutable record representing an answer submission request.
 * Contains the username, question ID, and the user's selected answer.
 */
public record AnswerSubmission(
    String username,
    UUID questionId,
    String selectedAnswer
) {
}
