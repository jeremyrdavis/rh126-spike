# Task Breakdown: Leaderboard Calculation and API

## Overview
Total Tasks: 12
Total Task Groups: 4

This implementation creates a REST API endpoint that retrieves current leaderboard rankings with calculated ranks, enabling players to see competitive standings based on correct answer scores with consistent tie-breaking logic.

## Task List

### Data Transfer Layer

#### Task Group 1: LeaderboardEntry DTO Record
**Dependencies:** None

- [x] 1.0 Complete LeaderboardEntry DTO
  - [x] 1.1 Write 2-8 focused tests for LeaderboardEntry record
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., record creation, field accessors, JSON serialization)
    - Skip exhaustive testing of all field combinations
    - Test file: `src/test/java/com/redhat/demos/model/LeaderboardEntryTest.java`
  - [x] 1.2 Create LeaderboardEntry Java Record in package com.redhat.demos.model
    - Define four fields: `String username`, `int score`, `int rank`, `int questionsAnsweredCorrectly`
    - No validation annotations needed
    - No custom constructors needed (use default record constructor)
    - Record automatically serializes to JSON via Jackson
  - [x] 1.3 Ensure DTO layer tests pass
    - Run ONLY the 2-8 tests written in 1.1
    - Verify record creation and accessors work
    - Verify JSON serialization produces expected structure
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 1.1 pass
- LeaderboardEntry record has all four fields: username, score, rank, questionsAnsweredCorrectly
- Record serializes correctly to JSON
- No validation or custom logic needed

### Service and Repository Layer

#### Task Group 2: Repository and Service Enhancement
**Dependencies:** Task Group 1

- [x] 2.0 Complete repository and service enhancements
  - [x] 2.1 Write 2-8 focused tests for repository and service functionality
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., getQuestionsAnsweredCorrectly returns correct count, getLeaderboard sorts correctly, tiebreaking logic works)
    - Skip exhaustive testing of edge cases
    - Test file: `src/test/java/com/redhat/demos/repository/LeaderboardRepositoryTest.java` (enhance existing)
    - Test file: `src/test/java/com/redhat/demos/service/LeaderboardServiceTest.java` (enhance existing)
  - [x] 2.2 Add getQuestionsAnsweredCorrectly method to LeaderboardRepository
    - Method signature: `int getQuestionsAnsweredCorrectly(String username)`
    - Return the size of the Set<UUID> for the given username from userCorrectQuestions HashMap
    - Return 0 if username not found in HashMap
    - Use `userCorrectQuestions.getOrDefault(username, Set.of()).size()` pattern
  - [x] 2.3 Add getAllScores method to LeaderboardRepository (if not exists)
    - Method signature: `Map<String, Integer> getAllScores()`
    - Return copy of userScores HashMap to prevent external modification
    - Use `new HashMap<>(userScores)` to create defensive copy
    - Return empty map if no users have participated
  - [x] 2.4 Implement getLeaderboard method in LeaderboardService
    - Method signature: `List<LeaderboardEntry> getLeaderboard()`
    - Call `leaderboardRepository.getAllScores()` to retrieve HashMap<String, Integer>
    - Return empty list if HashMap is empty
    - For each username in HashMap:
      - Get score from HashMap value
      - Call `leaderboardRepository.getQuestionsAnsweredCorrectly(username)` to get count
      - Create entry with username, score, rank (to be calculated), questionsAnsweredCorrectly
  - [x] 2.5 Implement sorting and ranking logic in getLeaderboard
    - Sort entries in descending order by score (highest score first)
    - Apply alphabetical sorting by username as tiebreaker when scores are equal (case-sensitive, ascending)
    - Use Java Comparator with chained comparison:
      - `Comparator.comparing(LeaderboardEntry::score).reversed().thenComparing(LeaderboardEntry::username)`
    - Calculate rank based on sorted position: first entry gets rank 1, second gets rank 2, etc.
    - Use iteration index to assign sequential rank values
    - No shared ranks for tied scores (each entry gets unique sequential rank)
  - [x] 2.6 Ensure repository and service layer tests pass
    - Run ONLY the 2-8 tests written in 2.1
    - Verify getQuestionsAnsweredCorrectly returns correct count
    - Verify getAllScores returns defensive copy
    - Verify getLeaderboard sorts by score descending
    - Verify tiebreaking by username ascending
    - Verify rank assignment is sequential
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 2.1 pass
- LeaderboardRepository.getQuestionsAnsweredCorrectly returns correct count from Set size
- LeaderboardRepository.getAllScores returns defensive copy of userScores
- LeaderboardService.getLeaderboard returns sorted list by score descending
- Tiebreaker sorts alphabetically by username ascending
- Rank values are sequential (1, 2, 3, ...) based on sorted position
- Empty list returned when no users have participated

### API Layer

#### Task Group 3: REST Endpoint
**Dependencies:** Task Group 2

- [x] 3.0 Complete REST API endpoint
  - [x] 3.1 Write 2-8 focused tests for LeaderboardResource endpoint
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors (e.g., HTTP 200 with valid JSON array, HTTP 200 with empty array when no users, HTTP 500 on error)
    - Skip exhaustive testing of all response scenarios
    - Test file: `src/test/java/com/redhat/demos/LeaderboardResourceTest.java`
  - [x] 3.2 Create LeaderboardResource class in package com.redhat.demos
    - Annotate class with @Path("/api/leaderboard")
    - Use constructor injection to inject LeaderboardService
    - Add null validation for LeaderboardService in constructor
    - Throw IllegalArgumentException if service is null
    - Store service as private final field
  - [x] 3.3 Implement GET /api/leaderboard endpoint
    - Create method annotated with @GET, @Produces(MediaType.APPLICATION_JSON)
    - Call `leaderboardService.getLeaderboard()` to retrieve List<LeaderboardEntry>
    - Return Response.ok(list).build() for successful retrieval (HTTP 200)
    - Wrap service call in try-catch block for error handling
  - [x] 3.4 Implement error handling
    - Return HTTP 200 with List of LeaderboardEntry JSON for all successful requests (including empty list)
    - Return HTTP 500 with message "Internal server error" for unexpected exceptions
    - Use try-catch block wrapping service call
    - Use Response.ok(list).build() pattern for success responses
    - Use Response.status(500).entity("Internal server error").build() pattern for error responses
    - Follow error handling pattern from QuestionResource and AnswerResource
  - [x] 3.5 Ensure API layer tests pass
    - Run ONLY the 2-8 tests written in 3.1
    - Verify endpoint returns HTTP 200 with valid JSON array
    - Verify endpoint returns HTTP 200 with empty array when no users
    - Verify endpoint returns HTTP 500 for errors
    - Verify JSON structure matches LeaderboardEntry schema
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 3.1 pass
- GET /api/leaderboard endpoint exists and responds with JSON
- HTTP 200 returned with List<LeaderboardEntry> for successful requests
- HTTP 200 returned with empty array when no users have participated
- HTTP 500 returned for unexpected errors
- Constructor validates injected service is non-null
- Follows existing resource patterns (QuestionResource, AnswerResource)

### Testing

#### Task Group 4: Integration Testing and Gap Analysis
**Dependencies:** Task Groups 1-3

- [x] 4.0 Review existing tests and fill critical gaps only
  - [x] 4.1 Review tests from Task Groups 1-3
    - Review the 2-8 tests written for LeaderboardEntry DTO (Task 1.1)
    - Review the 2-8 tests written for repository and service enhancements (Task 2.1)
    - Review the 2-8 tests written for LeaderboardResource endpoint (Task 3.1)
    - Total existing tests: approximately 6-24 tests
  - [x] 4.2 Analyze test coverage gaps for this feature only
    - Identify critical end-to-end workflows that lack test coverage
    - Focus ONLY on gaps related to Leaderboard Calculation and API feature requirements
    - Do NOT assess entire application test coverage
    - Prioritize integration tests over additional unit tests
    - Key workflows to verify:
      - Request leaderboard -> Service retrieves and sorts data -> API returns sorted JSON
      - Leaderboard with multiple users sorted by score descending
      - Leaderboard with tied scores sorted alphabetically by username
      - Rank calculation is sequential and matches sorted order
      - Empty leaderboard returns HTTP 200 with empty array
      - questionsAnsweredCorrectly matches actual count from repository
  - [x] 4.3 Write up to 10 additional strategic tests maximum
    - Add maximum of 10 new integration tests to fill identified critical gaps
    - Focus on end-to-end API workflows (request -> retrieval -> sorting -> ranking -> response)
    - Test JSON response structure matches LeaderboardEntry array schema
    - Test sorting logic with multiple users at different scores
    - Test tiebreaking logic with users at same score
    - Test rank calculation accuracy across sorted entries
    - Test questionsAnsweredCorrectly accuracy for each user
    - Verify empty leaderboard scenario
    - Do NOT write comprehensive coverage for all scenarios
    - Skip performance tests and load testing unless business-critical
  - [x] 4.4 Run feature-specific tests only
    - Run ONLY tests related to Leaderboard Calculation and API feature
    - Expected total: approximately 16-34 tests maximum
    - Verify all critical workflows pass
    - Verify JSON serialization is correct
    - Verify sorting logic works correctly
    - Verify ranking logic works correctly
    - Verify tiebreaking logic works correctly
    - Do NOT run the entire application test suite

**Acceptance Criteria:**
- All feature-specific tests pass (approximately 16-34 tests total)
- Critical end-to-end workflows are covered (API request -> sorting -> ranking -> JSON response)
- JSON response structure verified to match LeaderboardEntry array schema
- Sorting logic verified (score descending, username ascending for ties)
- Ranking logic verified (sequential ranks based on sorted position)
- Tiebreaking logic verified (alphabetical by username)
- questionsAnsweredCorrectly count verified for accuracy
- Empty leaderboard scenario verified
- No more than 10 additional tests added when filling in testing gaps

## Execution Order

Recommended implementation sequence:
1. Data Transfer Layer (Task Group 1) - LeaderboardEntry DTO Record
2. Service and Repository Layer (Task Group 2) - Repository and service enhancements with sorting and ranking
3. API Layer (Task Group 3) - REST endpoint with error handling
4. Integration Testing (Task Group 4) - End-to-end verification and gap analysis

## Implementation Notes

### Package Structure
- DTO Model: `com.redhat.demos.model.LeaderboardEntry`
- Repository: `com.redhat.demos.repository.LeaderboardRepository` (enhance existing)
- Service: `com.redhat.demos.service.LeaderboardService` (enhance existing)
- REST Resource: `com.redhat.demos.LeaderboardResource`

### Key Design Patterns
- **Constructor Injection**: Follow existing pattern with null validation (QuestionService, QuestionResource, AnswerResource)
- **Defensive Copy**: Return new HashMap from getAllScores to prevent external modification
- **Comparator Chaining**: Use Comparator.comparing().reversed().thenComparing() for sorting with tiebreaker
- **Error Responses**: Use Response.status().entity().build() pattern (QuestionResource, AnswerResource)
- **CDI Beans**: Use @ApplicationScoped for repositories and services
- **Record DTOs**: Use Java Records for immutable data transfer objects

### Reference Files
- Pattern reference: `QuestionResource.java` for GET endpoint structure and error handling
- Pattern reference: `AnswerResource.java` for error handling approach
- Pattern reference: `TriviaQuestion.java` and `AnswerResponse.java` for record DTO pattern
- Pattern reference: `LeaderboardRepository.java` for HashMap storage patterns
- Pattern reference: `LeaderboardService.java` for service layer delegation

### Critical Requirements
- Sort by score descending (highest score first)
- Apply alphabetical tiebreaker by username ascending (case-sensitive)
- Calculate rank based on sorted position (1, 2, 3, ...)
- No shared ranks for tied scores (sequential ranks only)
- Return complete leaderboard (all users who have participated)
- Return empty list when no users have participated (HTTP 200 with empty array)
- Expose questionsAnsweredCorrectly count from userCorrectQuestions Set size
- Use defensive copy of userScores HashMap in getAllScores
- Follow HTTP status code conventions: 200 (success), 500 (server error)
- Limit testing to 2-8 tests per task group, with maximum 10 additional integration tests

### Sorting and Ranking Algorithm
1. Retrieve all user scores from LeaderboardRepository.getAllScores()
2. For each username, get score and questionsAnsweredCorrectly count
3. Create list of entries (username, score, temporary rank, questionsAnsweredCorrectly)
4. Sort list using Comparator:
   - Primary: score descending (highest first)
   - Tiebreaker: username ascending (alphabetical, case-sensitive)
5. Iterate through sorted list and assign rank:
   - First entry (index 0) gets rank 1
   - Second entry (index 1) gets rank 2
   - Continue sequentially (rank = index + 1)
6. Return sorted and ranked list

### HashMap Access Patterns
- `getAllScores()`: Returns defensive copy `new HashMap<>(userScores)`
- `getQuestionsAnsweredCorrectly(username)`: Returns `userCorrectQuestions.getOrDefault(username, Set.of()).size()`
- Both methods return safe values when username not found (empty HashMap, 0 count)

### Error Handling
- HTTP 200: Successful retrieval (including empty list)
- HTTP 500: Unexpected exceptions with generic "Internal server error" message
- No HTTP 404 needed (empty list is valid response for no users)
- Use try-catch block in LeaderboardResource wrapping service call
- Follow error handling pattern from existing resource classes

### JSON Response Structure
```json
[
  {
    "username": "alice",
    "score": 15,
    "rank": 1,
    "questionsAnsweredCorrectly": 15
  },
  {
    "username": "bob",
    "score": 12,
    "rank": 2,
    "questionsAnsweredCorrectly": 12
  },
  {
    "username": "charlie",
    "score": 12,
    "rank": 3,
    "questionsAnsweredCorrectly": 12
  }
]
```

### Tiebreaking Example
Users with same score (12) are sorted alphabetically:
- "bob" comes before "charlie" alphabetically
- "bob" gets rank 2, "charlie" gets rank 3
- Ranks are sequential, not shared
