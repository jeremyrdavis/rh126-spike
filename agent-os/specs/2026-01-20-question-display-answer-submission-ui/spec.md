# Specification: Question Display and Answer Submission UI

## Goal
Build frontend UI components to display trivia questions with multiple-choice answer buttons, handle answer selection and submission via backend APIs, show result feedback with correct answer information, and automatically transition to the next question for continuous gameplay.

## User Stories
- As a trivia game player, I want to see questions with multiple-choice options so that I can select and submit my answers
- As a trivia game player, I want to see if my answer was correct and learn the right answer so that I can improve while playing
- As a trivia game player, I want automatic progression to the next question so that I can continue playing without interruption

## Specific Requirements

**Question Display Component**
- Display question text prominently at top of game-content card
- Render four answer option buttons labeled "A", "B", "C", "D" in vertical arrangement
- Display environment/informational message below answer options
- Use existing card-based layout pattern from login-form for consistency
- Apply semantic HTML with button elements for answer options
- Follow existing typography and spacing patterns from styles.css

**Answer Selection Interaction**
- Enable single answer selection by clicking answer button (radio button behavior)
- Allow user to change selection before submitting
- Visually emphasize selected answer with bold text or background highlight
- Add CSS class to selected button (e.g., 'selected') for styling
- Disable submit button when no answer selected
- Enable submit button when answer selected
- Only one answer can be selected at a time

**Answer Submission Flow**
- Submit button triggers POST request to /api/answers endpoint
- Include username from sessionStorage via StorageManager.getUsername()
- Include questionId from current TriviaQuestion data
- Include selectedAnswer as letter string ("A", "B", "C", or "D")
- Disable all answer buttons during submission to prevent double-clicks
- Disable submit button during submission
- Show loading spinner during API call
- Use fetch API for HTTP requests following modern JavaScript patterns

**Result Display Screen**
- After successful submission, display result feedback in game-content area
- Show "Correct!" message in success color if isCorrect is true
- Show "Incorrect" message in error color if isCorrect is false
- Keep original question text visible during result display
- Display "Correct answer: [correctAnswer letter and text]" using AnswerResponse data
- Apply CSS styling consistent with success/error color scheme from styles.css
- Display result for 2500ms before transitioning to next question
- Use setTimeout for automatic transition delay

**Automatic Question Progression**
- After result display timeout, fetch next question from AnswerResponse.nextQuestion field
- If nextQuestion exists, display it and reset to question display state
- Continue game loop indefinitely with no end condition
- Clear previous answer selection when loading next question
- Reset submit button to disabled state for next question
- Reset all answer buttons to unselected state

**Loading States**
- Display loading spinner when initially fetching first question via GET /api/questions/random
- Show loading spinner during answer submission
- Disable all interactive elements (answer buttons, submit button) during loading
- Use existing loading-message pattern from game-screen HTML
- Replace loading state with question UI once data loaded successfully

**Error Handling**
- Display user-friendly error message if GET /api/questions/random fails
- Display user-friendly error message if POST /api/answers fails
- Show "Try Again" button when error occurs
- Retry button triggers same API call again
- Log detailed error information to browser console for debugging
- Handle network errors, HTTP 4xx/5xx responses, and timeout scenarios
- Clear error display when retry succeeds

**API Integration - Question Retrieval**
- Call GET /api/questions/random on initial game screen load
- Parse TriviaQuestion JSON response with fields: questionText, option1, option2, option3, option4, environment
- Store current question data in component state for submission
- No questionId field in TriviaQuestion, use from Answer API response if needed

**API Integration - Answer Submission**
- Construct AnswerSubmission JSON with username, questionId, selectedAnswer
- POST to /api/answers with Content-Type application/json
- Parse AnswerResponse JSON with fields: isCorrect, correctAnswer, originalQuestion, nextQuestion
- Use originalQuestion.questionText for result display
- Use nextQuestion data for automatic progression
- Handle case where nextQuestion may be null (display friendly message)

**State Management**
- Track current question data (TriviaQuestion object)
- Track selected answer letter ("A", "B", "C", "D", or null)
- Track loading state (initial load, submitting answer)
- Track error state (error message string or null)
- Track display mode (question, result, loading, error)
- Track questionId from originalQuestion for submission

**CSS Styling Requirements**
- Reuse existing CSS variable system from styles.css (colors, spacing, transitions)
- Apply .btn and .btn-primary classes to submit button
- Create answer button styles following existing button patterns
- Use var(--color-success) for correct answer feedback
- Use var(--color-error) for incorrect answer feedback
- Apply card layout with .game-content background and padding
- Maintain responsive design for mobile viewports
- Follow existing border-radius, shadow, and transition patterns

**Accessibility Requirements**
- Use semantic button elements for all clickable answer options
- Provide aria-label attributes for answer buttons if needed
- Ensure keyboard navigation works (tab through buttons, enter to select/submit)
- Maintain focus visibility with existing :focus-visible styles
- Use role="alert" and aria-live="polite" for error messages
- Ensure color contrast meets WCAG standards using existing color variables
- Support screen readers with appropriate ARIA labels

## Visual Design
No visual assets provided for this feature. Implementation follows existing UI patterns from login screen and maintains consistency with established design language.

## Existing Code to Leverage

**app.ts - TypeScript class structure and DOM management**
- Use class-based architecture following VegasTriviaApp pattern
- Follow DOM element initialization pattern with getElementById and type assertions
- Use private methods for event handlers and screen transitions
- Apply existing screen management with .hidden class toggling
- Follow init() pattern for component initialization
- Reuse StorageManager.getUsername() for retrieving authenticated username

**app.html - HTML structure and screen layout**
- Extend game-screen div structure with question display elements
- Follow existing screen class pattern with container and content areas
- Replace loading-message with dynamic question content
- Use form-group pattern for answer selection UI
- Apply semantic HTML structure consistent with login-form

**styles.css - Design system and component styles**
- Reuse CSS variables: --color-primary, --color-error, --color-success, --spacing-*, --border-radius
- Apply .btn and .btn-primary classes for submit button
- Use .game-content card styling for question container
- Follow .screen, .container, and .hidden class patterns
- Reuse .error-message and .visible patterns for error display
- Apply .fade-in animation class for smooth transitions
- Leverage existing @keyframes fadeIn for result transitions

**StorageManager - Session storage utility**
- Call getUsername() to retrieve authenticated user for API calls
- Follow existing try-catch error handling pattern
- Log errors to console following established pattern
- No need for clearUsername() in this feature

**Fetch API patterns**
- Use fetch() for GET /api/questions/random with error handling
- Use fetch() for POST /api/answers with JSON body and headers
- Parse JSON responses with .json() method
- Handle HTTP status codes with response.ok check
- Catch network errors and log to console
- Follow async/await pattern for cleaner code

## Out of Scope
- Question timer or countdown functionality
- Score display on question screen (handled separately by leaderboard feature)
- Ability to skip questions
- Question history or review functionality
- Previous/next question navigation buttons
- Question difficulty indicators or badges
- Hints or help functionality
- Sound effects or complex animations beyond basic CSS transitions
- Pause/resume game functionality
- Question bookmarking or favorites
- Social sharing features
- Answer explanations or detailed feedback
- Multi-language support
- Dark mode or theme switching
