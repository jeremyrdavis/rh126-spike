package com.redhat.demos;

import com.redhat.demos.model.TriviaQuestion;
import com.redhat.demos.service.QuestionService;
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
        try {
            Optional<TriviaQuestion> triviaQuestion = questionService.getRandomTriviaQuestion();

            if (triviaQuestion.isEmpty()) {
                return Response.status(404).entity("No questions available").build();
            }

            return Response.ok(triviaQuestion.get()).build();

        } catch (Exception e) {
            return Response.status(500).entity("Internal server error").build();
        }
    }
}
