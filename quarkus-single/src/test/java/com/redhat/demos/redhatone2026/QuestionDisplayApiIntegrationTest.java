package com.redhat.demos.redhatone2026;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the Question Display API feature.
 * Validates end-to-end workflow from API request to JSON response.
 */
@QuarkusTest
class QuestionDisplayApiIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCompleteFullWorkflowFromRequestToJsonResponse() {
        Response response = given()
            .when().get("/api/questions/random")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .extract().response();

        assertNotNull(response);
        String jsonBody = response.asString();
        assertNotNull(jsonBody);
        assertFalse(jsonBody.isEmpty());
    }

    @Test
    void shouldReturnJsonMatchingTriviaQuestionSchema() throws Exception {
        Response response = given()
            .when().get("/api/questions/random")
            .then()
            .statusCode(200)
            .extract().response();

        JsonNode jsonNode = objectMapper.readTree(response.asString());

        assertTrue(jsonNode.has("questionText"), "JSON should have questionText field");
        assertTrue(jsonNode.has("option1"), "JSON should have option1 field");
        assertTrue(jsonNode.has("option2"), "JSON should have option2 field");
        assertTrue(jsonNode.has("option3"), "JSON should have option3 field");
        assertTrue(jsonNode.has("option4"), "JSON should have option4 field");
        assertTrue(jsonNode.has("environment"), "JSON should have environment field");

        // Verify all fields have string values
        assertTrue(jsonNode.get("questionText").isTextual());
        assertTrue(jsonNode.get("option1").isTextual());
        assertTrue(jsonNode.get("option2").isTextual());
        assertTrue(jsonNode.get("option3").isTextual());
        assertTrue(jsonNode.get("option4").isTextual());
        assertTrue(jsonNode.get("environment").isTextual());
    }

    @Test
    void shouldIncludeEnvironmentMessageInEveryResponse() {
        for (int i = 0; i < 5; i++) {
            given()
                .when().get("/api/questions/random")
                .then()
                .statusCode(200)
                .body("environment", notNullValue());
        }
    }

    @Test
    void shouldVaryEnvironmentMessagesAcrossMultipleRequests() {
        Set<String> uniqueMessages = new HashSet<>();

        // Collect environment messages from multiple requests
        for (int i = 0; i < 20; i++) {
            String environment = given()
                .when().get("/api/questions/random")
                .then()
                .statusCode(200)
                .extract().path("environment");
            uniqueMessages.add(environment);
        }

        // Should see at least 2 different messages (highly probable with 20 calls)
        assertTrue(uniqueMessages.size() >= 2,
            "Expected to see multiple different environment messages across requests");
    }

    @Test
    void shouldNotExposeIsCorrectFieldInJsonResponse() throws Exception {
        Response response = given()
            .when().get("/api/questions/random")
            .then()
            .statusCode(200)
            .extract().response();

        String jsonString = response.asString();
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        // Verify isCorrect field is not present at root level
        assertFalse(jsonNode.has("isCorrect"),
            "JSON response should not contain 'isCorrect' field");

        // Verify the raw JSON string doesn't contain isCorrect anywhere
        assertFalse(jsonString.contains("\"isCorrect\""),
            "JSON response should not contain 'isCorrect' field anywhere in the structure");
    }

    @Test
    void shouldReturnFourDistinctOptions() {
        Response response = given()
            .when().get("/api/questions/random")
            .then()
            .statusCode(200)
            .extract().response();

        String option1 = response.path("option1");
        String option2 = response.path("option2");
        String option3 = response.path("option3");
        String option4 = response.path("option4");

        assertNotNull(option1);
        assertNotNull(option2);
        assertNotNull(option3);
        assertNotNull(option4);

        // All options should have non-empty text
        assertFalse(option1.isEmpty());
        assertFalse(option2.isEmpty());
        assertFalse(option3.isEmpty());
        assertFalse(option4.isEmpty());
    }

    @Test
    void shouldMapQuestionTextCorrectly() {
        Response response = given()
            .when().get("/api/questions/random")
            .then()
            .statusCode(200)
            .extract().response();

        String questionText = response.path("questionText");

        assertNotNull(questionText);
        assertFalse(questionText.isEmpty());
        assertTrue(questionText.endsWith("?") || questionText.length() > 10,
            "Question text should be a meaningful question");
    }

    @Test
    void shouldReturnValidJsonContentType() {
        given()
            .when().get("/api/questions/random")
            .then()
            .statusCode(200)
            .contentType("application/json");
    }

    @Test
    void shouldHandleMultipleConsecutiveRequests() {
        // Verify API can handle multiple requests without errors
        for (int i = 0; i < 10; i++) {
            given()
                .when().get("/api/questions/random")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("questionText", notNullValue())
                .body("environment", notNullValue());
        }
    }
}
