# Specification: User Authentication with Username

## Goal
Implement a simple username-based authentication system that serves as the entry point to the Vegas Trivia game, allowing users to identify themselves without complex authentication flows while maintaining their identity throughout the browser session using sessionStorage.

## User Stories
- As a sales kickoff attendee, I want to quickly enter my name and start playing trivia without creating an account or remembering a password, so I can immediately engage with the game
- As a returning user in the same session, I want to be automatically logged in when I revisit the app so I can resume playing without re-entering my username

## Specific Requirements

**React and shadcn/ui Integration with Quarkus Web Bundler**
- Configure Quarkus Web Bundler to support React framework in the existing setup
- Add shadcn/ui component library as the primary UI component library for consistent, accessible components
- Ensure TailwindCSS continues to work with React components for styling
- Structure frontend code in src/main/resources/web/ following React best practices
- Set up proper TypeScript configuration for type safety with React components
- Configure Web Bundler to handle JSX/TSX compilation via Deno and esbuild

**Login Screen as Application Entry Point**
- Create a full-screen login component that displays before any other application content
- Display banner text: "Welcome to Red Hat One Las Vegas Trivia" prominently at the top
- Include a labeled username input field using shadcn/ui Input component
- Provide a "Start Playing" button using shadcn/ui Button component to submit the username
- Apply TailwindCSS utility classes for responsive layout and spacing
- Ensure the login screen is visually appealing and aligns with Red Hat branding expectations

**Username Input Validation**
- Accept alphanumeric characters (a-z, A-Z, 0-9) and spaces in username field
- Allow multi-word usernames like "John Doe" or "Sarah Smith"
- Enforce minimum length of 2 characters and maximum length of 30 characters
- Implement client-side validation that runs on input change and form submission
- Validate username against offensive language using a client-side word filter library or custom implementation
- Display field-specific error messages below the input field when validation fails
- Disable "Start Playing" button when username is invalid or empty

**SessionStorage Management**
- Store username in sessionStorage (not localStorage) upon successful login
- Use key "vegas-trivia-username" for consistent storage access across components
- Retrieve username from sessionStorage on application initialization
- Clear sessionStorage handling on browser tab/window close automatically
- Do not implement manual logout functionality or storage clearing options

**Welcome Message Display**
- Show personalized message "Welcome, [username]! Let's play!" after successful username submission
- Display welcome message for 2-3 seconds before transitioning to game screen
- Use shadcn/ui Alert or Card component for the welcome message display
- Apply smooth fade-in animation for welcome message appearance

**SPA Screen Transitions**
- Implement show/hide logic for login screen and game screen components
- Use React state to manage which screen is currently visible
- Transition from login screen to game screen after welcome message timeout
- Display "Loading..." message on game screen during initial render
- Do not use full page navigation or route changes for screen transitions

**Auto-Login for Returning Users**
- Check sessionStorage for existing username immediately on app initialization
- If valid username exists in sessionStorage, bypass login screen entirely
- Automatically show game screen for users with existing session
- Do not display "Continue as [username]" or "Play as Different User" options

**Error Handling and User Feedback**
- Display clear validation errors for username format violations
- Show specific error message for offensive language detection: "Please choose a different username"
- Show specific error message for length violations: "Username must be between 2 and 30 characters"
- Show specific error message for invalid characters: "Username can only contain letters, numbers, and spaces"
- Use shadcn/ui Form components with built-in error display patterns

## Out of Scope
- Password fields or password-based authentication
- Email input fields or email validation
- "Remember me" checkbox functionality
- Manual logout button or logout functionality
- Backend session management or server-side user tracking
- User registration flows or account creation
- Profile editing or username change after initial login
- "Continue as" or "Play as Different User" options for returning users
- OAuth2, OIDC, or third-party authentication providers
- Multi-factor authentication or security questions
