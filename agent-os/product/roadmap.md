# Product Roadmap

1. [x] User Authentication with Username — Implement username-based login where users enter a username to access the game, with the username stored in browser memory (localStorage or sessionStorage) to maintain identity across page interactions without backend session management. `S`

2. [x] Trivia Question Data Model and Storage — Create Java domain models for trivia questions with multiple-choice answers and correct answer tracking, implementing in-memory HashMap storage for the question bank with ability to retrieve questions sequentially or randomly. `S`

3. [x] Question Display API — Build RESTful API endpoint to serve trivia questions to the frontend, including question text, multiple-choice options, and question metadata, with JSON serialization and proper CORS configuration for SPA access. `S`

4. [x] Answer Submission and Validation — Implement API endpoint to accept user answer submissions, validate against correct answers, track scoring, and store user responses in memory with HashMap-based storage linking usernames to their answer history. `M`

5. [x] Leaderboard Calculation and API — Create leaderboard ranking system that calculates scores based on correct answers, implements sorting logic, and exposes RESTful endpoint to retrieve current rankings with usernames and scores. `S`

6. [ ] Informational Messaging System — Build randomized message rotation system with three predefined messages about Quarkus/application features, implementing backend logic to randomly select messages and include them in question API responses. `XS`

7. [x] Frontend SPA with Quarkus Web Bundler — Set up Quarkus Web Bundler extension, create single-page application structure in src/main/resources/web, implement login screen with username input form, and configure bundler to serve SPA through Quarkus. `M`

8. [x] Question Display and Answer Submission UI — Build frontend components to display trivia questions with multiple-choice buttons, handle answer selection and submission via API calls, show informational messages alongside questions, and automatically transition to next question after submission. `M`

9. [x] Leaderboard Display UI — Create leaderboard view component that fetches rankings from API, displays usernames and scores in sorted order, and updates in real-time as users submit answers with periodic polling or manual refresh. `S`

10. [ ] Integration Testing and Polish — Perform end-to-end testing of complete user flow from login through question answering to leaderboard viewing, verify message randomization works correctly, ensure data persistence in HashMaps throughout gameplay session, and add visual polish to UI. `M`

> Notes
>
> - Order items by technical dependencies and product architecture
> - Each item should represent an end-to-end (frontend + backend) functional and testable feature
> - Foundation items (data model, storage, authentication) come first
> - API endpoints built before frontend components that consume them
> - Core gameplay loop (questions and answers) established before leaderboard
> - Integration and polish last to ensure complete system works together
