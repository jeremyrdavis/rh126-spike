package com.redhat.demos.redhatone2026;

import com.redhat.demos.redhatone2026.model.TriviaQuestion;
import com.redhat.demos.redhatone2026.service.QuestionService;
import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

/**
 * REST resource for trivia question API endpoints.
 * Provides access to trivia questions with integrated environment messages.
 */
@Path("/api/questions")
public class QuestionResource {

    private final QuestionService questionService;

    public QuestionResource(QuestionService questionService) {
        if (questionService == null) {
            throw new IllegalArgumentException("QuestionService cannot be null");
        }
        this.questionService = questionService;
    }

    /**
     * Retrieves a random trivia question with environment message.
     *
     * @return TriviaQuestion DTO with HTTP 200 if available,
     *         HTTP 404 if no questions exist,
     *         HTTP 500 for unexpected errors
     */
    @GET
    @Path("/random")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRandom() {
        Log.debug("Received request for random trivia question");
        try {
            Optional<TriviaQuestion> triviaQuestion = questionService.getRandomTriviaQuestion();

            if (triviaQuestion.isEmpty()) {
                Log.debug("No questions available, returning 404");
                return Response.status(404).entity("No questions available").build();
            }

            Log.debugf("Returning random trivia question: %s", triviaQuestion.get());
            return Response.ok(triviaQuestion.get()).build();

        } catch (Exception e) {
            Log.errorf(e, "Error retrieving random question");
            return Response.status(500).entity("Internal server error").build();
        }
    }
}
