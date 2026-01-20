# Specification: Leaderboard Calculation and API

## Goal
Create a REST API endpoint that retrieves current leaderboard rankings with calculated ranks, enabling players to see competitive standings based on correct answer scores with consistent tie-breaking logic.

## User Stories
- As a trivia game player, I want to view the leaderboard via API so that I can see how I rank against other players
- As a sales engineer, I want the leaderboard to display all participants with accurate rankings so that event attendees stay engaged through competition

## Specific Requirements

**REST Endpoint - Leaderboard Retrieval**
- Create REST resource class `LeaderboardResource` in package `com.redhat.demos`
- Implement `GET /api/leaderboard` endpoint using Jakarta WS-RS annotations
- Use `@Path("/api/leaderboard")`, `@GET`, and `@Produces(MediaType.APPLICATION_JSON)`
- Inject `LeaderboardService` via constructor injection following QuestionResource and AnswerResource patterns
- Validate injected LeaderboardService is non-null in constructor (throw IllegalArgumentException if null)
- Return `Response` object with HTTP 200 and List of LeaderboardEntry DTOs on success
- Return `Response` object with HTTP 500 and error message for unexpected exceptions
- Use try-catch error handling pattern from existing resource classes

**Response DTO - LeaderboardEntry Record**
- Create Java Record `LeaderboardEntry` in package `com.redhat.demos.model`
- Include four fields: `String username`, `int score`, `int rank`, `int questionsAnsweredCorrectly`
- `username` is the player's username
- `score` is the total number of points (same as questionsAnsweredCorrectly for MVP)
- `rank` is the calculated ranking position (1 for highest score, increments for lower scores)
- `questionsAnsweredCorrectly` is the count of unique questions answered correctly
- No validation annotations or custom constructors needed
- Record automatically serializes to JSON via Jackson following TriviaQuestion and AnswerResponse patterns

**Service Layer Enhancement - Leaderboard Retrieval**
- Add `getLeaderboard()` method to existing `LeaderboardService` class
- Return type: `List<LeaderboardEntry>`
- Method retrieves all user scores from `LeaderboardRepository.getAllScores()` which returns HashMap<String, Integer>
- Retrieve questionsAnsweredCorrectly count for each user from repository's userCorrectQuestions HashMap (Set size per user)
- Return empty list if no users have participated (HashMap is empty)
- Do not modify existing `recordAnswer()` method

**Repository Layer Enhancement - Data Access**
- Add `getQuestionsAnsweredCorrectly(String username)` method to `LeaderboardRepository`
- Return the size of the Set<UUID> for the given username from userCorrectQuestions HashMap
- Return 0 if username not found in HashMap
- No other changes needed to repository layer (getAllScores() already exists)
- Continue using thread-safe HashMap storage with no external dependencies

**Sorting and Ranking Logic**
- Sort leaderboard entries in descending order by score (highest score first)
- Apply alphabetical sorting by username as tiebreaker when scores are equal (case-sensitive, ascending)
- Calculate rank based on sorted position: first entry gets rank 1, second gets rank 2, etc.
- Users with identical scores and alphabetically tied usernames get sequential ranks (no shared ranks)
- Implement sorting in service layer using Java Comparator with chained comparison
- Map HashMap entries to LeaderboardEntry records with calculated rank values

**Complete Leaderboard Return**
- Return all users who have answered at least one question correctly (present in userScores HashMap)
- No pagination or top-N filtering (return complete leaderboard regardless of size)
- No filtering by specific user or score threshold
- Empty list is valid response when no users have participated

**Error Handling and HTTP Status Codes**
- Return HTTP 200 with List of LeaderboardEntry JSON for all successful requests (including empty list)
- Return HTTP 500 with generic message "Internal server error" for unexpected exceptions
- Use try-catch block in LeaderboardResource wrapping service call
- Use Response.ok(list).build() pattern for success responses
- Use Response.status(500).entity(message).build() pattern for error responses from QuestionResource and AnswerResource

## Visual Design
No visual assets provided for this backend API feature.

## Existing Code to Leverage

**LeaderboardRepository - HashMap storage and existing methods**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/repository/LeaderboardRepository.java`
- Already provides `getAllScores()` method returning HashMap<String, Integer> copy
- Stores userCorrectQuestions as HashMap<String, Set<UUID>> tracking which questions each user answered correctly
- Stores userScores as HashMap<String, Integer> tracking total score per user
- Uses computeIfAbsent and merge patterns for safe HashMap updates
- Follow @ApplicationScoped annotation pattern for singleton repository
- Add new method to expose questionsAnsweredCorrectly count (Set size) following getScore() pattern

**LeaderboardService - Service layer pattern**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/service/LeaderboardService.java`
- Uses @ApplicationScoped annotation for singleton service
- Constructor-based dependency injection with null validation for LeaderboardRepository
- Existing recordAnswer() method delegates to repository (leave unchanged)
- Add getLeaderboard() method following same delegation pattern

**QuestionResource - REST endpoint pattern for GET requests**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/QuestionResource.java`
- Follow same package structure (`com.redhat.demos`) for new LeaderboardResource
- Use constructor injection with null validation throwing IllegalArgumentException
- Use try-catch block for error handling with specific HTTP status codes
- Return Response.ok(data).build() for successful responses
- Use @Path, @GET, @Produces(MediaType.APPLICATION_JSON) annotation pattern

**AnswerResource - Error handling pattern**
- Located at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/AnswerResource.java`
- Follow try-catch pattern with catch blocks for specific exception types
- Use Response.status(code).entity(message).build() for error responses
- Constructor validates injected service is non-null
- Follow same structure for LeaderboardResource error handling

**TriviaQuestion and AnswerResponse - Record DTO patterns**
- TriviaQuestion at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/model/TriviaQuestion.java`
- AnswerResponse at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/model/AnswerResponse.java`
- Use simple Java record with field declarations only
- No validation annotations or custom constructors
- Jackson automatically handles JSON serialization for records
- Place LeaderboardEntry in same com.redhat.demos.model package

## Out of Scope
- Pagination or top-N limits (e.g., top 10 players only)
- Filtering by specific user or username prefix
- Filtering by minimum score threshold
- Historical leaderboard snapshots or versioning
- Real-time updates via WebSocket or Server-Sent Events
- Leaderboard reset or clear endpoint
- Additional response fields beyond username, score, rank, questionsAnsweredCorrectly
- Time-based rankings or time period filtering
- Shared rank values for tied scores (sequential ranks only)
- Changes to existing recordAnswer() method in service or repository
- External database or caching layer
- Authentication or authorization for endpoint access
- Rate limiting or throttling mechanisms
