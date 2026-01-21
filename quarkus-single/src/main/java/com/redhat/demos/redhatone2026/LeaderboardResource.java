package com.redhat.demos.redhatone2026;

import com.redhat.demos.redhatone2026.model.LeaderboardEntry;
import com.redhat.demos.redhatone2026.service.LeaderboardService;
import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * REST resource for leaderboard API endpoints.
 * Provides access to current leaderboard rankings with scores and calculated ranks.
 */
@Path("/api/leaderboard")
public class LeaderboardResource {

    private final LeaderboardService leaderboardService;

    public LeaderboardResource(LeaderboardService leaderboardService) {
        if (leaderboardService == null) {
            throw new IllegalArgumentException("LeaderboardService cannot be null");
        }
        this.leaderboardService = leaderboardService;
    }

    /**
     * Retrieves the current leaderboard with rankings.
     *
     * @return List of LeaderboardEntry with HTTP 200 for success,
     *         HTTP 500 for unexpected errors
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLeaderboard() {
        Log.debug("Received request for leaderboard");
        try {
            List<LeaderboardEntry> leaderboard = leaderboardService.getLeaderboard();
            Log.debugf("Returning leaderboard with %d entries", leaderboard.size());
            return Response.ok(leaderboard).build();

        } catch (Exception e) {
            Log.errorf(e, "Error retrieving leaderboard");
            return Response.status(500).entity("Internal server error").build();
        }
    }
}
