# Spec Requirements: Question Display and Answer Submission UI

## Initial Description

Build frontend components to display trivia questions with multiple-choice buttons, handle answer selection and submission via API calls, show informational messages alongside questions, and automatically transition to next question after submission.

**Roadmap Reference:** This is roadmap item 8 from the product roadmap.

**Context:**
- Frontend uses Vanilla TypeScript with Quarkus Web Bundler (NOT React)
- Backend APIs available:
  - GET /api/questions/random - Retrieves random trivia question with environment message
  - POST /api/answers - Submits answer and receives validation result with next question
- User authentication already exists (username stored in sessionStorage)
- Current issue: Game screen shows only "Loading..." - this feature will fix it by implementing the actual question display and answer submission functionality

## Requirements Discussion

### First Round Questions

**Q1: Layout and Structure** - I assume the question should be displayed at the top, followed by four answer buttons arranged vertically (labeled A, B, C, D), with the informational message below the answers, and a submit button at the bottom. Is that correct, or would you prefer a different layout (e.g., two-column grid for answers, message at top)?

**Answer:** Question text at top, four labeled answer buttons (A, B, C, D) vertical, environment message below, submit button at bottom.

**Q2: Answer Selection Behavior** - I'm thinking users should be able to click an answer button to select it, and can change their selection before submitting (only one answer selected at a time). Should we bold or otherwise emphasize the currently selected answer?

**Answer:** Users can change selection. Bold/emphasize selected answer.

**Q3: Result Display After Submission** - When the user submits an answer, I assume we should show whether they were correct or incorrect, then display the next question after a brief delay (2-3 seconds). Should we show any additional information like "The correct answer was: [answer text]" if they were wrong?

**Answer:** Show "Correct!" or "Incorrect", original question, "Correct answer: [correct answer]" using AnswerResponse data.

**Q4: Loading States** - I'm thinking we should show a loading spinner while fetching the initial question and while submitting an answer, and disable the answer buttons during submission to prevent double-clicks. Is that approach sufficient?

**Answer:** Show spinner, disable buttons during submission.

**Q5: Error Handling** - If the API call fails (network error, server error), I assume we should display a user-friendly error message and provide a "Try Again" button to retry the request. Should we also log the error details to the browser console for debugging?

**Answer:** User-friendly error + "Try Again" button, log to console.

**Q6: Styling Approach** - I'm planning to follow the existing CSS patterns in the application and use a card-based layout similar to the login screen. Should we reuse the existing button styles and color scheme from other parts of the app?

**Answer:** Follow existing CSS patterns, card-based layout, reuse button styles.

**Q7: Game Flow and Transitions** - After displaying the answer result, I assume we should automatically load and display the next question to create a continuous game loop. Should there be any end condition (e.g., after 10 questions), or should the game continue indefinitely until the user navigates away?

**Answer:** Continuous loop, no end condition.

**Q8: Features to Exclude** - Are there any features we should explicitly NOT include in this iteration? For example: question timer, score display on the question screen, ability to skip questions, question history/review, etc.

**Answer:** No timer, no scoring display on question screen, no skip questions.

### Existing Code to Reference

**Similar Features Identified:**
- Feature: Login screen - Path: `vegas-trivia/src/main/resources/web/`
- Components to potentially reuse: Card layout, button styles, form handling patterns, loading states
- Frontend patterns to reference: API call patterns, sessionStorage usage for username, error handling approach

### Follow-up Questions

No follow-up questions were needed. The user provided comprehensive answers covering all aspects of the feature.

## Visual Assets

### Files Provided:
No visual assets provided.

### Visual Insights:
No visual assets to analyze. Implementation will follow existing UI patterns from the login screen and maintain consistency with the application's established design language.

## Requirements Summary

### Functional Requirements

**Core Display Functionality:**
- Display trivia question text prominently at the top of the screen
- Render four multiple-choice answer options as labeled buttons (A, B, C, D) in vertical arrangement
- Display informational/environment message below the answer options
- Provide submit button at the bottom to submit the selected answer
- Show question ID or metadata if needed for tracking

**Answer Selection:**
- Allow user to select one answer by clicking a button
- Allow user to change their selection before submitting
- Visually emphasize (bold or highlight) the currently selected answer
- Only one answer can be selected at a time (radio button behavior)
- Submit button should only be active when an answer is selected

**Answer Submission:**
- POST selected answer to /api/answers endpoint with username and answer data
- Include username from sessionStorage in the submission
- Disable all answer buttons and submit button during submission to prevent double-submission
- Show loading spinner during submission

**Result Display:**
- After submission, display feedback message: "Correct!" or "Incorrect"
- Keep the original question visible
- Display "Correct answer: [answer text]" regardless of whether user was correct or incorrect
- Use data from AnswerResponse API response for all result information
- Show result for 2-3 seconds before transitioning

**Automatic Question Progression:**
- After displaying result, automatically fetch and display the next question
- Use GET /api/questions/random endpoint to retrieve next question
- Continue game loop indefinitely (no end condition)
- Each cycle: display question → user selects answer → submit → show result → fetch next question

**Loading States:**
- Display loading spinner when initially fetching the first question
- Display loading spinner during answer submission
- Disable interaction with answer buttons while loading
- Prevent user actions during loading states

**Error Handling:**
- Display user-friendly error message if question fetch fails
- Display user-friendly error message if answer submission fails
- Provide "Try Again" button to retry the failed operation
- Log detailed error information to browser console for debugging
- Gracefully handle network errors, server errors, and timeout scenarios

**Data Integration:**
- Retrieve username from sessionStorage (set during login)
- Parse question data from GET /api/questions/random response
- Parse answer validation and next question data from POST /api/answers response
- Display environment/informational message included in question response

### Reusability Opportunities

**UI Components:**
- Reuse card-based layout component from login screen
- Reuse button styles and color scheme from existing UI
- Follow established form handling patterns from login implementation

**API Patterns:**
- Follow existing API call patterns used in login feature
- Reuse error handling approach from existing features
- Apply same sessionStorage usage pattern for username retrieval

**Frontend Architecture:**
- Maintain consistency with existing Vanilla TypeScript structure
- Follow established file organization in `src/main/resources/web/`
- Reuse loading state patterns if already established

### Scope Boundaries

**In Scope:**
- Question display with multiple-choice options
- Answer selection with visual feedback
- Answer submission to backend API
- Result display showing correct/incorrect feedback
- Automatic progression to next question
- Loading states during API calls
- Error handling with retry capability
- Environment message display
- Continuous game loop with no end condition

**Out of Scope:**
- Question timer or countdown functionality
- Score display on question screen (leaderboard feature handles this separately)
- Ability to skip questions
- Question history or review functionality
- Previous/next question navigation
- Question difficulty indicators
- Hints or help functionality
- Sound effects or animations (beyond basic transitions)
- Pause/resume game functionality

### Technical Considerations

**Frontend Technology:**
- Vanilla TypeScript (NOT React or other framework)
- Quarkus Web Bundler for asset compilation
- Files located in `src/main/resources/web/`

**API Endpoints:**
- GET /api/questions/random - Returns question object with text, options, ID, and environment message
- POST /api/answers - Accepts username and answer data, returns AnswerResponse with validation result

**Browser Storage:**
- Username retrieved from sessionStorage (set during login)
- No additional browser storage requirements for this feature

**Styling Approach:**
- Follow existing CSS patterns and methodology
- Card-based layout consistent with login screen
- Reuse button styles from existing components
- Maintain design system consistency (colors, spacing, typography)

**Accessibility:**
- Use semantic HTML elements (button elements for answers)
- Ensure keyboard navigation works properly
- Provide visible focus indicators
- Maintain sufficient color contrast
- Use appropriate ARIA labels if needed for screen readers

**Error Scenarios to Handle:**
- Network failure during question fetch
- Network failure during answer submission
- API server errors (4xx, 5xx responses)
- Timeout scenarios
- Missing username in sessionStorage
- Malformed API responses

**State Management:**
- Track current question data
- Track selected answer
- Track loading states (initial load, submitting answer)
- Track error states
- Track result display state

**Performance:**
- Minimize re-renders during answer selection
- Efficient DOM manipulation with Vanilla TypeScript
- Quick transitions between questions for smooth UX

**Integration Points:**
- Assumes user is already authenticated (username in sessionStorage)
- Integrates with existing backend APIs
- Works within Quarkus Web Bundler asset pipeline
- Coordinates with leaderboard feature (separate component will handle score display)
