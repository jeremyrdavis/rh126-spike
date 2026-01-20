# Spec Requirements: Leaderboard Calculation and API

## Initial Description
Create leaderboard ranking system that calculates scores based on correct answers, implements sorting logic, and exposes RESTful endpoint to retrieve current rankings with usernames and scores.

This is roadmap item 5 from the product roadmap, building on the existing Answer Submission and Validation feature (item 4) which already tracks user scores in the LeaderboardRepository and LeaderboardService.

## Requirements Discussion

### First Round Questions

**Q1:** For the REST endpoint, I'm assuming `GET /api/leaderboard` returning HTTP 200 for success and HTTP 500 for errors. Is that the correct endpoint path and response codes?
**Answer:** Yes, that is correct.

**Q2:** For the response structure, I'm thinking LeaderboardEntry with fields: username, score, rank, questionsAnsweredCorrectly. Should we include any additional fields like timestamp or percentile?
**Answer:** No additional fields needed. The four fields you mentioned are sufficient.

**Q3:** For sorting logic, I assume we want descending order by score (highest first), with alphabetical tiebreaker by username. Is that correct?
**Answer:** Yes, that is correct.

**Q4:** Should the endpoint return the complete leaderboard or support pagination (e.g., top 10, top 100)?
**Answer:** Return the complete leaderboard. No pagination or top-N limits needed.

**Q5:** I'm assuming we should add a `getLeaderboard()` method to the existing LeaderboardService class that delegates to the repository. Should we modify the existing `recordAnswer()` method or leave it unchanged?
**Answer:** Add getLeaderboard() method. Leave recordAnswer() unchanged.

**Q6:** For the response DTO, should we create a new `LeaderboardEntry` record in the `com.redhat.demos.model` package following the pattern of TriviaQuestion and AnswerResponse?
**Answer:** Yes, create LeaderboardEntry record in com.redhat.demos.model package.

**Q7:** I assume the leaderboard should use the existing thread-safe HashMap storage in LeaderboardRepository with no external dependencies. Is that correct?
**Answer:** Correct. Use existing HashMap storage, no external dependencies.

**Q8:** Are there any features we should explicitly exclude from this implementation, such as filtering by user, historical snapshots, or real-time WebSocket updates?
**Answer:** Exclude filtering by user, historical snapshots, and real-time WebSocket updates. This is a simple GET endpoint returning current state only.

### Existing Code to Reference

**Similar Features Identified:**
- Feature: Question Display API - Path: `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/QuestionResource.java`
  - Pattern: GET endpoint with Optional<T> service response, HTTP 200/404/500 error handling
  - Constructor injection of service layer with null validation
  - Try-catch exception handling for 500 errors

- Feature: Answer Submission API - Path: `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/AnswerResource.java`
  - Pattern: POST endpoint with Optional<T> service response, HTTP 200/400/404/500 error handling
  - Constructor injection with validation
  - Try-catch with specific exception types

- Components to potentially reuse:
  - DTOs: TriviaQuestion and AnswerResponse records in com.redhat.demos.model package
  - Repository pattern: LeaderboardRepository with HashMap storage at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/repository/LeaderboardRepository.java`
  - Service pattern: LeaderboardService at `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/service/LeaderboardService.java`

- Backend logic to reference:
  - LeaderboardRepository.getAllScores() method returns HashMap<String, Integer> with all user scores
  - LeaderboardRepository already tracks questionsAnsweredCorrectly count via userCorrectQuestions HashMap
  - Service layer pattern: thin service delegating to repository

### Follow-up Questions

No follow-up questions were needed. All requirements were clearly specified in the first round.

## Visual Assets

### Files Provided:
No visual assets provided.

### Visual Insights:
Not applicable.

## Requirements Summary

### Functional Requirements
- Create REST endpoint `GET /api/leaderboard` that returns current leaderboard rankings
- Calculate rank based on score (number of correct answers)
- Sort leaderboard entries in descending order by score (highest first)
- Apply alphabetical sorting by username as tiebreaker when scores are equal
- Return complete leaderboard with all users who have participated
- Retrieve data from existing LeaderboardRepository via LeaderboardService
- Return HTTP 200 with leaderboard data on success
- Return HTTP 500 on unexpected errors
- Expose questionsAnsweredCorrectly count for each user (already tracked in repository)

### Data Structure Requirements
- Create `LeaderboardEntry` record DTO with fields:
  - `String username` - The user's username
  - `int score` - Number of questions answered correctly (from userScores HashMap)
  - `int rank` - Calculated rank based on score and alphabetical tiebreaker
  - `int questionsAnsweredCorrectly` - Count of unique questions answered correctly (from userCorrectQuestions HashMap size)
- Place DTO in `com.redhat.demos.model` package following existing pattern
- Use Java record for immutability (matching TriviaQuestion and AnswerResponse patterns)

### Service Layer Requirements
- Add `getLeaderboard()` method to existing LeaderboardService class
- Method should return List<LeaderboardEntry> or appropriate collection type
- Delegate to LeaderboardRepository to retrieve all user scores
- Implement sorting logic in service layer (descending by score, then alphabetical by username)
- Calculate rank values based on sorted position
- Handle case where no users have participated (empty leaderboard)
- Preserve existing `recordAnswer()` method unchanged

### Repository Layer Requirements
- LeaderboardRepository already provides `getAllScores()` method returning HashMap<String, Integer>
- Repository tracks questionsAnsweredCorrectly via userCorrectQuestions HashMap (Set<UUID> per user)
- No changes required to repository layer
- Continue using thread-safe HashMap storage with no external dependencies

### REST Resource Requirements
- Create `LeaderboardResource` class in `com.redhat.demos` package
- Annotate with `@Path("/api/leaderboard")`
- Implement GET endpoint with:
  - `@GET` annotation
  - `@Produces(MediaType.APPLICATION_JSON)`
  - Return `Response` type following QuestionResource and AnswerResource patterns
- Constructor inject LeaderboardService with null validation
- Implement try-catch error handling:
  - Return HTTP 200 with List<LeaderboardEntry> on success
  - Return HTTP 500 with error message on unexpected exceptions
- Follow existing resource patterns for consistency

### Reusability Opportunities
- Follow QuestionResource GET endpoint pattern for structure
- Reuse AnswerResource error handling patterns (try-catch with specific status codes)
- Mirror TriviaQuestion and AnswerResponse record patterns for LeaderboardEntry DTO
- Apply existing constructor injection and null validation patterns
- Use existing LeaderboardRepository HashMap storage and getAllScores() method
- Extend LeaderboardService following established service layer delegation pattern

### Scope Boundaries

**In Scope:**
- REST endpoint `GET /api/leaderboard` returning current rankings
- LeaderboardEntry DTO with username, score, rank, questionsAnsweredCorrectly fields
- Sorting logic (descending by score, alphabetical tiebreaker)
- Service layer method getLeaderboard() in existing LeaderboardService
- LeaderboardResource REST controller
- Complete leaderboard retrieval (all participating users)
- HTTP 200 success and HTTP 500 error responses
- Using existing HashMap storage in LeaderboardRepository

**Out of Scope:**
- Pagination or top-N filtering (e.g., top 10 users)
- Filtering by specific user
- Historical leaderboard snapshots or versioning
- Real-time updates via WebSocket or Server-Sent Events
- Additional response fields beyond the four specified
- Changes to existing recordAnswer() method
- External database or caching dependencies
- Authentication or authorization (follows existing username-based pattern)
- Modification of existing repository methods

### Technical Considerations
- Integration point: Extends existing LeaderboardService and LeaderboardRepository
- Storage: In-memory HashMap in LeaderboardRepository (already implemented)
- Thread safety: HashMap storage already in use (acceptable for event context)
- Technology: Quarkus RESTEasy Reactive with Jackson JSON serialization
- Pattern alignment: Follows existing Resource, Service, Repository layering
- Error handling: Match QuestionResource and AnswerResource patterns
- DTO conventions: Java record in com.redhat.demos.model package
- Similar code patterns to follow:
  - QuestionResource for GET endpoint structure
  - AnswerResource for error handling approach
  - TriviaQuestion and AnswerResponse for record DTO pattern
  - LeaderboardService constructor injection and null validation
