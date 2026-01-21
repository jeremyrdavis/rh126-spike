package com.redhat.demos.redhatone2026;

import com.redhat.demos.redhatone2026.model.AnswerResponse;
import com.redhat.demos.redhatone2026.model.AnswerSubmission;
import com.redhat.demos.redhatone2026.service.AnswerService;
import io.quarkus.logging.Log;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

/**
 * REST resource for answer submission API endpoints.
 * Provides access to answer validation and score tracking.
 */
@Path("/api/answers")
public class AnswerResource {

    private final AnswerService answerService;

    public AnswerResource(AnswerService answerService) {
        if (answerService == null) {
            throw new IllegalArgumentException("AnswerService cannot be null");
        }
        this.answerService = answerService;
    }

    /**
     * Submits an answer for validation.
     *
     * @param submission the answer submission
     * @return AnswerResponse with HTTP 200 if successful,
     *         HTTP 400 for invalid input,
     *         HTTP 404 if question not found,
     *         HTTP 500 for unexpected errors
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response submitAnswer(AnswerSubmission submission) {
        Log.debugf("Received answer submission: %s", submission);
        try {
            Optional<AnswerResponse> answerResponse = answerService.submitAnswer(submission);

            if (answerResponse.isEmpty()) {
                return Response.status(404).entity("Question not found").build();
            }
            Log.debugf("Returning answer response: %s", answerResponse.get());
            return Response.ok(answerResponse.get()).build();

        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(500).entity("Internal server error").build();
        }
    }
}
