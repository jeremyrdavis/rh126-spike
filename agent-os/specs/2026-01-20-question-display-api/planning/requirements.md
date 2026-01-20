# Spec Requirements: Question Display API

## Initial Description

Creating REST API endpoints to retrieve trivia questions with the following capabilities:

- Creating REST API endpoints to retrieve trivia questions
- Integration with the existing QuestionService
- Randomized informational messages about Quarkus
- JSON response formatting

This is the third item from the product roadmap for the Vegas Trivia application.

The feature involves building the backend REST API layer that will:
1. Expose HTTP endpoints for question retrieval
2. Integrate with the QuestionService component
3. Include randomized informational messages about Quarkus alongside questions
4. Return properly formatted JSON responses

## Requirements Discussion

### First Round Questions

**Q1: API Endpoint Path and Methods**
I assume we should create `/api/questions` following REST conventions and using `@Produces(MediaType.APPLICATION_JSON)`. Should we support both GET for single random questions and GET with path parameters for specific questions, or just random retrieval initially?

**Answer:** Only `GET /api/questions/random` for now (retrieve single random question).

**Q2: Response DTO Structure**
I'm thinking we need a response DTO that includes the question text, four answer options (as strings), and the environment message. Should the DTO be a Java Record named `TriviaQuestion` with fields like `questionText`, `option1`, `option2`, `option3`, `option4`, and `environment`? Or would you prefer a different structure?

**Answer:** Yes, create `TriviaQuestion` Record with exactly those fields: String questionText, String option1, String option2, String option3, String option4, String environment.

**Q3: Environment Message Storage**
For the randomized environment messages about Quarkus, I assume we should create an `EnvironmentRepository` similar to `QuestionRepository` that stores four hardcoded message strings and has a method to return a random one. Is that the right approach?

**Answer:** Yes, create `EnvironmentRepository` that contains four hardcoded Strings (placeholder text for now) and has a method to return a random one of the four Strings.

**Q4: Service Layer Responsibilities**
I'm thinking the `QuestionService` should be enhanced to return the `TriviaQuestion` DTO by combining data from `QuestionRepository` and `EnvironmentRepository`, mapping the Answer objects to option strings without exposing the `isCorrect` field. Should the service layer handle this mapping, or would you prefer the resource/controller to do it?

**Answer:** Yes, `QuestionService` should return `TriviaQuestion` DTO by populating values from `QuestionRepository`, getting random environment message from `EnvironmentRepository`, and mapping Answer objects to option1-4 strings (without isCorrect field).

**Q5: Error Handling**
For error cases like no questions available, I assume we should return appropriate HTTP status codes (404 for not found, 500 for server errors) with generic error messages. Should we create a custom error response DTO or use simple string messages?

**Answer:** Generic error messages with appropriate HTTP status codes (200 for success, 404 for not found, 500 for server errors).

**Q6: CORS Configuration**
Since this API will be consumed by the frontend SPA, I assume we need to enable CORS in application.properties with `quarkus.http.cors=true`. Is that correct?

**Answer:** Yes, enable CORS in application.properties with `quarkus.http.cors=true`.

**Q7: Scope Boundaries**
Should we exclude any features like filtering, pagination, or sorting for this iteration, or should we plan for those capabilities now?

**Answer:** Out of scope: filtering, pagination, sorting - only random question retrieval.

### Existing Code to Reference

**Similar Features Identified:**
- Feature: GreetingResource - Path: `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/GreetingResource.java`
  - Jakarta WS-RS REST endpoint pattern with `@Path`, `@GET`, `@Produces` annotations

- Feature: IndexResource - Path: `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/IndexResource.java`
  - Response handling using `jakarta.ws.rs.core.Response`

- Feature: QuestionService - Path: `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/service/QuestionService.java`
  - Service layer with `@ApplicationScoped` annotation
  - Constructor-based dependency injection
  - Existing `getRandomQuestion()` method returning `Optional<Question>`

**Existing Domain Models:**
- Question Record: `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/model/Question.java`
  - Fields: UUID id, String questionText, List<Answer> optionalAnswers

- Answer Record: `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/model/Answer.java`
  - Fields: UUID id, String text, boolean isCorrect

## Visual Assets

### Files Provided:
No visual assets provided.

### Visual Insights:
No visual assets available for analysis.

## Requirements Summary

### Functional Requirements

**REST API Endpoint:**
- Create `GET /api/questions/random` endpoint to retrieve a single random trivia question
- Return JSON response using `@Produces(MediaType.APPLICATION_JSON)` annotation
- Follow Jakarta WS-RS conventions with `@Path`, `@GET` annotations

**Response Data Transfer Object:**
- Create new `TriviaQuestion` Record with fields:
  - `String questionText` - The trivia question text
  - `String option1` - First answer option (text only, no correctness indicator)
  - `String option2` - Second answer option
  - `String option3` - Third answer option
  - `String option4` - Fourth answer option
  - `String environment` - Randomized informational message about Quarkus

**Environment Message Repository:**
- Create new `EnvironmentRepository` class
- Store four hardcoded placeholder String messages
- Provide method to return a random message from the four options
- Use `@ApplicationScoped` annotation for singleton behavior

**Service Layer Enhancement:**
- Enhance existing `QuestionService` to support DTO creation
- Add method to retrieve random question and map to `TriviaQuestion` DTO
- Combine data from `QuestionRepository.findRandom()` and `EnvironmentRepository.getRandomMessage()`
- Map `Answer` objects to option1-4 strings, excluding the `isCorrect` field
- Handle cases where no questions exist

**Data Mapping:**
- Map from domain model `Question` with `List<Answer>` to flat DTO with four option strings
- Extract answer text from `Answer.text()`, omit `Answer.isCorrect()`
- Populate environment field with random message from `EnvironmentRepository`

**Error Handling:**
- Return HTTP 200 for successful question retrieval
- Return HTTP 404 when no questions are available
- Return HTTP 500 for unexpected server errors
- Use generic error messages (no custom error DTO for this iteration)

**Cross-Origin Resource Sharing:**
- Enable CORS in `application.properties` with property: `quarkus.http.cors=true`
- Allow frontend SPA to consume API endpoints

### Reusability Opportunities

**Existing REST Endpoint Patterns:**
- `GreetingResource.java` demonstrates basic Jakarta WS-RS endpoint structure
- Use `@Path`, `@GET`, `@Produces` annotation pattern
- Follow package structure: REST resources in root package `com.redhat.demos`

**Existing Response Handling:**
- `IndexResource.java` shows `Response` object usage for advanced scenarios
- For successful cases, can return DTO directly
- For error cases, use `Response` object with status codes

**Existing Service Layer Patterns:**
- `QuestionService.java` demonstrates service class structure
- Use `@ApplicationScoped` for service beans
- Use constructor-based dependency injection
- Return `Optional` for methods that may not find data

**Existing Domain Models:**
- Reference `Question` record structure for understanding question data
- Reference `Answer` record structure for mapping answer options
- Existing `QuestionRepository` provides `findRandom()` method

### Scope Boundaries

**In Scope:**
- Single REST endpoint: `GET /api/questions/random`
- New `TriviaQuestion` DTO record
- New `EnvironmentRepository` with four hardcoded messages
- Enhancement to `QuestionService` for DTO mapping
- Basic error handling with HTTP status codes
- CORS configuration for SPA access
- Integration with existing `QuestionRepository` via `QuestionService`

**Out of Scope:**
- Filtering questions by category, difficulty, or other criteria
- Pagination of question lists
- Sorting questions
- Retrieving specific questions by ID via API (exists in service layer but not exposed via REST)
- Retrieving all questions via API
- Custom error response DTOs
- Authentication or authorization
- Rate limiting
- Caching strategies
- Advanced CORS configuration (only basic enable)

**Future Enhancements:**
- Additional endpoints for specific question retrieval
- Question filtering and search capabilities
- Pagination support for question lists
- More sophisticated error responses with structured error objects
- Dynamic environment messages from database or configuration

### Technical Considerations

**Framework and Annotations:**
- Use Jakarta WS-RS (Jakarta RESTful Web Services) for REST endpoints
- Use Quarkus CDI (Contexts and Dependency Injection) for service layer
- Follow existing annotation patterns: `@Path`, `@GET`, `@Produces`, `@ApplicationScoped`

**JSON Serialization:**
- Leverage Quarkus's built-in Jackson integration for JSON serialization
- Java Records automatically serialize to JSON with field names as keys
- No custom serialization configuration needed for basic DTO

**Dependency Injection:**
- Inject `QuestionService` into REST resource using constructor injection
- Inject `QuestionRepository` and `EnvironmentRepository` into `QuestionService`
- Follow existing pattern of null-check validation in service constructors

**Data Transformation:**
- Service layer responsible for mapping domain models to DTOs
- Keep REST resource thin - delegate business logic to service layer
- Ensure Answer objects are mapped in consistent order to option1-4

**Configuration:**
- Add CORS configuration to `src/main/resources/application.properties`
- Use property: `quarkus.http.cors=true`

**Testing Considerations:**
- Unit tests should verify DTO mapping logic
- Test random message selection from `EnvironmentRepository`
- Test error scenarios (no questions available)
- Integration tests should verify REST endpoint behavior and JSON response format

**Alignment with Product Mission:**
- API enables demonstration of Quarkus REST capabilities
- Environment messages support product goal of naturally exposing Quarkus value propositions
- Simple, lightweight design aligns with event-optimized architecture
- No database dependency maintains in-memory storage approach

**Alignment with Tech Stack:**
- Uses Quarkus RESTEasy Reactive for REST endpoints
- Leverages Jackson for JSON serialization
- Follows Java Records best practices
- Compatible with existing in-memory HashMap storage pattern
- Integrates with Quarkus Web Bundler frontend architecture
