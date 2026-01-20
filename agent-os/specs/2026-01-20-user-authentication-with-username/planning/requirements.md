# Spec Requirements: User Authentication with Username

## Initial Description
User Authentication with Username — Implement username-based login where users enter a username to access the game, with the username stored in browser memory (localStorage or sessionStorage) to maintain identity across page interactions without backend session management.

## Requirements Discussion

### First Round Questions

**Q1:** For the login screen layout, I'm assuming this should be the first page users see when they open the app, with a simple banner text like "Welcome to Red Hat One Las Vegas Trivia" and a username input field with a "Start Playing" button. Is that the right approach, or would you prefer a different welcome message or layout?
**Answer:** First page users see. Banner text: "Welcome to Red Hat One Las Vegas Trivia". Username input field. "Start Playing" button.

**Q2:** For storage, should we use sessionStorage (data clears when browser tab closes) or localStorage (persists across sessions)? I'm thinking sessionStorage makes sense for an event context where each session is independent.
**Answer:** Use sessionStorage (not localStorage).

**Q3:** For username validation, I'm assuming we should allow alphanumeric characters and spaces (so "John Doe" works) with a minimum of 2 characters and max of 30. Should we also validate against offensive language, or keep it simple?
**Answer:** Allow alphanumeric characters and spaces. Example: "John Doe". Min 2 characters, max 30 characters. MUST validate against offensive language.

**Q4:** After a user submits their username successfully, should we store it in sessionStorage and immediately display a personalized message like "Welcome, [username]! Let's play!" before transitioning to the game, or just silently store it and move to the trivia screen?
**Answer:** Store username in sessionStorage. Display message: "Welcome, [username]! Let's play!"

**Q5:** After authentication, should we use an SPA approach where we just show/hide the login screen and display the game screen, or do a full page navigation to a different route?
**Answer:** SPA approach. Show game screen with "Loading..." message.

**Q6:** If a user returns to the app and has a username in sessionStorage, should we automatically log them in and show the game screen, or give them an option like "Continue as [username]" or "Play as Different User"?
**Answer:** Check sessionStorage on app load. If username exists, automatically log them in. No "Continue as" or "Play as Different User" options.

**Q7:** I see the project currently uses Quarkus Web Bundler with JavaScript/TypeScript and TailwindCSS. Should we stick with vanilla JavaScript for this authentication component, or would you prefer to introduce a framework like React or Vue for component organization?
**Answer:** Add React framework to Quarkus Web Bundler setup. Use shadcn/ui component library. Use TailwindCSS for styling. Structure: src/main/resources/web/

**Q8:** Are there any specific things we should explicitly exclude from this feature? For example, should we avoid implementing logout functionality, password fields, email validation, or "Remember me" checkboxes since this is a simple demo?
**Answer:** Exclusions (trivial demo): No password fields. No email validation. No "Remember me" checkboxes. No logout functionality.

### Existing Code to Reference

No similar existing features identified for reference.

### Follow-up Questions

No follow-up questions were needed.

## Visual Assets

### Files Provided:
No visual assets provided.

### Visual Insights:
No visual analysis performed.

## Requirements Summary

### Functional Requirements
- Create a login screen as the first page users encounter
- Display banner text: "Welcome to Red Hat One Las Vegas Trivia"
- Provide username input field accepting alphanumeric characters and spaces
- Validate username length: minimum 2 characters, maximum 30 characters
- Validate username against offensive language
- Provide "Start Playing" button to submit username
- Store username in sessionStorage (not localStorage)
- Display personalized welcome message: "Welcome, [username]! Let's play!"
- Transition to game screen using SPA approach (show/hide components)
- Display "Loading..." message on game screen initially
- Auto-login returning users by checking sessionStorage on app load
- Bypass login screen entirely if valid username exists in sessionStorage

### Reusability Opportunities

No existing components or patterns identified for reuse.

### Scope Boundaries

**In Scope:**
- Simple username-based authentication
- SessionStorage for username persistence
- Login form with validation
- Auto-login for returning users
- SPA-style screen transitions
- Integration of React framework with Quarkus Web Bundler
- shadcn/ui component library setup
- TailwindCSS styling
- Offensive language validation

**Out of Scope:**
- Password fields or password authentication
- Email validation or email fields
- "Remember me" checkbox functionality
- Logout functionality
- Backend session management
- User registration or account creation
- "Continue as" or "Play as Different User" options for returning users

### Technical Considerations

**ARCHITECTURAL CHANGE - Frontend Technology Stack:**
- Introduce React framework to the existing Quarkus Web Bundler setup
- Add shadcn/ui component library for UI components
- Continue using TailwindCSS for styling
- Frontend code location: src/main/resources/web/
- Leverage Quarkus Web Bundler's support for React/TypeScript

**Storage Strategy:**
- Use sessionStorage instead of localStorage
- Data clears when browser tab closes (appropriate for event context)
- No backend storage or session management required

**Validation Requirements:**
- Client-side validation for username format (alphanumeric + spaces)
- Length validation (2-30 characters)
- Offensive language validation (must be implemented)

**User Experience Flow:**
1. App loads → Check sessionStorage for existing username
2. If username exists → Auto-login → Show game screen
3. If no username → Show login screen
4. User enters username → Validate → Store in sessionStorage
5. Show welcome message → Transition to game screen with "Loading..." message
