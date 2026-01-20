# Task Breakdown: Trivia Question Data Model and Storage

## Overview
Total Tasks: 17
Feature: Foundational data model and in-memory storage for trivia questions using Java Records and HashMap-based storage

## Task List

### Domain Model Layer

#### Task Group 1: Java Records for Question and Answer
**Dependencies:** None

- [x] 1.0 Complete domain model records
  - [x] 1.1 Write 2-8 focused tests for domain models
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors: Record instantiation, immutability, field access, serialization
    - Skip exhaustive edge case testing
    - Place tests in: `src/test/java/com/redhat/demos/model/QuestionTest.java` and `AnswerTest.java`
  - [x] 1.2 Create Answer record in com.redhat.demos.model package
    - Fields: `UUID id`, `String text`, `boolean isCorrect`
    - Use Java Record syntax for immutability
    - Ensure Jackson JSON deserialization compatibility (no custom deserializers needed)
    - Follow Java naming conventions
  - [x] 1.3 Create Question record in com.redhat.demos.model package
    - Fields: `UUID id`, `String questionText`, `List<Answer> optionalAnswers`
    - Use Java Record syntax for immutability
    - Ensure answers list is immutable using `List.copyOf()` in compact constructor
    - Ensure Jackson JSON serialization/deserialization compatibility
  - [x] 1.4 Ensure domain model tests pass
    - Run ONLY the 2-8 tests written in 1.1
    - Verify Record instantiation and field access work correctly
    - Verify immutability constraints
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 1.1 pass
- Answer record has three immutable fields: id, text, isCorrect
- Question record has three immutable fields: id, questionText, optionalAnswers
- Both records are in com.redhat.demos.model package
- Jackson can serialize/deserialize records without custom configuration

### Data Access Layer

#### Task Group 2: QuestionRepository with HashMap Storage
**Dependencies:** Task Group 1

- [x] 2.0 Complete repository layer
  - [x] 2.1 Write 2-8 focused tests for QuestionRepository
    - Limit to 2-8 highly focused tests maximum
    - Test only critical repository behaviors: Bean initialization, findAll(), findById(), findRandom()
    - Use mock questions for test data (do NOT rely on questions.json in tests)
    - Skip exhaustive testing of error scenarios
    - Place tests in: `src/test/java/com/redhat/demos/repository/QuestionRepositoryTest.java`
  - [x] 2.2 Create QuestionRepository class in com.redhat.demos.repository package
    - Annotate with `@ApplicationScoped` for CDI bean lifecycle
    - Declare `HashMap<UUID, Question>` for in-memory storage (regular HashMap, not ConcurrentHashMap)
    - Initialize HashMap in constructor
  - [x] 2.3 Implement findAll() method
    - Return `List<Question>` containing all questions from HashMap
    - Use `new ArrayList<>(questionMap.values())` for conversion
  - [x] 2.4 Implement findById(UUID id) method
    - Return `Optional<Question>` for null-safe retrieval
    - Use `Optional.ofNullable(questionMap.get(id))`
  - [x] 2.5 Implement findRandom() method
    - Return `Optional<Question>` containing random question
    - Use `Random` or `ThreadLocalRandom` for selection
    - Return `Optional.empty()` if no questions exist
  - [x] 2.6 Implement @PostConstruct initialization method
    - Annotate method with `@PostConstruct` for lifecycle hook
    - Load questions from `src/main/resources/questions.json` using Jackson ObjectMapper
    - Deserialize JSON array into `List<Question>`
    - Populate HashMap with questions using their UUID as key
    - Log number of questions loaded at INFO level
    - Throw runtime exception if JSON file missing or malformed (fail-fast strategy)
  - [x] 2.7 Ensure repository layer tests pass
    - Run ONLY the 2-8 tests written in 2.1
    - Verify all repository methods work correctly
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 2.1 pass
- QuestionRepository is @ApplicationScoped CDI bean
- Regular HashMap used for storage (read-only after initialization)
- findAll(), findById(), findRandom() methods work correctly
- @PostConstruct loads questions from JSON at startup
- Application fails to start if JSON is missing/malformed
- Number of loaded questions logged at INFO level

### Business Logic Layer

#### Task Group 3: QuestionService
**Dependencies:** Task Group 2

- [x] 3.0 Complete service layer
  - [x] 3.1 Write 2-8 focused tests for QuestionService
    - Limit to 2-8 highly focused tests maximum
    - Test only critical service behaviors: CDI injection, delegation to repository
    - Mock QuestionRepository dependency for isolation
    - Skip exhaustive testing of validation scenarios
    - Place tests in: `src/test/java/com/redhat/demos/service/QuestionServiceTest.java`
  - [x] 3.2 Create QuestionService class in com.redhat.demos.service package
    - Annotate with `@ApplicationScoped` for CDI bean lifecycle
    - Use constructor injection for QuestionRepository dependency
    - Follow Quarkus CDI constructor injection pattern
  - [x] 3.3 Implement getAllQuestions() method
    - Delegate to `repository.findAll()`
    - Return `List<Question>`
    - Add basic validation to ensure repository is initialized (non-null)
  - [x] 3.4 Implement getQuestionById(UUID id) method
    - Delegate to `repository.findById(id)`
    - Return `Optional<Question>`
    - Add null check for id parameter
  - [x] 3.5 Implement getRandomQuestion() method
    - Delegate to `repository.findRandom()`
    - Return `Optional<Question>`
    - This will be used by future API endpoints for question rotation
  - [x] 3.6 Ensure service layer tests pass
    - Run ONLY the 2-8 tests written in 3.1
    - Verify service methods delegate correctly to repository
    - Verify CDI injection works properly
    - Do NOT run the entire test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 3.1 pass
- QuestionService is @ApplicationScoped CDI bean
- QuestionRepository injected via constructor injection
- getAllQuestions(), getQuestionById(), getRandomQuestion() delegate to repository
- Basic validation ensures repository is initialized before use

### Data Resources

#### Task Group 4: JSON Question Data
**Dependencies:** Task Group 1 (requires Question/Answer structure knowledge)

- [x] 4.0 Complete question data resources
  - [x] 4.1 Create questions.json in src/main/resources/ directory
    - Structure as JSON array of question objects
    - Each question: `{"id": "uuid-string", "questionText": "...", "optionalAnswers": [...]}`
    - Each answer: `{"id": "uuid-string", "text": "...", "isCorrect": true/false}`
    - Use proper JSON formatting with indentation for readability
  - [x] 4.2 Add 10-15 Las Vegas-themed trivia questions
    - Topics: Las Vegas history, landmarks, entertainment, geography
    - Each question has 3-4 answer options
    - Each question has exactly ONE correct answer (isCorrect: true)
    - Examples: "What year was Las Vegas founded?", "Which hotel has the largest casino floor?"
    - Use realistic, interesting trivia about Las Vegas
  - [x] 4.3 Validate JSON structure
    - Verify JSON is valid using online validator or IDE
    - Ensure all UUIDs are properly formatted
    - Ensure each question has exactly one correct answer
    - Test that Jackson can deserialize the JSON (manual verification or simple test)

**Acceptance Criteria:**
- questions.json file exists in src/main/resources/
- File contains 10-15 Las Vegas-themed questions
- JSON is well-formed and valid
- Each question has 3-4 answer options
- Each question has exactly one answer with isCorrect: true
- All ids are valid UUID strings

### Integration Testing

#### Task Group 5: End-to-End Verification
**Dependencies:** Task Groups 1-4

- [x] 5.0 Verify complete feature integration
  - [x] 5.1 Review existing tests from Task Groups 1-3
    - Review the 2-8 tests written for domain models (Task 1.1)
    - Review the 2-8 tests written for repository (Task 2.1)
    - Review the 2-8 tests written for service (Task 3.1)
    - Total existing tests: approximately 6-24 tests
  - [x] 5.2 Analyze integration test coverage gaps
    - Identify critical integration flows that lack coverage
    - Focus on: JSON loading, repository initialization, service-to-repository interaction
    - Do NOT assess entire application test coverage
    - Prioritize end-to-end data flow over unit test gaps
  - [x] 5.3 Write up to 10 additional integration tests maximum
    - Add maximum of 10 new tests for critical integration gaps
    - Test: Application startup with questions.json loading
    - Test: Service layer retrieving questions loaded from JSON
    - Test: Error handling for missing/malformed JSON
    - Do NOT write comprehensive coverage for all scenarios
    - Place integration tests in: `src/test/java/com/redhat/demos/integration/QuestionDataFlowTest.java`
  - [x] 5.4 Run feature-specific tests only
    - Run ONLY tests related to this feature (tests from 1.1, 2.1, 3.1, and 5.3)
    - Expected total: approximately 16-34 tests maximum
    - Verify all tests pass
    - Do NOT run the entire application test suite
  - [x] 5.5 Manual verification in dev mode
    - Start application with `./mvnw quarkus:dev`
    - Verify application starts successfully
    - Verify INFO log shows correct number of questions loaded
    - Test that service can retrieve questions (via dev console or temporary endpoint)

**Acceptance Criteria:**
- All feature-specific tests pass (approximately 16-34 tests total)
- Application starts successfully with questions.json loaded
- INFO log confirms correct number of questions loaded (10-15)
- No more than 10 additional integration tests added
- Service layer can successfully retrieve questions from repository
- Questions loaded from JSON are accessible via service methods

## Execution Order

Recommended implementation sequence:
1. Domain Model Layer (Task Group 1) - Foundation for all other layers
2. Data Resources (Task Group 4) - Can be done in parallel with repository development
3. Data Access Layer (Task Group 2) - Depends on domain models
4. Business Logic Layer (Task Group 3) - Depends on repository
5. Integration Testing (Task Group 5) - Verifies complete feature

## Notes

### Package Structure
All Java classes follow this package structure:
- `com.redhat.demos.model` - Question and Answer records
- `com.redhat.demos.repository` - QuestionRepository
- `com.redhat.demos.service` - QuestionService

### Test Structure
Mirror source structure in test directory:
- `src/test/java/com/redhat/demos/model/` - Domain model tests
- `src/test/java/com/redhat/demos/repository/` - Repository tests
- `src/test/java/com/redhat/demos/service/` - Service tests
- `src/test/java/com/redhat/demos/integration/` - Integration tests

### Thread Safety Approach
- Regular HashMap used (not ConcurrentHashMap)
- Data is read-only after @PostConstruct initialization
- Java Records provide immutability for thread-safe reads
- No synchronization needed since no writes occur after startup

### Error Handling Strategy
- Fail-fast approach: Application fails to start if questions.json is missing/malformed
- Use runtime exceptions during initialization (IllegalStateException or similar)
- Log errors before throwing exceptions for debugging
- No graceful degradation - questions are required for application to function

### Future Extensions
This foundation supports upcoming roadmap items:
- Question Display REST API (roadmap item 3)
- Answer Submission and Validation (roadmap item 4)
- Question rotation and usage tracking (future enhancement)
