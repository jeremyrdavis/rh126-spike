# Specification: Question Display API

## Goal
Create a REST API endpoint that retrieves random trivia questions with integrated Quarkus informational messages, enabling the frontend to display questions to users while naturally exposing product value propositions.

## User Stories
- As a trivia game player, I want to receive random questions via API so that I can participate in the game
- As a sales engineer, I want Quarkus messages displayed alongside questions so that players learn about the product while playing

## Specific Requirements

**REST Endpoint - Random Question Retrieval**
- Create REST resource class `QuestionResource` in package `com.redhat.demos`
- Implement `GET /api/questions/random` endpoint using Jakarta WS-RS annotations
- Use `@Path("/api/questions")`, `@GET`, `@Path("/random")`, and `@Produces(MediaType.APPLICATION_JSON)`
- Inject `QuestionService` via constructor injection following existing patterns
- Return `TriviaQuestion` DTO directly for successful responses (HTTP 200)
- Return `Response` object with HTTP 404 status when no questions exist
- Return `Response` object with HTTP 500 status for unexpected errors

**Response DTO - TriviaQuestion Record**
- Create Java Record `TriviaQuestion` in package `com.redhat.demos.model`
- Include six String fields: `questionText`, `option1`, `option2`, `option3`, `option4`, `environment`
- No validation annotations or custom constructors needed
- Record automatically serializes to JSON via Jackson

**Environment Message Repository**
- Create `EnvironmentRepository` class in package `com.redhat.demos.repository`
- Annotate with `@ApplicationScoped` for singleton CDI bean
- Store four hardcoded String messages as private static final constants
- Implement `getRandomMessage()` method returning String
- Use `ThreadLocalRandom.current().nextInt(4)` to select random message (follows `QuestionRepository.findRandom()` pattern)

**Service Layer Enhancement - DTO Mapping**
- Add new method `getRandomTriviaQuestion()` to existing `QuestionService` class
- Return type: `Optional<TriviaQuestion>`
- Inject `EnvironmentRepository` via constructor (modify existing constructor to accept both repositories)
- Call `questionRepository.findRandom()` to get `Optional<Question>`
- If question exists, map to `TriviaQuestion` DTO by extracting answer text from `List<Answer>` to option1-4 fields
- Call `environmentRepository.getRandomMessage()` to populate environment field
- Return `Optional.empty()` if no question found
- Do not expose `Answer.isCorrect()` field in DTO response

**Data Mapping Logic**
- Extract `Question.questionText()` to `TriviaQuestion.questionText`
- Map `Question.optionalAnswers()` List to four option fields by index (0->option1, 1->option2, 2->option3, 3->option4)
- Extract only `Answer.text()` field from each Answer object
- Assumption: all questions have exactly 4 answers (validated by data loading)
- Populate `environment` field from `EnvironmentRepository.getRandomMessage()`

**Error Handling and HTTP Status Codes**
- Return HTTP 200 with `TriviaQuestion` JSON when question retrieved successfully
- Return HTTP 404 with generic message "No questions available" when `QuestionService.getRandomTriviaQuestion()` returns empty Optional
- Return HTTP 500 with generic message "Internal server error" for unexpected exceptions
- Use `Response.status(404).entity("message").build()` pattern from `IndexResource` for error responses
- Validate injected dependencies in service constructor following existing null-check pattern

**CORS Configuration**
- Add property `quarkus.http.cors=true` to `src/main/resources/application.properties`
- Enables frontend SPA to consume API from same-origin or different-origin contexts
- No additional CORS configuration needed for MVP

## Visual Design
No visual assets provided for this backend API feature.

## Existing Code to Leverage

**GreetingResource - REST endpoint pattern**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/GreetingResource.java`
- Demonstrates basic Jakarta WS-RS annotations: `@Path`, `@GET`, `@Produces`
- Follow same package structure (`com.redhat.demos`) for new `QuestionResource`
- Use `MediaType.APPLICATION_JSON` instead of `TEXT_PLAIN`

**IndexResource - Response object handling**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/IndexResource.java`
- Shows how to use `jakarta.ws.rs.core.Response` for non-standard status codes
- Use `Response.status(statusCode).entity(message).build()` for error cases
- Direct DTO return sufficient for successful 200 responses

**QuestionService - Service layer pattern**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/service/QuestionService.java`
- Use `@ApplicationScoped` annotation for service beans
- Constructor-based dependency injection with null validation
- Return `Optional<T>` for methods that may not find data
- Delegate to repository layer for data access
- Modify constructor to inject both `QuestionRepository` and `EnvironmentRepository`

**QuestionRepository - Repository pattern and random selection**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/repository/QuestionRepository.java`
- Uses `ThreadLocalRandom.current().nextInt()` for random selection
- Returns `Optional.empty()` when no data available
- Use same random selection pattern in `EnvironmentRepository.getRandomMessage()`
- Follow same `@ApplicationScoped` and `@PostConstruct` patterns if needed

**Question and Answer Records - Domain models**
- Question at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/model/Question.java`
- Answer at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/model/Answer.java`
- Map from `Question(id, questionText, List<Answer>)` to `TriviaQuestion(questionText, option1-4, environment)`
- Access Answer fields via `answer.text()` and avoid exposing `answer.isCorrect()`

## Out of Scope
- Retrieving all questions via API endpoint (exists in service layer only)
- Retrieving specific question by ID via API (exists in service layer only)
- Filtering questions by category, difficulty, or tags
- Pagination of question results
- Sorting questions by any criteria
- Custom error response DTOs with structured error objects
- Advanced CORS configuration with specific origins or headers
- Authentication or authorization for API access
- Rate limiting or throttling mechanisms
- Caching strategies for API responses
- Dynamic environment messages loaded from database or external configuration
