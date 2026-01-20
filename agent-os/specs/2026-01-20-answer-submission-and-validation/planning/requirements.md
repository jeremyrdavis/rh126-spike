# Spec Requirements: Answer Submission and Validation

## Initial Description

This is the fourth item from the roadmap. The feature involves:

- Creating REST API endpoint to accept and validate user answers
- Tracking user responses with username mapping
- Validating answers against correct answers
- Returning validation results

This feature is part of the Vegas Trivia application for Red Hat's 2026 sales kickoff event. It builds on the foundation of the question retrieval system and enables the core trivia game interaction loop.

Source: Initiated from product roadmap `agent-os/product/roadmap.md` (Item 4)
Date: 2026-01-20

## Requirements Discussion

### First Round Questions

**Q1:** For the API endpoint structure, I'm assuming we should follow the existing pattern of `/api/questions` and create `/api/answers` as a POST endpoint. Is that correct?

**Answer:** Yes, POST /api/answers

**Q2:** For the request payload, I'm thinking we need a JSON object with username (String), questionId (String), and selectedAnswer (String representing "A", "B", "C", or "D"). Should we include any additional fields like timestamp or session identifiers?

**Answer:** No, just username, questionId, and selectedAnswer. Keep it simple for the MVP.

**Q3:** For the response format, should we return both the correct answer information AND the next question to keep the game flowing, or return just the validation result and require a separate API call for the next question?

**Answer:** Return the original question, the correct answer, and the next TriviaQuestion to enable smooth game flow.

**Q4:** For validation logic, I assume we'll retrieve the question by ID from the QuestionRepository, compare the user's selected answer (A/B/C/D) to the stored correct answer in the Answer objects, and return whether it was correct. Is that the right approach?

**Answer:** Yes, that's correct.

**Q5:** For score tracking, should we create a LeaderboardService and LeaderboardRepository now, or just validate answers without persisting scores yet?

**Answer:** Create LeaderboardService and LeaderboardRepository. Track username to total number of correct responses. Use in-memory HashMap. Do NOT give points for multiple correct answers to the same question - track which questions the user has answered correctly.

**Q6:** For HTTP status codes, I'm assuming 200 OK for successful submission (both correct and incorrect answers), 400 Bad Request for invalid input, and 404 Not Found if the questionId doesn't exist. Should we handle any other error cases?

**Answer:** That's correct. Those status codes cover our needs.

**Q7:** For duplicate answer handling, if a user submits an answer to the same question twice, should we allow it, reject it, or update their previous answer?

**Answer:** Allow the submission but don't give points again for answering the same question correctly multiple times.

**Q8:** For username validation, should we verify that the username exists in some user registry, or just validate that it's a non-empty string?

**Answer:** Just validate non-empty/non-null. Return 400 error for empty or null username.

**Q9:** Are there any features or behaviors we should explicitly exclude from this implementation?

**Answer:** No time limits, no answer history retrieval in this endpoint, no partial credit for answers.

### Existing Code to Reference

**Similar Features Identified:**
- Feature: Question Display API - Path: `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/java/com/redhat/demos/QuestionResource.java`
- Components to potentially reuse: REST endpoint patterns with constructor injection, error handling with HTTP status codes and try-catch blocks, Response.ok() and Response.status() patterns
- Backend logic to reference: QuestionService service layer pattern with ApplicationScoped annotation, QuestionRepository data access patterns with in-memory HashMap storage
- Data models to reference: TriviaQuestion DTO pattern for API responses, Question domain model with Answer objects containing isCorrect boolean flag

## Visual Assets

### Files Provided:
No visual files found (bash check performed).

### Visual Insights:
No visual assets provided.

## Requirements Summary

### Functional Requirements
- Accept user answer submissions via POST endpoint at `/api/answers`
- Request payload must contain username (String), questionId (String), and selectedAnswer (String - "A", "B", "C", or "D")
- Validate submitted answer against correct answer stored in Question domain model
- Return response containing original question, correct answer indication, and next TriviaQuestion for game flow continuity
- Track user scores in LeaderboardService and LeaderboardRepository using in-memory HashMap
- Map usernames to total number of correct responses
- Prevent awarding duplicate points when user answers same question correctly multiple times
- Track which questions each user has answered correctly to enable duplicate prevention
- Validate username is non-empty and non-null
- Handle invalid questionId by returning 404 Not Found
- Handle invalid input with 400 Bad Request
- Return 200 OK for both correct and incorrect answer submissions

### Reusability Opportunities
- QuestionResource REST endpoint patterns: constructor injection, @Path and @POST annotations, Response building
- QuestionService service layer patterns: @ApplicationScoped annotation, constructor validation, Optional return types
- TriviaQuestion DTO pattern for consistent API response structure
- Question and Answer domain models: immutable records, UUID identifiers, boolean correctness flags
- QuestionRepository data access patterns: in-memory HashMap storage, findById method pattern
- Error handling: try-catch blocks, HTTP status code responses, entity messages
- Constructor null validation pattern consistently used across services and resources

### Scope Boundaries

**In Scope:**
- POST /api/answers endpoint creation
- Answer validation against stored correct answers
- Score tracking with username to correct answer count mapping
- Duplicate answer detection to prevent double-scoring same question
- Response containing original question, correct answer, and next question
- Input validation for username and questionId
- HTTP status code responses for success and error cases
- LeaderboardService and LeaderboardRepository implementation with HashMap storage

**Out of Scope:**
- Time limits for answer submission
- Answer history retrieval endpoints
- Partial credit for answers
- Complex user authentication or authorization
- Session management
- Answer editing or deletion
- Detailed scoring algorithms beyond correct answer counting
- External database persistence
- Real-time leaderboard push notifications

### Technical Considerations
- Integration with existing QuestionRepository to retrieve questions by ID
- Consistency with existing service layer patterns using constructor injection and ApplicationScoped
- Response format should enable smooth frontend game flow by providing next question
- In-memory HashMap storage acceptable for MVP event context (data persists during runtime only)
- Follow existing domain model patterns: immutable records with UUID identifiers
- Selected answer format ("A", "B", "C", "D") must map to Answer list positions in Question model
- Need to determine correct answer by checking isCorrect boolean on Answer objects
- LeaderboardRepository should use HashMap<String, Set<UUID>> to track which questions each user answered correctly
- LeaderboardRepository should use HashMap<String, Integer> to track username to score
- Follow existing error handling patterns with try-catch and HTTP Response objects
- Align with Quarkus RESTEasy Reactive patterns and Jakarta annotations
