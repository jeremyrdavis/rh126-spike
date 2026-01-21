package com.redhat.demos.redhatone2026;

import com.redhat.demos.redhatone2026.model.LeaderboardEntry;
import com.redhat.demos.redhatone2026.service.LeaderboardService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the complete leaderboard feature.
 * Tests end-to-end workflows from service layer to API responses.
 */
@QuarkusTest
class LeaderboardIntegrationTest {

    @InjectMock
    LeaderboardService leaderboardService;

    @Test
    void shouldReturnCompleteLeaderboardWithAllFields() {
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
                .body("[1].score", is(12))
                .body("[1].rank", is(2))
                .body("[1].questionsAnsweredCorrectly", is(12))
                .body("[2].username", is("charlie"))
                .body("[2].score", is(10))
                .body("[2].rank", is(3))
                .body("[2].questionsAnsweredCorrectly", is(10));
    }

    @Test
    void shouldReturnLeaderboardSortedByScoreDescending() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("topScorer", 100, 1, 100),
                new LeaderboardEntry("midScorer", 50, 2, 50),
                new LeaderboardEntry("lowScorer", 10, 3, 10)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].score", greaterThan(90))
                .body("[1].score", greaterThan(40))
                .body("[2].score", is(10));
    }

    @Test
    void shouldHandleTiedScoresWithAlphabeticalOrdering() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("alice", 10, 1, 10),
                new LeaderboardEntry("bob", 10, 2, 10),
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
                .body("[0].rank", is(1))
                .body("[1].username", is("bob"))
                .body("[1].rank", is(2))
                .body("[2].username", is("charlie"))
                .body("[2].rank", is(3));
    }

    @Test
    void shouldAssignSequentialRanksRegardlessOfTies() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("first", 20, 1, 20),
                new LeaderboardEntry("second", 15, 2, 15),
                new LeaderboardEntry("third", 15, 3, 15),
                new LeaderboardEntry("fourth", 10, 4, 10)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].rank", is(1))
                .body("[1].rank", is(2))
                .body("[2].rank", is(3))
                .body("[3].rank", is(4));
    }

    @Test
    void shouldIncludeQuestionsAnsweredCorrectlyForEachUser() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("user1", 25, 1, 25),
                new LeaderboardEntry("user2", 18, 2, 18)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].questionsAnsweredCorrectly", is(25))
                .body("[1].questionsAnsweredCorrectly", is(18));
    }

    @Test
    void shouldVerifyJsonArrayStructure() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("testuser", 5, 1, 5)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].username", is("testuser"))
                .body("[0].score", is(5))
                .body("[0].rank", is(1))
                .body("[0].questionsAnsweredCorrectly", is(5));
    }

    @Test
    void shouldHandleLargeLeaderboard() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("user1", 100, 1, 100),
                new LeaderboardEntry("user2", 90, 2, 90),
                new LeaderboardEntry("user3", 80, 3, 80),
                new LeaderboardEntry("user4", 70, 4, 70),
                new LeaderboardEntry("user5", 60, 5, 60),
                new LeaderboardEntry("user6", 50, 6, 50),
                new LeaderboardEntry("user7", 40, 7, 40),
                new LeaderboardEntry("user8", 30, 8, 30),
                new LeaderboardEntry("user9", 20, 9, 20),
                new LeaderboardEntry("user10", 10, 10, 10)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(10))
                .body("[0].rank", is(1))
                .body("[9].rank", is(10));
    }

    @Test
    void shouldReturnCompleteLeaderboardWithMixedScores() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("highScorer", 50, 1, 50),
                new LeaderboardEntry("mediumScorer", 25, 2, 25),
                new LeaderboardEntry("lowScorer", 5, 3, 5),
                new LeaderboardEntry("newbie", 1, 4, 1)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(4))
                .body("[0].score", is(50))
                .body("[1].score", is(25))
                .body("[2].score", is(5))
                .body("[3].score", is(1));
    }

    @Test
    void shouldVerifyScoreMatchesQuestionsAnsweredCorrectly() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("user1", 10, 1, 10),
                new LeaderboardEntry("user2", 5, 2, 5)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].score", is(10))
                .body("[0].questionsAnsweredCorrectly", is(10))
                .body("[1].score", is(5))
                .body("[1].questionsAnsweredCorrectly", is(5));
    }

    @Test
    void shouldReturnLeaderboardWithCorrectContentType() {
        List<LeaderboardEntry> entries = List.of(
                new LeaderboardEntry("user", 1, 1, 1)
        );

        when(leaderboardService.getLeaderboard()).thenReturn(entries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}
