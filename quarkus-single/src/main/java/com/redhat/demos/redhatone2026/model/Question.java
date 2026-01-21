package com.redhat.demos.redhatone2026.model;

import java.util.List;
import java.util.UUID;

/**
 * Immutable record representing a trivia question with multiple-choice answers.
 * The optionalAnswers list is made immutable in the compact constructor.
 */
public record Question(
    UUID id,
    String questionText,
    List<Answer> optionalAnswers
) {
    /**
     * Compact constructor ensures the answers list is immutable.
     */
    public Question {
        optionalAnswers = optionalAnswers == null ? List.of() : List.copyOf(optionalAnswers);
    }
}
