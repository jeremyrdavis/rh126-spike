package com.redhat.demos.redhatone2026;

import com.redhat.demos.redhatone2026.model.TriviaQuestion;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class QuestionResourceTest {

    @Test
    void shouldReturnRandomQuestionWithValidJsonStructure() {
        Response response = given()
            .when().get("/api/questions/random")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .body("questionText", notNullValue())
            .body("option1", notNullValue())
            .body("option2", notNullValue())
            .body("option3", notNullValue())
            .body("option4", notNullValue())
            .body("environment", notNullValue())
            .extract().response();

        TriviaQuestion question = response.as(TriviaQuestion.class);
        assertNotNull(question.questionText());
        assertNotNull(question.option1());
        assertNotNull(question.option2());
        assertNotNull(question.option3());
        assertNotNull(question.option4());
        assertNotNull(question.environment());
    }

    @Test
    void shouldReturnDifferentEnvironmentMessagesAcrossRequests() {
        String firstEnvironment = given()
            .when().get("/api/questions/random")
            .then()
            .statusCode(200)
            .extract().path("environment");

        // Make multiple requests to increase chance of getting different message
        String differentEnvironment = null;
        for (int i = 0; i < 10; i++) {
            String env = given()
                .when().get("/api/questions/random")
                .then()
                .statusCode(200)
                .extract().path("environment");
            if (!env.equals(firstEnvironment)) {
                differentEnvironment = env;
                break;
            }
        }

        assertNotNull(differentEnvironment, "Should eventually get a different environment message");
    }

    @Test
    void shouldIncludeEnvironmentMessageAboutQuarkus() {
        given()
            .when().get("/api/questions/random")
            .then()
            .statusCode(200)
            .body("environment", containsString("Quarkus"));
    }

    @Test
    void shouldNotExposeIsCorrectField() {
        Response response = given()
            .when().get("/api/questions/random")
            .then()
            .statusCode(200)
            .extract().response();

        String jsonResponse = response.asString();
        assertFalse(jsonResponse.contains("isCorrect"),
            "Response should not contain 'isCorrect' field");
    }
}
