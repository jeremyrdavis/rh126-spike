# Specification: Trivia Question Data Model and Storage

## Goal
Create the foundational data model and in-memory storage system for trivia questions, using Java Records for immutability and HashMap-based storage that loads Las Vegas-themed questions from a JSON resource file at application startup.

## User Stories
- As a developer, I want immutable domain models for Questions and Answers so that the data structure is thread-safe and clear in its design
- As a system, I want questions loaded from a JSON file at startup so that trivia content can be easily managed without code changes

## Specific Requirements

**Question Domain Model (Java Record)**
- Define Question record with three fields: UUID id, String questionText, and List<Answer> optionalAnswers
- Use Java Record syntax for immutability and concise implementation
- Place in package com.redhat.demos.model following existing project structure
- Ensure record is serializable for JSON processing via Jackson
- List of answers should be immutable (use List.copyOf or Collections.unmodifiableList in constructor if needed)

**Answer Domain Model (Java Record)**
- Define Answer record with three fields: UUID id, String text, and boolean isCorrect
- Use Java Record syntax for immutability
- Place in package com.redhat.demos.model with Question record
- Ensure exactly one answer per question has isCorrect set to true when loading data
- Support Jackson JSON deserialization without custom deserializers

**QuestionRepository (Data Access Layer)**
- Create @ApplicationScoped CDI bean in package com.redhat.demos.repository
- Initialize HashMap<UUID, Question> as the in-memory storage (regular HashMap, not ConcurrentHashMap)
- Implement method to retrieve all questions: List<Question> findAll()
- Implement method to retrieve question by ID: Optional<Question> findById(UUID id)
- Implement method to retrieve random question: Optional<Question> findRandom()
- Load questions from JSON resource file (src/main/resources/questions.json) during CDI bean initialization using @PostConstruct
- Use Jackson ObjectMapper to deserialize JSON array into List<Question> objects
- Populate HashMap with questions using their UUID as key after loading from JSON

**QuestionService (Business Logic Layer)**
- Create @ApplicationScoped CDI bean in package com.redhat.demos.service
- Inject QuestionRepository via CDI constructor injection
- Delegate to repository for question retrieval operations
- Provide getAllQuestions() method returning List<Question>
- Provide getQuestionById(UUID id) method returning Optional<Question>
- Provide getRandomQuestion() method for random question selection
- Add basic validation to ensure repository is properly initialized before returning data
- This layer will be extended in future specs to add business logic for question rotation and usage tracking

**JSON Resource File Structure**
- Create questions.json file in src/main/resources/ directory
- Structure as JSON array of question objects
- Each question object contains: id (UUID string), questionText (string), optionalAnswers (array)
- Each answer object contains: id (UUID string), text (string), isCorrect (boolean)
- Include 10-15 sample Las Vegas-themed trivia questions covering topics like history, landmarks, entertainment, and geography
- Ensure each question has exactly one correct answer (isCorrect: true) and 3-4 total answer options
- Format with proper indentation for readability and future maintenance

**Application Startup and Initialization**
- QuestionRepository loads questions.json during @PostConstruct lifecycle
- If JSON file is missing or malformed, log error and throw runtime exception to prevent application startup
- Log the number of questions successfully loaded at INFO level
- Ensure initialization completes before any REST endpoints become available
- No API endpoints for adding, editing, or deleting questions in this iteration

**Thread Safety and Concurrency**
- Use regular HashMap (not ConcurrentHashMap) since data is read-only after initialization
- Immutable Java Records ensure thread-safe access to question and answer data
- Multiple threads can safely read from the HashMap concurrently
- No write operations occur after initialization, eliminating need for synchronization

**Package Structure and Naming Conventions**
- Follow existing com.redhat.demos package structure
- Create model package for Question and Answer records
- Create repository package for QuestionRepository
- Create service package for QuestionService
- Use singular naming for domain models (Question, not Questions)
- Use descriptive method names following Java conventions (findAll, findById, findRandom)

## Visual Design
No visual assets provided - this is a backend data model and storage feature without UI components.

## Existing Code to Leverage

**GreetingResource.java REST endpoint pattern**
- Shows use of Jakarta WS-RS annotations (@Path, @GET, @Produces)
- Demonstrates MediaType.TEXT_PLAIN for response types
- Package structure: com.redhat.demos
- This pattern will be followed when creating Question API endpoints in subsequent specs

**IndexResource.java redirect pattern**
- Shows use of Response.seeOther for HTTP redirects
- Demonstrates URI.create for response location
- Pattern for handling root path requests
- Not directly applicable to this spec but useful for future frontend integration

**Quarkus dependency injection with CDI**
- Project uses Jakarta CDI for dependency injection
- @ApplicationScoped annotation for singleton beans
- Constructor injection pattern for dependencies
- @PostConstruct for initialization logic after bean creation
- This is the standard pattern to follow for QuestionRepository and QuestionService

**Jackson JSON processing**
- Project includes quarkus-rest-jackson dependency in pom.xml
- Jackson ObjectMapper available for JSON serialization/deserialization
- Automatic support for Java Records when using Jackson 2.12+
- Use ObjectMapper to read questions.json resource file

**Existing package structure: com.redhat.demos**
- All Java classes currently in com.redhat.demos package
- Subpackages should follow: model, repository, service, resource
- Maintains consistency with Quarkus and Java enterprise conventions
- Tests mirror source structure in src/test/java

## Out of Scope
- Question editing or deletion via API endpoints - no REST endpoints for modifying questions
- Difficulty level tracking or classification - all questions treated equally
- Question usage tracking such as answer count or success rate statistics
- Time limits per question - timing logic not part of data model
- Question categorization or tagging system - all questions are Las Vegas themed without categories
- Database persistence using PostgreSQL, MongoDB, or any external database
- Question creation API endpoints - questions only loaded from JSON file
- Question validation beyond basic structure - no duplicate checking or content validation
- ConcurrentHashMap or thread synchronization - regular HashMap sufficient for read-only access
- Custom Jackson deserializers - standard Jackson deserialization for Records is sufficient
