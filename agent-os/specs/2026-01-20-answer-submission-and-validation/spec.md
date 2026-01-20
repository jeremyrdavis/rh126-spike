# Specification: Answer Submission and Validation

## Goal
Create a REST API endpoint that accepts user answer submissions, validates them against stored correct answers, tracks scores via leaderboard storage, and returns validation results with the next question to enable smooth game flow.

## User Stories
- As a trivia game player, I want to submit my answers via API so that I can progress through the game
- As a trivia game player, I want to see if my answer was correct and receive the next question so that I can continue playing without interruption

## Specific Requirements

**REST Endpoint - Answer Submission**
- Create REST resource class `AnswerResource` in package `com.redhat.demos`
- Implement `POST /api/answers` endpoint using Jakarta WS-RS annotations
- Use `@Path("/api/answers")`, `@POST`, `@Consumes(MediaType.APPLICATION_JSON)`, and `@Produces(MediaType.APPLICATION_JSON)`
- Inject `AnswerService` via constructor injection following existing QuestionResource pattern
- Accept `AnswerSubmission` DTO in request body
- Return `AnswerResponse` DTO for successful submissions (HTTP 200)
- Return `Response` object with HTTP 400 for invalid input (null/empty username, null questionId, null/invalid selectedAnswer)
- Return `Response` object with HTTP 404 when questionId does not exist
- Return `Response` object with HTTP 500 for unexpected errors
- Follow try-catch error handling pattern from QuestionResource

**Request DTO - AnswerSubmission Record**
- Create Java Record `AnswerSubmission` in package `com.redhat.demos.model`
- Include three fields: `String username`, `UUID questionId`, `String selectedAnswer`
- No validation annotations needed (validation in service layer)
- Record automatically deserializes from JSON via Jackson

**Response DTO - AnswerResponse Record**
- Create Java Record `AnswerResponse` in package `com.redhat.demos.model`
- Include four fields: `boolean isCorrect`, `String correctAnswer`, `Question originalQuestion`, `TriviaQuestion nextQuestion`
- `isCorrect` indicates whether user's answer was correct
- `correctAnswer` is the letter ("A", "B", "C", or "D") of the correct answer
- `originalQuestion` is the full Question domain object the user answered (including all Answer objects with isCorrect flags)
- `nextQuestion` is the next TriviaQuestion DTO for game flow continuity (may be null if no more questions)
- Record automatically serializes to JSON via Jackson

**Service Layer - AnswerService**
- Create new `AnswerService` class in package `com.redhat.demos.service`
- Annotate with `@ApplicationScoped` for singleton CDI bean
- Inject `QuestionService`, `LeaderboardService`, and `EnvironmentRepository` via constructor
- Implement constructor null validation following QuestionService pattern
- Implement `submitAnswer(AnswerSubmission submission)` method returning `Optional<AnswerResponse>`
- Validate username is non-null and non-empty (throw IllegalArgumentException if invalid)
- Validate questionId is non-null (throw IllegalArgumentException if invalid)
- Validate selectedAnswer is non-null and one of "A", "B", "C", "D" (throw IllegalArgumentException if invalid)
- Use `QuestionService.getQuestionById(questionId)` to retrieve question (return Optional.empty() if not found)
- Determine correct answer by finding which Answer in the Question's optionalAnswers list has `isCorrect() == true`
- Map Answer list index (0-3) to letter ("A"-"D") for correctAnswer field in response
- Compare selectedAnswer to correctAnswer to determine isCorrect boolean
- Call `LeaderboardService.recordAnswer(username, questionId, isCorrect)` to track score
- Use `QuestionService.getRandomTriviaQuestion()` to get next question for response
- Construct and return Optional containing AnswerResponse DTO

**LeaderboardService - Score Tracking Logic**
- Create new `LeaderboardService` class in package `com.redhat.demos.service`
- Annotate with `@ApplicationScoped` for singleton CDI bean
- Inject `LeaderboardRepository` via constructor
- Implement constructor null validation following existing service patterns
- Implement `recordAnswer(String username, UUID questionId, boolean isCorrect)` method
- Delegate to `LeaderboardRepository.recordAnswer(username, questionId, isCorrect)` for persistence
- No additional business logic needed in service layer for MVP

**LeaderboardRepository - In-Memory Storage**
- Create new `LeaderboardRepository` class in package `com.redhat.demos.repository`
- Annotate with `@ApplicationScoped` for singleton CDI bean
- Use two HashMap fields: `HashMap<String, Set<UUID>> userCorrectQuestions` and `HashMap<String, Integer> userScores`
- Initialize HashMaps in constructor (no @PostConstruct needed for empty maps)
- Implement `recordAnswer(String username, UUID questionId, boolean isCorrect)` method
- If isCorrect is true, check if questionId is already in user's correct questions Set
- If questionId not already answered correctly, add to Set and increment score in userScores map
- If isCorrect is false, do nothing (no score penalty for incorrect answers)
- Use `Set.add()` return value to detect duplicates (returns false if already present)
- Initialize new user with empty HashSet and score of 0 if username not yet in maps
- Thread-safety not required for MVP (single-instance in-memory storage)

**Answer Validation Logic**
- Map selected answer letter to Answer list index: "A"->0, "B"->1, "C"->2, "D"->3
- Retrieve correct Answer object by iterating through Question.optionalAnswers() and finding where isCorrect() == true
- Determine correct answer letter by finding index of correct Answer in the list and mapping back to letter
- Compare user's selectedAnswer letter to correct answer letter for equality
- Case-sensitive comparison (always uppercase "A", "B", "C", "D")

**Error Handling and HTTP Status Codes**
- Return HTTP 200 with AnswerResponse JSON for all successful submissions (both correct and incorrect answers)
- Return HTTP 400 with message "Invalid username" when username is null or empty string
- Return HTTP 400 with message "Invalid questionId" when questionId is null
- Return HTTP 400 with message "Invalid answer selection" when selectedAnswer is not "A", "B", "C", or "D"
- Return HTTP 404 with message "Question not found" when questionId does not exist in QuestionRepository
- Return HTTP 500 with message "Internal server error" for unexpected exceptions
- Use try-catch block in AnswerResource wrapping service call
- Catch IllegalArgumentException for validation errors and return 400
- Use Response.status().entity().build() pattern for error responses

**Duplicate Answer Handling**
- Allow users to submit answers to same question multiple times
- Only award score points on first correct answer to each question
- Track which questions each user has answered correctly in Set to enable duplicate detection
- Do not modify score if user submits correct answer to previously correctly answered question
- Do not penalize score for incorrect answers (neither first attempt nor subsequent attempts)

## Visual Design
No visual assets provided for this backend API feature.

## Existing Code to Leverage

**QuestionResource - REST endpoint pattern**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/QuestionResource.java`
- Use same package structure (`com.redhat.demos`) for new `AnswerResource`
- Follow constructor injection pattern with null validation
- Use try-catch block with specific error status codes
- Return Response.ok() for success and Response.status() for errors
- Follow @Path, @GET/@POST, @Produces/@Consumes annotation patterns

**QuestionService - Service layer pattern**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/service/QuestionService.java`
- Use @ApplicationScoped annotation for new services
- Constructor-based dependency injection with null validation checks
- Return Optional<T> for methods that may not find data
- Delegate to repository layer for data access
- Leverage existing getQuestionById(UUID) and getRandomTriviaQuestion() methods in AnswerService

**QuestionRepository - HashMap storage pattern**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/repository/QuestionRepository.java`
- Follow HashMap<UUID, Entity> storage pattern for LeaderboardRepository
- Use @ApplicationScoped annotation for singleton repository
- Follow Optional.ofNullable() pattern for lookups that may not find data
- Initialize HashMap in constructor (or @PostConstruct if loading data)

**Question and Answer domain models**
- Question at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/model/Question.java`
- Answer at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/model/Answer.java`
- Use Answer.isCorrect() to identify correct answer from optionalAnswers list
- Access Question fields via question.id(), question.questionText(), question.optionalAnswers()
- Follow immutable record pattern for new AnswerSubmission and AnswerResponse DTOs

**TriviaQuestion DTO pattern**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/model/TriviaQuestion.java`
- Use simple record with field declarations only (no validation annotations)
- Jackson automatically handles serialization/deserialization
- Include in AnswerResponse to provide next question for game flow

## Out of Scope
- Time limits for answer submission
- Answer history retrieval endpoints
- Partial credit for answers
- Complex user authentication or authorization
- Session management or token-based auth
- Answer editing or deletion endpoints
- Score adjustment or reset endpoints
- Detailed scoring algorithms beyond correct answer counting
- External database persistence
- Real-time leaderboard push notifications
- Leaderboard retrieval endpoint (separate feature)
- Answer attempt tracking (only track first correct answer per question)
- Score penalties for incorrect answers
- Timed bonuses or multipliers
- Question difficulty weighting
