package com.redhat.demos.redhatone2026.model;

import java.util.UUID;

/**
 * Immutable record representing a trivia question answer option.
 * Each question has multiple answers, with exactly one marked as correct.
 */
public record Answer(
    UUID id,
    String text,
    boolean isCorrect
) {
}
