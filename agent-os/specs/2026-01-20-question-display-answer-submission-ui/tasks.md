# Task Breakdown: Question Display and Answer Submission UI

## Overview
Total Tasks: 4 Task Groups

## Task List

### HTML Structure Layer

#### Task Group 1: Question Display and Answer Selection UI Markup
**Dependencies:** None

- [x] 1.0 Complete HTML structure for question display
  - [x] 1.1 Extend game-screen HTML structure in app.html
    - Replace loading-message placeholder with dynamic content container
    - Create question-display div with semantic structure
    - Follow existing card-based layout pattern from login-form
  - [x] 1.2 Build question text display element
    - Add h2 element for question text with id="question-text"
    - Apply semantic heading hierarchy
    - Include appropriate text spacing classes
  - [x] 1.3 Create answer options button structure
    - Build four button elements with ids: answer-a, answer-b, answer-c, answer-d
    - Add data-answer attributes ("A", "B", "C", "D") for selection tracking
    - Use button type="button" to prevent form submission
    - Apply answer-btn class for styling
    - Structure buttons in vertical arrangement container
  - [x] 1.4 Add environment message display
    - Create paragraph element with id="environment-message"
    - Position below answer buttons following spec layout
    - Add appropriate text styling classes
  - [x] 1.5 Implement submit button
    - Add button with id="submit-answer" and class="btn btn-primary"
    - Set type="button" and disabled attribute by default
    - Position at bottom of question card
  - [x] 1.6 Build result display structure
    - Create result-display div with id="result-display" (initially hidden)
    - Add result-message element for "Correct!"/"Incorrect" feedback
    - Add correct-answer-text element for displaying correct answer
    - Include original question text display area
    - Apply result-correct and result-incorrect CSS classes for styling
  - [x] 1.7 Create loading spinner element
    - Add spinner div with id="question-loading" (initially hidden)
    - Follow existing loading-message pattern from current game-screen
    - Position centrally in game-content area
  - [x] 1.8 Add error display structure
    - Create error-container div with id="question-error" (initially hidden)
    - Add error-message text element with role="alert" and aria-live="polite"
    - Include retry button with id="retry-question" and class="btn btn-primary"
    - Follow existing error-message pattern from login form

**Acceptance Criteria:**
- HTML structure follows semantic markup standards
- All required ids and classes are properly assigned
- Layout matches existing card-based pattern from login screen
- Structure supports all display modes: loading, question, result, error
- Accessibility attributes (role, aria-live) are present

### TypeScript Implementation Layer

#### Task Group 2: API Integration and State Management
**Dependencies:** Task Group 1

- [x] 2.0 Complete TypeScript implementation for question handling
  - [x] 2.1 Write 2-8 focused tests for question API integration
    - Limit to 2-8 highly focused tests maximum
    - Test only critical behaviors: fetch question success, submit answer success, error handling, state transitions
    - Skip exhaustive coverage of edge cases and all error scenarios
    - Use manual browser testing or simple test utilities (no complex test framework setup needed for frontend)
  - [x] 2.2 Extend VegasTriviaApp class with question-related properties
    - Add private questionData property for TriviaQuestion storage
    - Add private selectedAnswer property for tracking selected option ("A", "B", "C", "D", or null)
    - Add private currentQuestionId property for submission tracking
    - Add DOM element references: questionText, answerButtons, environmentMessage, submitButton, resultDisplay, loadingSpinner, errorContainer
    - Initialize all new DOM elements in constructor using getElementById with type assertions
  - [x] 2.3 Implement question fetch method
    - Create private async fetchQuestion() method
    - Show loading spinner and hide other content during fetch
    - Call GET /api/questions/random using fetch API
    - Parse TriviaQuestion JSON response with fields: questionText, option1, option2, option3, option4, environment
    - Store question data in component state
    - Handle HTTP errors (check response.ok, log to console)
    - Call displayQuestion() on success
    - Call showError() on failure
    - Use async/await pattern for clean code
  - [x] 2.4 Build question display method
    - Create private displayQuestion() method
    - Hide loading spinner and error display
    - Show question display container
    - Populate question text element with questionText
    - Populate answer buttons with option1-option4 text
    - Set button labels to "A: [option1]", "B: [option2]", etc.
    - Display environment message
    - Reset answer selection state (selectedAnswer = null)
    - Disable submit button
    - Remove selected class from all answer buttons
  - [x] 2.5 Implement answer selection handlers
    - Add click event listeners to all four answer buttons
    - Create private handleAnswerSelection(answer: string) method
    - Update selectedAnswer state when button clicked
    - Remove selected class from all buttons
    - Add selected class to clicked button
    - Enable submit button when answer selected
    - Support changing selection before submission
  - [x] 2.6 Create answer submission method
    - Add submit button click event listener
    - Create private async submitAnswer() method
    - Disable all answer buttons and submit button during submission
    - Show loading spinner
    - Retrieve username from StorageManager.getUsername()
    - Construct AnswerSubmission JSON: {username, questionId, selectedAnswer}
    - POST to /api/answers with Content-Type: application/json
    - Parse AnswerResponse JSON: {isCorrect, correctAnswer, originalQuestion, nextQuestion}
    - Store originalQuestion.id as currentQuestionId for next submission
    - Call displayResult() with response data on success
    - Call showError() on failure
    - Use async/await pattern
  - [x] 2.7 Implement result display method
    - Create private displayResult(response: AnswerResponse) method
    - Hide question display container
    - Show result display container
    - Display "Correct!" in success color if isCorrect is true
    - Display "Incorrect" in error color if isCorrect is false
    - Show original question text from response.originalQuestion.questionText
    - Display "Correct answer: [correctAnswer]" using response.correctAnswer
    - Apply result-correct or result-incorrect CSS class based on isCorrect
    - Use setTimeout with 2500ms delay before calling loadNextQuestion()
  - [x] 2.8 Build automatic progression method
    - Create private loadNextQuestion(nextQuestion: TriviaQuestion) method
    - Check if nextQuestion exists in AnswerResponse
    - If nextQuestion exists, store it and call displayQuestion()
    - If nextQuestion is null, fetch new question via fetchQuestion()
    - Reset all state: selectedAnswer, disable submit button, clear selections
    - Continue game loop indefinitely
  - [x] 2.9 Implement error handling methods
    - Create private showError(message: string) method
    - Hide loading, question, and result displays
    - Show error container with user-friendly message
    - Log detailed error to browser console
    - Add retry button click handler that calls fetchQuestion()
    - Handle network errors, HTTP 4xx/5xx, and timeout scenarios
  - [x] 2.10 Initialize question flow on game screen load
    - Modify showGameScreen() method to call fetchQuestion()
    - Ensure question fetch happens automatically when game screen appears
    - Follow init() pattern from existing VegasTriviaApp
  - [x] 2.11 Ensure TypeScript implementation tests pass
    - Run ONLY the 2-8 tests written in 2.1
    - Verify critical API integration behaviors work
    - Test in browser with manual verification if needed
    - Do NOT run entire application test suite at this stage

**Acceptance Criteria:**
- The 2-8 tests written in 2.1 pass
- Question fetching works and displays correctly
- Answer selection provides visual feedback
- Answer submission validates correctly
- Results display with proper success/error messaging
- Automatic progression to next question works
- Error handling provides retry capability
- All state transitions work smoothly

### CSS Styling Layer

#### Task Group 3: Visual Design and Interactions
**Dependencies:** Task Groups 1-2

- [x] 3.0 Complete CSS styling for question display and interactions
  - [x] 3.1 Write 2-8 focused visual tests
    - Limit to 2-8 highly focused visual checks maximum
    - Test only critical styles: answer button appearance, selected state, result colors, responsive layout
    - Skip exhaustive testing of all states and breakpoints
    - Use manual browser testing across 2-3 viewports (mobile, tablet, desktop)
  - [x] 3.2 Style answer button base appearance
    - Create .answer-btn class in styles.css
    - Apply full width layout with consistent padding (var(--spacing-sm))
    - Set background to var(--color-background) with border: 2px solid var(--color-border)
    - Use border-radius: var(--border-radius)
    - Apply text-align: left for answer text alignment
    - Add font-size: 1rem and font-weight: 500
    - Include transition: var(--transition) for smooth state changes
    - Add cursor: pointer
    - Set margin-bottom: var(--spacing-sm) for vertical spacing
  - [x] 3.3 Implement answer button interaction states
    - Add .answer-btn:hover styles with border-color: var(--color-primary)
    - Create .answer-btn.selected class for selected state
    - Apply border-color: var(--color-primary) and background-color: rgba(238, 0, 0, 0.05)
    - Add font-weight: 600 for emphasis on selected answer
    - Include :focus-visible styles following existing pattern
    - Add :disabled state with opacity: 0.6 and cursor: not-allowed
  - [x] 3.4 Style question text and environment message
    - Create #question-text styles with font-size: 1.25rem and font-weight: 600
    - Apply margin-bottom: var(--spacing-lg)
    - Add color: var(--color-text)
    - Style #environment-message with font-size: 0.875rem
    - Apply color: var(--color-text-light) and font-style: italic
    - Add margin-top: var(--spacing-md) and margin-bottom: var(--spacing-md)
  - [x] 3.5 Create result display styles
    - Style #result-display container with text-align: center
    - Create .result-correct class with color: var(--color-success)
    - Create .result-incorrect class with color: var(--color-error)
    - Add .result-message styles with font-size: 1.5rem and font-weight: 700
    - Style .correct-answer-text with margin-top: var(--spacing-md)
    - Apply fade-in animation using existing @keyframes fadeIn
  - [x] 3.6 Style loading spinner
    - Create #question-loading styles following existing .loading-message pattern
    - Center align with text-align: center
    - Apply font-size: 1.25rem
    - Use color: var(--color-text-light) and font-style: italic
  - [x] 3.7 Implement error display styles
    - Style #question-error container following existing .error-message pattern
    - Apply background: var(--color-error-bg) with padding: var(--spacing-md)
    - Use border-left: 4px solid var(--color-error)
    - Set border-radius: var(--border-radius)
    - Add margin-bottom: var(--spacing-md)
    - Style retry button with standard .btn .btn-primary classes
  - [x] 3.8 Add responsive design adjustments
    - Add @media (max-width: 768px) for tablet breakpoint
    - Reduce #question-text to font-size: 1.125rem on mobile
    - Adjust padding on .game-content to var(--spacing-lg)
    - Ensure answer buttons remain readable on small screens
    - Test layout at 320px, 768px, and 1024px+ widths
  - [x] 3.9 Implement utility classes for visibility
    - Create .hidden class with display: none (already exists, ensure consistency)
    - Verify .visible class usage for error messages
    - Add .fade-in class for smooth transitions (already exists, reuse)
  - [x] 3.10 Ensure visual design tests pass
    - Run ONLY the 2-8 visual checks written in 3.1
    - Verify critical visual states render correctly
    - Test responsive layout at key breakpoints
    - Do NOT perform exhaustive cross-browser testing at this stage

**Acceptance Criteria:**
- The 2-8 visual tests written in 3.1 pass
- Answer buttons have clear visual states (default, hover, selected, disabled)
- Result display uses correct success/error colors
- Layout is responsive across mobile, tablet, and desktop viewports
- Styles follow existing CSS variable system
- All transitions are smooth and consistent
- Accessibility focus indicators are visible

### Integration and Testing

#### Task Group 4: End-to-End Testing and Polish
**Dependencies:** Task Groups 1-3

- [x] 4.0 Complete integration testing and final polish
  - [x] 4.1 Review tests from Task Groups 1-3
    - Review the 2-8 tests from TypeScript implementation (Task 2.1)
    - Review the 2-8 visual tests from CSS styling (Task 3.1)
    - Total existing tests: approximately 4-16 tests
  - [x] 4.2 Analyze test coverage gaps for THIS feature only
    - Identify critical user workflows that lack test coverage
    - Focus ONLY on gaps related to question display and answer submission
    - Prioritize end-to-end workflows over unit test gaps
    - Do NOT assess entire application test coverage
    - Key workflows to verify: complete question cycle, error recovery, continuous game loop
  - [x] 4.3 Write up to 10 additional integration tests maximum
    - Add maximum of 10 new tests to fill identified critical gaps
    - Focus on end-to-end user workflows
    - Test critical integration points: API fetch → display → select → submit → result → next question
    - Test error recovery flow: API failure → error display → retry success
    - Test continuous game loop: multiple question cycles
    - Do NOT write comprehensive coverage for all scenarios
    - Skip performance tests, extensive edge cases, and non-critical error paths
  - [x] 4.4 Manual browser testing checklist
    - Test complete question answering flow in browser
    - Verify correct answer shows success feedback
    - Verify incorrect answer shows error feedback with correct answer
    - Test error handling with network disconnection simulation
    - Test retry functionality after error
    - Verify automatic progression to next question
    - Test answer selection change before submission
    - Verify keyboard navigation works (tab through buttons, enter to select/submit)
    - Test on mobile viewport (320px width)
    - Test on desktop viewport (1024px+ width)
  - [x] 4.5 Run feature-specific tests only
    - Run ONLY tests related to this spec's feature
    - Expected total: approximately 14-26 tests maximum
    - Do NOT run the entire application test suite
    - Verify all critical workflows pass
  - [x] 4.6 Final polish and cleanup
    - Review all console.log statements and ensure proper error logging
    - Verify all accessibility attributes are present and correct
    - Check that all CSS variables are used consistently
    - Ensure no TypeScript compilation errors
    - Validate HTML structure with semantic markup
    - Test with keyboard-only navigation
    - Verify focus indicators are visible

**Acceptance Criteria:**
- All feature-specific tests pass (approximately 14-26 tests total)
- Critical user workflows for this feature are covered
- No more than 10 additional tests added when filling in testing gaps
- Manual browser testing checklist is completed successfully
- Question display and answer submission works end-to-end
- Error handling and retry functionality works correctly
- Automatic progression creates continuous game loop
- Responsive design works across mobile and desktop
- Keyboard navigation and accessibility work properly
- No TypeScript compilation errors
- Code is clean and follows existing patterns

## Execution Order

Recommended implementation sequence:
1. HTML Structure Layer (Task Group 1)
2. TypeScript Implementation Layer (Task Group 2)
3. CSS Styling Layer (Task Group 3)
4. Integration and Testing (Task Group 4)

## Implementation Notes

### Existing Code to Leverage

**app.ts patterns:**
- Class-based architecture with VegasTriviaApp
- DOM element initialization with getElementById and type assertions
- Private methods for event handlers and screen management
- Screen transitions using .hidden class toggling
- StorageManager.getUsername() for retrieving authenticated username
- Existing init() pattern for component initialization

**app.html patterns:**
- Screen management with .screen class and .hidden toggle
- Card-based layout with .container and content areas
- Form-group pattern for input grouping
- Semantic HTML structure with proper accessibility attributes

**styles.css patterns:**
- CSS variable system for colors, spacing, transitions
- .btn and .btn-primary classes for button styling
- .game-content card styling for content containers
- .error-message and .visible patterns for error display
- .fade-in animation class and @keyframes fadeIn
- Responsive design with @media queries
- Accessibility with :focus-visible styles

**API Integration:**
- GET /api/questions/random returns TriviaQuestion: {questionText, option1, option2, option3, option4, environment}
- POST /api/answers expects AnswerSubmission: {username, questionId, selectedAnswer}
- POST /api/answers returns AnswerResponse: {isCorrect, correctAnswer, originalQuestion, nextQuestion}

### Files to Modify

- `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/resources/web/app.html` - Add question display HTML structure
- `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/resources/web/app.ts` - Extend VegasTriviaApp with question logic
- `/Users/jeremyrdavis/Workspace/quarkus-single/quarkus-single/src/main/resources/web/styles.css` - Add question and answer styles

### Technology Context

- Frontend: Vanilla TypeScript with Quarkus Web Bundler (NOT React)
- Build: Quarkus Web Bundler compiles TypeScript and CSS automatically
- Testing: Manual browser testing is primary approach for frontend validation
- No complex test framework setup needed for this frontend-focused feature
