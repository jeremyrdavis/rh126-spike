# Task Breakdown: Answer Submission and Validation

## Overview
Total Tasks: 20
Total Task Groups: 5

This implementation creates a REST API endpoint that accepts user answer submissions, validates them against stored correct answers, tracks scores via leaderboard storage, and returns validation results with the next question to enable smooth game flow.

## Task List

### Data Transfer Layer

#### Task Group 1: Request and Response DTOs
**Dependencies:** None

- [x] 1.0 Complete DTO records for answer submission
  - [x] 1.1 Write 2-8 focused tests for AnswerSubmission and AnswerResponse records
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., record creation, field accessors, JSON deserialization for AnswerSubmission, JSON serialization for AnswerResponse)
    - Skip exhaustive testing of all field combinations
    - Test file: `src/test/java/com/redhat/demos/model/AnswerSubmissionTest.java`
    - Test file: `src/test/java/com/redhat/demos/model/AnswerResponseTest.java`
  - [x] 1.2 Create AnswerSubmission Java Record in package com.redhat.demos.model
    - Define three fields: `String username`, `UUID questionId`, `String selectedAnswer`
    - No validation annotations needed (validation in service layer)
    - No custom constructors needed (use default record constructor)
    - Record automatically deserializes from JSON via Jackson
  - [x] 1.3 Create AnswerResponse Java Record in package com.redhat.demos.model
    - Define four fields: `boolean isCorrect`, `String correctAnswer`, `Question originalQuestion`, `TriviaQuestion nextQuestion`
    - No validation annotations needed
    - No custom constructors needed (use default record constructor)
    - Record automatically serializes to JSON via Jackson
  - [x] 1.4 Ensure DTO layer tests pass
    - Run ONLY the 2-8 tests written in 1.1
    - Verify record creation and accessors work
    - Verify JSON deserialization for AnswerSubmission
    - Verify JSON serialization for AnswerResponse produces expected structure
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 1.1 pass
- AnswerSubmission record has username, questionId, and selectedAnswer fields
- AnswerResponse record has isCorrect, correctAnswer, originalQuestion, and nextQuestion fields
- Records serialize/deserialize correctly to/from JSON
- No validation or custom logic needed in records

### Repository Layer

#### Task Group 2: Leaderboard Storage
**Dependencies:** None

- [x] 2.0 Complete leaderboard repository
  - [x] 2.1 Write 2-8 focused tests for LeaderboardRepository functionality
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., score increments on first correct answer, duplicate prevention works, multiple users tracked separately)
    - Skip exhaustive coverage of edge cases
    - Test file: `src/test/java/com/redhat/demos/repository/LeaderboardRepositoryTest.java`
  - [x] 2.2 Create LeaderboardRepository class in package com.redhat.demos.repository
    - Annotate with @ApplicationScoped for singleton CDI bean
    - Create two private HashMap fields: `HashMap<String, Set<UUID>> userCorrectQuestions` and `HashMap<String, Integer> userScores`
    - Initialize both HashMaps in constructor
    - No @PostConstruct needed for empty maps
  - [x] 2.3 Implement recordAnswer method
    - Method signature: `void recordAnswer(String username, UUID questionId, boolean isCorrect)`
    - If isCorrect is false, return immediately (no score penalty)
    - If isCorrect is true, check if questionId is already in user's Set
    - Use `userCorrectQuestions.computeIfAbsent(username, k -> new HashSet<>())` to initialize new users
    - Use `Set.add()` to add questionId to user's Set and detect duplicates
    - If `Set.add()` returns true (new question), increment score in userScores map
    - If `Set.add()` returns false (duplicate), do not modify score
    - Use `userScores.merge(username, 1, Integer::sum)` for score increment
  - [x] 2.4 Ensure repository layer tests pass
    - Run ONLY the 2-8 tests written in 2.1
    - Verify score increments correctly on first correct answer
    - Verify duplicate correct answers do not increment score
    - Verify incorrect answers do not affect score
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 2.1 pass
- LeaderboardRepository is @ApplicationScoped bean
- recordAnswer method correctly handles first correct answers
- Duplicate prevention works via Set.add() return value
- Multiple users tracked independently in separate HashMaps
- No score penalty for incorrect answers

### Service Layer - Part 1

#### Task Group 3: Leaderboard Service
**Dependencies:** Task Group 2

- [x] 3.0 Complete leaderboard service
  - [x] 3.1 Write 2-8 focused tests for LeaderboardService functionality
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., delegates to repository correctly, handles null repository gracefully)
    - Skip exhaustive testing of business logic (covered in repository tests)
    - Test file: `src/test/java/com/redhat/demos/service/LeaderboardServiceTest.java`
  - [x] 3.2 Create LeaderboardService class in package com.redhat.demos.service
    - Annotate with @ApplicationScoped for singleton CDI bean
    - Use constructor injection to inject LeaderboardRepository
    - Add null validation for LeaderboardRepository in constructor
    - Throw IllegalArgumentException if repository is null
    - Store repository as private final field
  - [x] 3.3 Implement recordAnswer method
    - Method signature: `void recordAnswer(String username, UUID questionId, boolean isCorrect)`
    - Delegate directly to `leaderboardRepository.recordAnswer(username, questionId, isCorrect)`
    - No additional business logic needed for MVP
  - [x] 3.4 Ensure leaderboard service tests pass
    - Run ONLY the 2-8 tests written in 3.1
    - Verify constructor null validation works
    - Verify delegation to repository works correctly
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 3.1 pass
- LeaderboardService is @ApplicationScoped bean
- Constructor validates repository is non-null
- recordAnswer method delegates to repository
- Follows existing service layer patterns (QuestionService)

### Service Layer - Part 2

#### Task Group 4: Answer Validation Service
**Dependencies:** Task Groups 1, 3

- [x] 4.0 Complete answer validation service
  - [x] 4.1 Write 2-8 focused tests for AnswerService functionality
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., correct answer validation works, incorrect answer validation works, calls LeaderboardService, returns next question)
    - Skip exhaustive testing of edge cases
    - Test file: `src/test/java/com/redhat/demos/service/AnswerServiceTest.java`
  - [x] 4.2 Create AnswerService class in package com.redhat.demos.service
    - Annotate with @ApplicationScoped for singleton CDI bean
    - Use constructor injection to inject QuestionService, LeaderboardService, and EnvironmentRepository
    - Add null validation for all three dependencies in constructor
    - Throw IllegalArgumentException if any dependency is null
    - Store all dependencies as private final fields
  - [x] 4.3 Implement input validation in submitAnswer method
    - Method signature: `Optional<AnswerResponse> submitAnswer(AnswerSubmission submission)`
    - Validate username is non-null and non-empty (throw IllegalArgumentException with message "Invalid username")
    - Validate questionId is non-null (throw IllegalArgumentException with message "Invalid questionId")
    - Validate selectedAnswer is non-null and one of "A", "B", "C", "D" (throw IllegalArgumentException with message "Invalid answer selection")
    - Use String.isEmpty() for username validation
    - Use Set.of("A", "B", "C", "D").contains(selectedAnswer) for answer validation
  - [x] 4.4 Implement answer validation logic
    - Call `questionService.getQuestionById(submission.questionId())` to retrieve question
    - If Optional is empty, return Optional.empty()
    - Extract Question from Optional
    - Iterate through `question.optionalAnswers()` list to find Answer where `isCorrect() == true`
    - Map index (0-3) to correctAnswer letter: 0="A", 1="B", 2="C", 3="D"
    - Use switch expression or if-else chain for index to letter mapping
    - Compare `submission.selectedAnswer()` to correctAnswer for equality
    - Store result in boolean isCorrect variable
  - [x] 4.5 Implement score tracking and response construction
    - Call `leaderboardService.recordAnswer(submission.username(), submission.questionId(), isCorrect)`
    - Call `questionService.getRandomTriviaQuestion()` to get next question
    - Extract TriviaQuestion from Optional or use null if empty
    - Construct AnswerResponse with: isCorrect, correctAnswer, question (original Question object), nextQuestion (TriviaQuestion or null)
    - Return Optional.of(answerResponse)
  - [x] 4.6 Ensure answer service tests pass
    - Run ONLY the 2-8 tests written in 4.1
    - Verify input validation throws appropriate exceptions
    - Verify correct answer validation works
    - Verify incorrect answer validation works
    - Verify LeaderboardService is called
    - Verify next question is included in response
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 4.1 pass
- AnswerService is @ApplicationScoped bean
- Constructor validates all three dependencies are non-null
- submitAnswer validates username, questionId, and selectedAnswer
- Answer validation correctly compares selectedAnswer to correctAnswer
- Calls LeaderboardService.recordAnswer with validation result
- Returns AnswerResponse with all four fields populated
- Returns Optional.empty() when question not found

### API Layer

#### Task Group 5: REST Endpoint
**Dependencies:** Task Group 4

- [x] 5.0 Complete REST API endpoint
  - [x] 5.1 Write 2-8 focused tests for AnswerResource endpoint
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., HTTP 200 with valid submission, HTTP 400 for invalid input, HTTP 404 when question not found, HTTP 500 on error)
    - Skip exhaustive testing of all error scenarios
    - Test file: `src/test/java/com/redhat/demos/AnswerResourceTest.java`
  - [x] 5.2 Create AnswerResource class in package com.redhat.demos
    - Annotate class with @Path("/api/answers")
    - Use constructor injection to inject AnswerService
    - Add null validation for AnswerService in constructor
    - Throw IllegalArgumentException if service is null
    - Store service as private final field
  - [x] 5.3 Implement POST /api/answers endpoint
    - Create method annotated with @POST, @Consumes(MediaType.APPLICATION_JSON), @Produces(MediaType.APPLICATION_JSON)
    - Accept AnswerSubmission parameter in request body
    - Wrap service call in try-catch block
    - Call `answerService.submitAnswer(submission)` and store result
    - If Optional contains AnswerResponse, return it directly via `Response.ok(answerResponse).build()` (HTTP 200)
    - If Optional is empty, return `Response.status(404).entity("Question not found").build()`
  - [x] 5.4 Implement error handling
    - Catch IllegalArgumentException for validation errors
    - Return HTTP 400 with exception message as entity
    - Use `Response.status(400).entity(e.getMessage()).build()` pattern
    - Catch all other exceptions (Exception class)
    - Return HTTP 500 with "Internal server error" message
    - Use `Response.status(500).entity("Internal server error").build()` pattern
    - Follow try-catch pattern from QuestionResource
  - [x] 5.5 Ensure API layer tests pass
    - Run ONLY the 2-8 tests written in 5.1
    - Verify endpoint returns HTTP 200 with valid AnswerResponse JSON
    - Verify endpoint returns HTTP 400 with appropriate message for invalid input
    - Verify endpoint returns HTTP 404 when question not found
    - Verify endpoint returns HTTP 500 for unexpected errors
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 5.1 pass
- POST /api/answers endpoint exists and accepts/produces JSON
- HTTP 200 returned with AnswerResponse for successful submissions
- HTTP 400 returned with message for invalid input (username, questionId, selectedAnswer)
- HTTP 404 returned with message when question not found
- HTTP 500 returned for unexpected errors
- Follows existing REST resource patterns (QuestionResource)

### Testing

#### Task Group 6: Integration Testing and Gap Analysis
**Dependencies:** Task Groups 1-5

- [x] 6.0 Review existing tests and fill critical gaps only
  - [x] 6.1 Review tests from Task Groups 1-5
    - Review the 2-8 tests written for DTO records (Task 1.1)
    - Review the 2-8 tests written for LeaderboardRepository (Task 2.1)
    - Review the 2-8 tests written for LeaderboardService (Task 3.1)
    - Review the 2-8 tests written for AnswerService (Task 4.1)
    - Review the 2-8 tests written for AnswerResource endpoint (Task 5.1)
    - Total existing tests: approximately 10-40 tests
  - [x] 6.2 Analyze test coverage gaps for this feature only
    - Identify critical end-to-end workflows that lack test coverage
    - Focus ONLY on gaps related to Answer Submission and Validation feature requirements
    - Do NOT assess entire application test coverage
    - Prioritize integration tests over additional unit tests
    - Key workflows to verify:
      - Submit correct answer -> Score increments -> Next question returned
      - Submit incorrect answer -> Score unchanged -> Next question returned
      - Submit duplicate correct answer -> Score unchanged -> Next question returned
      - Submit answer to non-existent question -> 404 error
      - Submit invalid input -> 400 error with appropriate message
  - [x] 6.3 Write up to 10 additional strategic tests maximum
    - Add maximum of 10 new integration tests to fill identified critical gaps
    - Focus on end-to-end API workflows (request -> validation -> score tracking -> response)
    - Test JSON request/response structure matches AnswerSubmission/AnswerResponse schemas
    - Test that correct answer letter ("A"-"D") is correctly determined from Answer list
    - Test that duplicate prevention works across multiple API calls
    - Test that multiple users can submit answers independently
    - Do NOT write comprehensive coverage for all scenarios
    - Skip performance tests and load testing unless business-critical
  - [x] 6.4 Run feature-specific tests only
    - Run ONLY tests related to Answer Submission and Validation feature
    - Expected total: approximately 20-50 tests maximum
    - Verify all critical workflows pass
    - Verify JSON serialization/deserialization is correct
    - Verify answer validation logic works correctly
    - Verify duplicate prevention works
    - Verify score tracking works for multiple users
    - Do NOT run the entire application test suite

**Acceptance Criteria:**
- All feature-specific tests pass (approximately 20-50 tests total)
- Critical end-to-end workflows are covered (answer submission -> validation -> score tracking -> response)
- JSON request/response structure verified to match DTO schemas
- Answer validation logic verified to correctly map Answer list index to letter
- Duplicate prevention verified to work correctly
- Multiple user score tracking verified to work independently
- No more than 10 additional tests added when filling in testing gaps

## Execution Order

Recommended implementation sequence:
1. Data Transfer Layer (Task Group 1) - AnswerSubmission and AnswerResponse DTOs
2. Repository Layer (Task Group 2) - LeaderboardRepository with dual HashMap storage
3. Service Layer - Part 1 (Task Group 3) - LeaderboardService for score tracking
4. Service Layer - Part 2 (Task Group 4) - AnswerService with validation logic
5. API Layer (Task Group 5) - REST endpoint with error handling
6. Integration Testing (Task Group 6) - End-to-end verification and gap analysis

## Implementation Notes

### Package Structure
- DTO Models: `com.redhat.demos.model.AnswerSubmission`, `com.redhat.demos.model.AnswerResponse`
- Repository: `com.redhat.demos.repository.LeaderboardRepository`
- Services: `com.redhat.demos.service.LeaderboardService`, `com.redhat.demos.service.AnswerService`
- REST Resource: `com.redhat.demos.AnswerResource`

### Key Design Patterns
- **Constructor Injection**: Follow existing pattern with null validation (QuestionService, QuestionResource)
- **Optional Return Types**: Use Optional<AnswerResponse> for service methods that may not find data
- **HashMap Storage**: Use dual HashMaps for Set-based duplicate tracking and score tracking
- **Error Responses**: Use Response.status().entity().build() pattern (QuestionResource)
- **CDI Beans**: Use @ApplicationScoped for repositories and services
- **Record DTOs**: Use Java Records for immutable data transfer objects

### Reference Files
- Pattern reference: `QuestionResource.java` for REST endpoint structure and error handling
- Pattern reference: `QuestionService.java` for constructor injection and Optional returns
- Pattern reference: `QuestionRepository.java` for HashMap storage patterns
- Pattern reference: `TriviaQuestion.java` for record DTO pattern
- Pattern reference: `Question.java` and `Answer.java` for domain model navigation

### Critical Requirements
- Map Answer list index (0-3) to correctAnswer letter ("A"-"D") correctly
- Use Set.add() return value to detect duplicate correct answers
- Only increment score on first correct answer to each question
- No score penalty for incorrect answers
- Include both original Question and next TriviaQuestion in AnswerResponse
- Validate selectedAnswer is exactly one of "A", "B", "C", "D" (case-sensitive)
- Follow HTTP status code conventions: 200 (success), 400 (invalid input), 404 (question not found), 500 (server error)
- Limit testing to 2-8 tests per task group, with maximum 10 additional integration tests
- Use IllegalArgumentException with descriptive messages for validation errors
- Return Optional.empty() when question not found (not an exception)

### Answer Validation Algorithm
1. Retrieve Question by questionId from QuestionService
2. Iterate through Question.optionalAnswers() list
3. Find Answer where isCorrect() == true
4. Determine index (0-3) of correct Answer in list
5. Map index to letter: 0="A", 1="B", 2="C", 3="D"
6. Compare selectedAnswer to correctAnswer letter for equality
7. Result is boolean isCorrect

### Duplicate Prevention Algorithm
1. Use HashMap<String, Set<UUID>> to track which questions each user answered correctly
2. When isCorrect is true, attempt to add questionId to user's Set
3. Set.add() returns true if questionId was not already in Set (first correct answer)
4. Set.add() returns false if questionId was already in Set (duplicate)
5. Only increment score in userScores HashMap when Set.add() returns true
6. Initialize new user with empty HashSet and score of 0 on first answer

### Error Message Mappings
- Invalid username (null or empty): "Invalid username" -> HTTP 400
- Invalid questionId (null): "Invalid questionId" -> HTTP 400
- Invalid selectedAnswer (not "A", "B", "C", or "D"): "Invalid answer selection" -> HTTP 400
- Question not found: "Question not found" -> HTTP 404
- Unexpected error: "Internal server error" -> HTTP 500
