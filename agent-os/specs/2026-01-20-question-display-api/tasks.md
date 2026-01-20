# Task Breakdown: Question Display API

## Overview
Total Tasks: 16
Total Task Groups: 4

This implementation creates a REST API endpoint that retrieves random trivia questions with integrated Quarkus informational messages, enabling the frontend to display questions while naturally exposing product value propositions.

## Task List

### Repository Layer

#### Task Group 1: Environment Message Repository
**Dependencies:** None

- [x] 1.0 Complete environment message repository
  - [x] 1.1 Write 2-8 focused tests for EnvironmentRepository functionality
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., returns non-null message, returns one of the four expected messages, randomization works)
    - Skip exhaustive coverage of edge cases
    - Test file: `src/test/java/com/redhat/demos/repository/EnvironmentRepositoryTest.java`
  - [x] 1.2 Create EnvironmentRepository class in package com.redhat.demos.repository
    - Annotate with @ApplicationScoped for singleton CDI bean
    - Create four private static final String constants with placeholder Quarkus messages
    - Implement getRandomMessage() method returning String
    - Use ThreadLocalRandom.current().nextInt(4) for random selection (follows QuestionRepository.findRandom() pattern)
    - Return one of the four hardcoded messages
  - [x] 1.3 Ensure repository layer tests pass
    - Run ONLY the 2-8 tests written in 1.1
    - Verify getRandomMessage() returns expected messages
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 1.1 pass
- EnvironmentRepository is @ApplicationScoped bean
- getRandomMessage() returns one of four hardcoded messages
- Random selection uses ThreadLocalRandom pattern

### Data Transfer Layer

#### Task Group 2: TriviaQuestion DTO Record
**Dependencies:** None

- [x] 2.0 Complete TriviaQuestion DTO
  - [x] 2.1 Write 2-8 focused tests for TriviaQuestion record
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., record creation, field accessors, JSON serialization)
    - Skip exhaustive testing of all field combinations
    - Test file: `src/test/java/com/redhat/demos/model/TriviaQuestionTest.java`
  - [x] 2.2 Create TriviaQuestion Java Record in package com.redhat.demos.model
    - Define six String fields: questionText, option1, option2, option3, option4, environment
    - No validation annotations needed
    - No custom constructors needed (use default record constructor)
    - Record automatically serializes to JSON via Jackson
  - [x] 2.3 Ensure DTO layer tests pass
    - Run ONLY the 2-8 tests written in 2.1
    - Verify record creation and accessors work
    - Verify JSON serialization produces expected structure
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 2.1 pass
- TriviaQuestion record has all six String fields
- Record serializes correctly to JSON
- No validation or custom logic needed

### Service Layer

#### Task Group 3: QuestionService Enhancement
**Dependencies:** Task Groups 1, 2

- [x] 3.0 Complete QuestionService enhancement
  - [x] 3.1 Write 2-8 focused tests for service layer DTO mapping
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., successful DTO mapping with environment message, empty Optional when no questions, correct Answer-to-option mapping)
    - Skip exhaustive testing of all edge cases
    - Test file: `src/test/java/com/redhat/demos/service/QuestionServiceTest.java` (enhance existing)
  - [x] 3.2 Modify QuestionService constructor to inject EnvironmentRepository
    - Update constructor signature to accept both QuestionRepository and EnvironmentRepository
    - Add null validation for EnvironmentRepository following existing pattern
    - Store EnvironmentRepository as private final field
  - [x] 3.3 Implement getRandomTriviaQuestion() method
    - Return type: Optional<TriviaQuestion>
    - Call questionRepository.findRandom() to get Optional<Question>
    - If question not present, return Optional.empty()
    - If question present, map to TriviaQuestion DTO
  - [x] 3.4 Implement DTO mapping logic within getRandomTriviaQuestion()
    - Extract Question.questionText() to TriviaQuestion.questionText
    - Map Question.optionalAnswers() List to four option fields by index
      - Index 0 -> option1 (using Answer.text())
      - Index 1 -> option2 (using Answer.text())
      - Index 2 -> option3 (using Answer.text())
      - Index 3 -> option4 (using Answer.text())
    - Do NOT expose Answer.isCorrect() field in DTO
    - Call environmentRepository.getRandomMessage() to populate environment field
    - Return Optional.of(triviaQuestion)
  - [x] 3.5 Ensure service layer tests pass
    - Run ONLY the 2-8 tests written in 3.1
    - Verify DTO mapping extracts correct fields
    - Verify environment message is included
    - Verify Answer.isCorrect() is not exposed
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 3.1 pass
- QuestionService constructor accepts both repositories with null validation
- getRandomTriviaQuestion() returns Optional<TriviaQuestion>
- DTO mapping correctly extracts answer text without isCorrect field
- Environment message is populated from EnvironmentRepository

### API Layer

#### Task Group 4: REST Endpoint
**Dependencies:** Task Group 3

- [x] 4.0 Complete REST API endpoint
  - [x] 4.1 Write 2-8 focused tests for QuestionResource endpoint
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., HTTP 200 with valid JSON, HTTP 404 when no questions, HTTP 500 on error)
    - Skip exhaustive testing of all response scenarios
    - Test file: `src/test/java/com/redhat/demos/QuestionResourceTest.java`
  - [x] 4.2 Create QuestionResource class in package com.redhat.demos
    - Annotate class with @Path("/api/questions")
    - Use constructor injection to inject QuestionService
    - Add null validation for QuestionService in constructor
  - [x] 4.3 Implement GET /api/questions/random endpoint
    - Create method annotated with @GET, @Path("/random"), @Produces(MediaType.APPLICATION_JSON)
    - Call questionService.getRandomTriviaQuestion()
    - If Optional contains TriviaQuestion, return it directly (HTTP 200)
    - If Optional is empty, return Response.status(404).entity("No questions available").build()
    - Wrap in try-catch for unexpected exceptions
  - [x] 4.4 Implement error handling
    - Return HTTP 200 with TriviaQuestion JSON for successful retrieval
    - Return HTTP 404 with message "No questions available" when service returns empty Optional
    - Return HTTP 500 with message "Internal server error" for unexpected exceptions
    - Use Response.status(statusCode).entity(message).build() pattern from IndexResource
  - [x] 4.5 Add CORS configuration
    - Add property quarkus.http.cors=true to src/main/resources/application.properties
    - Enables frontend SPA to consume API from same-origin or different-origin contexts
  - [x] 4.6 Ensure API layer tests pass
    - Run ONLY the 2-8 tests written in 4.1
    - Verify endpoint returns HTTP 200 with valid JSON
    - Verify endpoint returns HTTP 404 when no questions exist
    - Verify endpoint returns HTTP 500 for errors
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 4.1 pass
- GET /api/questions/random endpoint exists and responds with JSON
- HTTP 200 returned with TriviaQuestion for successful requests
- HTTP 404 returned with appropriate message when no questions available
- HTTP 500 returned for unexpected errors
- CORS is enabled in application.properties

### Testing

#### Task Group 5: Integration Testing and Gap Analysis
**Dependencies:** Task Groups 1-4

- [x] 5.0 Review existing tests and fill critical gaps only
  - [x] 5.1 Review tests from Task Groups 1-4
    - Review the 2-8 tests written for EnvironmentRepository (Task 1.1)
    - Review the 2-8 tests written for TriviaQuestion DTO (Task 2.1)
    - Review the 2-8 tests written for QuestionService enhancement (Task 3.1)
    - Review the 2-8 tests written for QuestionResource endpoint (Task 4.1)
    - Total existing tests: approximately 8-32 tests
  - [x] 5.2 Analyze test coverage gaps for this feature only
    - Identify critical end-to-end workflows that lack test coverage
    - Focus ONLY on gaps related to Question Display API feature requirements
    - Do NOT assess entire application test coverage
    - Prioritize integration tests over additional unit tests
    - Key workflow to verify: Request random question -> Service maps DTO with environment message -> API returns JSON
  - [x] 5.3 Write up to 10 additional strategic tests maximum
    - Add maximum of 10 new integration tests to fill identified critical gaps
    - Focus on end-to-end API workflows (request -> response)
    - Test JSON response structure matches TriviaQuestion schema
    - Test that environment message is present and varies across requests
    - Test that Answer.isCorrect() field is NOT exposed in JSON response
    - Do NOT write comprehensive coverage for all scenarios
    - Skip performance tests and load testing unless business-critical
  - [x] 5.4 Run feature-specific tests only
    - Run ONLY tests related to Question Display API feature
    - Expected total: approximately 18-42 tests maximum
    - Verify all critical workflows pass
    - Verify JSON serialization is correct
    - Verify no sensitive data (isCorrect) is exposed
    - Do NOT run the entire application test suite

**Acceptance Criteria:**
- All feature-specific tests pass (approximately 18-42 tests total)
- Critical end-to-end workflow is covered (API request -> JSON response)
- JSON response structure verified to match TriviaQuestion schema
- Environment messages verified to be included in responses
- Answer.isCorrect() field verified to NOT appear in JSON responses
- No more than 10 additional tests added when filling in testing gaps

## Execution Order

Recommended implementation sequence:
1. Repository Layer (Task Group 1) - EnvironmentRepository for random messages
2. Data Transfer Layer (Task Group 2) - TriviaQuestion DTO Record
3. Service Layer (Task Group 3) - QuestionService enhancement with DTO mapping
4. API Layer (Task Group 4) - REST endpoint with error handling
5. Integration Testing (Task Group 5) - End-to-end verification and gap analysis

## Implementation Notes

### Package Structure
- Repository: `com.redhat.demos.repository.EnvironmentRepository`
- DTO Model: `com.redhat.demos.model.TriviaQuestion`
- Service: `com.redhat.demos.service.QuestionService` (enhance existing)
- REST Resource: `com.redhat.demos.QuestionResource`

### Key Design Patterns
- **Constructor Injection**: Follow existing pattern with null validation
- **Optional Return Types**: Use Optional<TriviaQuestion> for service methods
- **Random Selection**: Use ThreadLocalRandom.current().nextInt() pattern
- **Error Responses**: Use Response.status().entity().build() pattern
- **CDI Beans**: Use @ApplicationScoped for repositories and services

### Reference Files
- Pattern reference: `QuestionRepository.java` for random selection
- Pattern reference: `QuestionService.java` for constructor injection
- Pattern reference: `IndexResource.java` for Response object handling
- Pattern reference: `GreetingResource.java` for basic REST endpoint structure

### Critical Requirements
- DO NOT expose Answer.isCorrect() field in TriviaQuestion DTO
- Map Answer objects to option1-4 strings by index position
- Include random environment message in every TriviaQuestion response
- Enable CORS for frontend consumption
- Follow RESTful conventions with appropriate HTTP status codes (200, 404, 500)
- Limit testing to 2-8 tests per task group, with maximum 10 additional integration tests
