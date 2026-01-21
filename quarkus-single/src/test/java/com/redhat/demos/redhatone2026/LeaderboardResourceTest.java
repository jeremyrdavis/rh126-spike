package com.redhat.demos.redhatone2026;

import com.redhat.demos.redhatone2026.model.LeaderboardEntry;
import com.redhat.demos.redhatone2026.service.LeaderboardService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

/**
 * Tests for LeaderboardResource REST endpoint.
 * Validates HTTP responses, JSON structure, and error handling.
 */
@QuarkusTest
class LeaderboardResourceTest {

    @InjectMock
    LeaderboardService leaderboardService;

    @Test
    void shouldReturnEmptyLeaderboardWithHttp200() {
        when(leaderboardService.getLeaderboard()).thenReturn(List.of());

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(0));
    }

    @Test
    void shouldReturnLeaderboardWithHttp200() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("alice", 15, 1, 15),
                new LeaderboardEntry("bob", 12, 2, 12),
                new LeaderboardEntry("charlie", 10, 3, 10)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(3))
                .body("[0].username", is("alice"))
                .body("[0].score", is(15))
                .body("[0].rank", is(1))
                .body("[0].questionsAnsweredCorrectly", is(15))
                .body("[1].username", is("bob"))
                .body("[1].rank", is(2))
                .body("[2].username", is("charlie"))
                .body("[2].rank", is(3));
    }

    @Test
    void shouldReturnHttp500OnUnexpectedException() {
        when(leaderboardService.getLeaderboard()).thenThrow(new RuntimeException("Database error"));

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(500)
                .body(is("Internal server error"));
    }

    @Test
    void shouldValidateJsonStructure() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("alice", 15, 1, 15)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].username", is("alice"))
                .body("[0].score", is(15))
                .body("[0].rank", is(1))
                .body("[0].questionsAnsweredCorrectly", is(15));
    }

    @Test
    void shouldReturnLeaderboardWithTiedScores() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("alice", 10, 1, 10),
                new LeaderboardEntry("bob", 10, 2, 10)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(2))
                .body("[0].rank", is(1))
                .body("[1].rank", is(2));
    }
}
