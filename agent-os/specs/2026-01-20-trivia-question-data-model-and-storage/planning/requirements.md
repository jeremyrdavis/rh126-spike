# Spec Requirements: Trivia Question Data Model and Storage

## Initial Description
This is the second item from the roadmap. The feature involves:
- Creating the data model for trivia questions
- Implementing in-memory HashMap-based storage
- Question structure with multiple-choice answers
- Tracking correct answers

This feature is part of the Vegas Trivia application, an interactive trivia game for Red Hat's 2026 sales kickoff. It forms the foundation for storing and managing trivia questions that will be served through the REST API.

## Requirements Discussion

### First Round Questions

**Q1:** I assume each question should have a UUID, question text, an array of answer choices, and a reference to the correct answer. Should we store the correct answer as a reference (UUID) to one of the answers, or inline as a boolean flag on each Answer object?
**Answer:** Use a boolean flag on each Answer object (isCorrect field).

**Q2:** I'm thinking we should use a HashMap for storage where the key is the Question ID (UUID) and the value is the Question object. Is that correct, or would you prefer a different data structure?
**Answer:** HashMap with Question ID as key is correct.

**Q3:** For the data model, should we use Java Records (available in Java 17+) for immutability and clarity, or traditional POJOs with getters/setters?
**Answer:** Use Java Records since the project uses Java 17+.

**Q4:** I assume the Question Record should contain: UUID id, String questionText, List<Answer> optionalAnswers. Should the Answer Record contain: UUID id, String text, boolean isCorrect?
**Answer:** Confirmed - that structure is correct.

**Q5:** Should questions be loaded from a JSON resource file at application startup, or should we provide an API endpoint to add questions at runtime?
**Answer:** Load questions from JSON resource file at application startup. No API endpoint for adding questions in this iteration.

**Q6:** For thread safety in the HashMap, should we use ConcurrentHashMap, or is a regular HashMap with read-only access after initialization sufficient?
**Answer:** Regular HashMap is sufficient. Concurrent access to the same questions is acceptable since we're only reading after initialization.

**Q7:** Should we create a repository/service layer to encapsulate question storage and retrieval, or expose the HashMap directly through REST endpoints?
**Answer:** Create both QuestionRepository and QuestionService as CDI @ApplicationScoped beans following Quarkus conventions.

**Q8:** Will questions have categories or themes (e.g., "Las Vegas History", "Entertainment", "Geography"), or are all questions treated equally?
**Answer:** All questions are about Las Vegas. No categorization needed in the data model.

**Q9:** What should be OUT OF SCOPE for this iteration? For example: question editing/deletion via API, difficulty levels, question usage tracking, time limits per question?
**Answer:** Out of scope: Question editing/deletion via API, difficulty levels, question usage tracking, time limits per question.

### Existing Code to Reference
No similar existing features identified for reference.

### Follow-up Questions
None required. All requirements were clearly defined in the first round.

## Visual Assets

### Files Provided:
No visual assets provided.

### Visual Insights:
Not applicable - this is a data model and storage feature without UI components.

## Requirements Summary

### Functional Requirements
- Create immutable Java Record for Question containing: UUID id, String questionText, List<Answer> optionalAnswers
- Create immutable Java Record for Answer containing: UUID id, String text, boolean isCorrect
- Implement in-memory HashMap storage with Question UUID as key and Question object as value
- Use regular HashMap (not ConcurrentHashMap) since data is read-only after initialization
- Load questions from JSON resource file at application startup
- Create QuestionRepository as CDI @ApplicationScoped bean to encapsulate HashMap storage
- Create QuestionService as CDI @ApplicationScoped bean to provide business logic for question retrieval
- All questions are Las Vegas themed (no categorization or tagging in data model)
- Support retrieval of questions for API consumption

### Reusability Opportunities
No existing similar features identified. This is foundational data model work that will be referenced by:
- Question Display API (roadmap item #3)
- Answer Submission and Validation (roadmap item #4)
- Future question-related features

### Scope Boundaries

**In Scope:**
- Java Record definitions for Question and Answer domain models
- HashMap-based in-memory storage implementation
- Loading questions from JSON resource file at startup
- QuestionRepository bean for data access abstraction
- QuestionService bean for business logic
- Basic question retrieval methods (by ID, all questions, random selection)

**Out of Scope:**
- Question editing or deletion via API endpoints
- Difficulty level tracking or classification
- Question usage tracking (how many times answered, success rate)
- Time limits per question
- Question categorization or tagging system
- Database persistence (PostgreSQL, MongoDB, etc.)
- Question creation API endpoints
- Question validation beyond basic structure

### Technical Considerations
- Java 17+ language features (Records) are available and should be used
- Quarkus dependency injection with CDI @ApplicationScoped beans
- JSON deserialization for loading questions from resource files (Jackson)
- UUID generation for question and answer identifiers
- Immutability through Java Records ensures thread-safe read access
- In-memory storage is acceptable for sales kickoff event context (data persists during application runtime only)
- Regular HashMap sufficient since no concurrent writes occur after initialization
- Resource file location follows Quarkus conventions (src/main/resources/)
- Service layer pattern separates business logic from data access
- Foundation for future REST API endpoints in subsequent roadmap items
