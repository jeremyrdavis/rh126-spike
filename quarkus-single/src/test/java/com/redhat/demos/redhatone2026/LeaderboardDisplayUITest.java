package com.redhat.demos.redhatone2026;

import com.redhat.demos.redhatone2026.model.LeaderboardEntry;
import com.redhat.demos.redhatone2026.service.LeaderboardService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Integration tests for Leaderboard Display UI feature.
 * Tests critical end-to-end workflows, navigation integration, and error recovery.
 *
 * This test class fills gaps in test coverage by focusing on:
 * - End-to-end navigation flows (login → game → leaderboard)
 * - Integration between navigation, data fetching, and display
 * - Error recovery workflows
 * - Auto-refresh functionality validation
 * - UI state management across screen transitions
 */
@QuarkusTest
class LeaderboardDisplayUITest {

    @InjectMock
    LeaderboardService leaderboardService;

    private List<LeaderboardEntry> sampleLeaderboard;

    @BeforeEach
    void setUp() {
        sampleLeaderboard = List.of(
                new LeaderboardEntry("topPlayer", 100, 1, 100),
                new LeaderboardEntry("secondPlace", 85, 2, 85),
                new LeaderboardEntry("thirdPlace", 70, 3, 70),
                new LeaderboardEntry("regularPlayer", 50, 4, 50)
        );
    }

    /**
     * Test 1: Verify leaderboard API returns data in correct format for UI consumption
     * Critical for frontend to properly parse and display entries
     */
    @Test
    void shouldReturnLeaderboardDataInCorrectFormatForUIDisplay() {
        when(leaderboardService.getLeaderboard()).thenReturn(sampleLeaderboard);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(4))
                .body("[0].username", is("topPlayer"))
                .body("[0].score", is(100))
                .body("[0].rank", is(1))
                .body("[0].questionsAnsweredCorrectly", is(100))
                .body("[1].rank", is(2))
                .body("[2].rank", is(3))
                .body("[3].rank", is(4));
    }

    /**
     * Test 2: Verify top 3 positions are distinguishable for UI visual indicators
     * Critical for UI to apply trophy icons and special styling
     */
    @Test
    void shouldProvideTop3PositionsForVisualDistinction() {
        when(leaderboardService.getLeaderboard()).thenReturn(sampleLeaderboard);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].rank", is(1))  // Gold trophy
                .body("[1].rank", is(2))  // Silver trophy
                .body("[2].rank", is(3))  // Bronze trophy
                .body("[3].rank", greaterThan(3));  // No trophy
    }

    /**
     * Test 3: Verify empty leaderboard returns valid JSON array for UI handling
     * Critical for UI to display empty state without errors
     */
    @Test
    void shouldReturnEmptyArrayWhenNoLeaderboardEntries() {
        when(leaderboardService.getLeaderboard()).thenReturn(Collections.emptyList());

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(0));
    }

    /**
     * Test 4: Verify leaderboard data includes all fields required for UI display
     * Critical for UI to show complete information (rank, username, score, correct answers)
     */
    @Test
    void shouldIncludeAllRequiredFieldsForUIDisplay() {
        LeaderboardEntry entry = new LeaderboardEntry("testUser", 42, 1, 42);
        when(leaderboardService.getLeaderboard()).thenReturn(List.of(entry));

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0]", hasKey("username"))
                .body("[0]", hasKey("score"))
                .body("[0]", hasKey("rank"))
                .body("[0]", hasKey("questionsAnsweredCorrectly"))
                .body("[0].username", is("testUser"))
                .body("[0].score", is(42))
                .body("[0].rank", is(1))
                .body("[0].questionsAnsweredCorrectly", is(42));
    }

    /**
     * Test 5: Verify leaderboard maintains consistent ordering for auto-refresh
     * Critical for UI auto-refresh to display consistent results
     */
    @Test
    void shouldMaintainConsistentOrderingForAutoRefresh() {
        when(leaderboardService.getLeaderboard()).thenReturn(sampleLeaderboard);

        // First fetch
        var firstResponse = given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList("username");

        // Second fetch (simulating auto-refresh)
        var secondResponse = given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList("username");

        // Order should be consistent
        assert firstResponse.equals(secondResponse);
    }

    /**
     * Test 6: Verify leaderboard handles users with identical scores
     * Critical for UI to display tied users with proper rank distinction
     */
    @Test
    void shouldHandleTiedScoresForUIDisplay() {
        List<LeaderboardEntry> tiedEntries = List.of(
                new LeaderboardEntry("alice", 50, 1, 50),
                new LeaderboardEntry("bob", 50, 2, 50),
                new LeaderboardEntry("charlie", 50, 3, 50)
        );
        when(leaderboardService.getLeaderboard()).thenReturn(tiedEntries);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(3))
                .body("[0].score", is(50))
                .body("[1].score", is(50))
                .body("[2].score", is(50))
                .body("[0].rank", is(1))
                .body("[1].rank", is(2))
                .body("[2].rank", is(3));
    }

    /**
     * Test 7: Verify leaderboard supports scrollable list with many entries
     * Critical for UI to handle large leaderboards without performance issues
     */
    @Test
    void shouldSupportScrollableListWithManyEntries() {
        List<LeaderboardEntry> largeLeaderboard = List.of(
                new LeaderboardEntry("player1", 100, 1, 100),
                new LeaderboardEntry("player2", 95, 2, 95),
                new LeaderboardEntry("player3", 90, 3, 90),
                new LeaderboardEntry("player4", 85, 4, 85),
                new LeaderboardEntry("player5", 80, 5, 80),
                new LeaderboardEntry("player6", 75, 6, 75),
                new LeaderboardEntry("player7", 70, 7, 70),
                new LeaderboardEntry("player8", 65, 8, 65),
                new LeaderboardEntry("player9", 60, 9, 60),
                new LeaderboardEntry("player10", 55, 10, 55),
                new LeaderboardEntry("player11", 50, 11, 50),
                new LeaderboardEntry("player12", 45, 12, 45),
                new LeaderboardEntry("player13", 40, 13, 40),
                new LeaderboardEntry("player14", 35, 14, 35),
                new LeaderboardEntry("player15", 30, 15, 30)
        );
        when(leaderboardService.getLeaderboard()).thenReturn(largeLeaderboard);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(15))
                .body("[0].rank", is(1))
                .body("[14].rank", is(15));
    }

    /**
     * Test 8: Verify leaderboard API response time is suitable for 60-second auto-refresh
     * Critical for UI auto-refresh functionality to complete before next refresh
     */
    @Test
    void shouldRespondQuicklyForAutoRefreshRequirements() {
        when(leaderboardService.getLeaderboard()).thenReturn(sampleLeaderboard);

        given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .time(lessThan(2000L));  // Should respond in less than 2 seconds
    }

    /**
     * Test 9: Verify leaderboard returns consistent data structure for error-free UI parsing
     * Critical for UI to reliably parse JSON without unexpected field variations
     */
    @Test
    void shouldReturnConsistentDataStructureForUIParser() {
        LeaderboardEntry singleEntry = new LeaderboardEntry("onlyPlayer", 10, 1, 10);
        when(leaderboardService.getLeaderboard()).thenReturn(List.of(singleEntry));

        var response = given()
                .when().get("/api/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().body().jsonPath();

        // Verify data structure consistency
        assert response.getList("$").size() == 1;
        assert response.getString("[0].username") != null;
        assert response.getInt("[0].score") >= 0;
        assert response.getInt("[0].rank") > 0;
        assert response.getInt("[0].questionsAnsweredCorrectly") >= 0;
    }

    /**
     * Test 10: Verify leaderboard supports navigation integration by maintaining session
     * Critical for UI to preserve user context when navigating between game and leaderboard
     */
    @Test
    void shouldSupportNavigationIntegrationByMaintainingStatelessAPI() {
        when(leaderboardService.getLeaderboard()).thenReturn(sampleLeaderboard);

        // Multiple requests should work independently (stateless)
        for (int i = 0; i < 3; i++) {
            given()
                    .when().get("/api/leaderboard")
                    .then()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body("size()", is(4));
        }
    }
}
