package com.redhat.demos.redhatone2026.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.demos.redhatone2026.model.Question;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Repository for managing trivia questions using in-memory HashMap storage.
 * Questions are loaded from a JSON resource file at application startup.
 */
@ApplicationScoped
public class QuestionRepository {

    private static final Logger LOG = Logger.getLogger(QuestionRepository.class);
    private static final String QUESTIONS_FILE = "/questions.json";

    private final HashMap<UUID, Question> questionMap;
    private final ObjectMapper objectMapper;

    public QuestionRepository() {
        this.questionMap = new HashMap<>();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Initializes the repository by loading questions from the JSON resource file.
     * Throws a runtime exception if the file is missing or malformed.
     */
    @PostConstruct
    void init() {
        try (InputStream inputStream = getClass().getResourceAsStream(QUESTIONS_FILE)) {
            if (inputStream == null) {
                String errorMsg = "Questions file not found: " + QUESTIONS_FILE;
                LOG.error(errorMsg);
                throw new IllegalStateException(errorMsg);
            }

            List<Question> questions = objectMapper.readValue(
                inputStream,
                new TypeReference<List<Question>>() {}
            );

            for (Question question : questions) {
                questionMap.put(question.id(), question);
            }

            LOG.infof("Successfully loaded %d questions from %s", questionMap.size(), QUESTIONS_FILE);

        } catch (IOException e) {
            String errorMsg = "Failed to load questions from " + QUESTIONS_FILE + ": " + e.getMessage();
            LOG.error(errorMsg, e);
            throw new IllegalStateException(errorMsg, e);
        }
    }

    /**
     * Retrieves all questions.
     *
     * @return List of all questions
     */
    public List<Question> findAll() {
        return new ArrayList<>(questionMap.values());
    }

    /**
     * Finds a question by its ID.
     *
     * @param id the question ID
     * @return Optional containing the question if found, empty otherwise
     */
    public Optional<Question> findById(UUID id) {
        return Optional.ofNullable(questionMap.get(id));
    }

    /**
     * Retrieves a random question from the repository.
     *
     * @return Optional containing a random question if any exist, empty otherwise
     */
    public Optional<Question> findRandom() {
        if (questionMap.isEmpty()) {
            return Optional.empty();
        }

        List<Question> questions = new ArrayList<>(questionMap.values());
        int randomIndex = ThreadLocalRandom.current().nextInt(questions.size());
        return Optional.of(questions.get(randomIndex));
    }
}
