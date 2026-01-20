# Tech Stack

## Backend

### Framework
- **Quarkus** - Cloud-native Java framework optimized for containers and serverless environments
  - Version: Latest stable (to be specified in pom.xml/build.gradle)
  - Provides REST endpoint capabilities, dependency injection, and development mode with live reload

### Language
- **Java** - Primary backend programming language
  - Version: Java 17+ (recommended for Quarkus)

### REST API
- **Quarkus RESTEasy Reactive** (or RESTEasy Classic) - For building RESTful web services
  - Handles HTTP endpoints for questions, answers, and leaderboard
  - JSON serialization/deserialization with Jackson

### Data Storage
- **In-Memory HashMap** - Simple, lightweight storage for MVP iteration
  - Question bank storage (questions and answers)
  - User answer tracking (username to responses mapping)
  - Leaderboard data (username to score mapping)
  - No external database required for first iteration
  - Data persists only during application runtime

## Frontend

### Bundler/Build Tool
- **Quarkus Web Bundler** (`io.quarkiverse.web-bundler:quarkus-web-bundler`)
  - Extension for creating full-stack web applications with integrated bundling
  - Uses Java native binding of Deno and esbuild for asset processing
  - Handles JavaScript, TypeScript, CSS, and SCSS compilation
  - Provides browser live-reload during development
  - No manual Node.js installation required

### Application Architecture
- **Single Page Application (SPA)** - Client-side rendered web application
  - Served directly by Quarkus backend
  - Assets located in `src/main/resources/web/`
  - Bundled and optimized by Quarkus Web Bundler

### Frontend Language
- **JavaScript** or **TypeScript** - For SPA implementation
  - Choice between vanilla JS/TS or lightweight framework (to be determined during implementation)
  - Access to NPM package catalog via Maven/Gradle dependencies

### Styling
- **CSS** or **SCSS** - For application styling
  - Option to use TailwindCSS 4+ (pre-configured in Web Bundler)
  - Bundler handles minification and optimization

### Client-Side Storage
- **Browser Storage API** - For username persistence
  - `localStorage` or `sessionStorage` for maintaining username across page interactions
  - No backend session management required

## Development Tools

### Build Tool
- **Maven** or **Gradle** - Java build automation (based on project initialization)
  - Manages Quarkus dependencies and extensions
  - Handles Web Bundler extension

### Development Mode
- **Quarkus Dev Mode** - Hot reload development environment
  - Live reload for both backend and frontend changes
  - Continuous testing capabilities

## Deployment

### Target Environment
- **Demonstration/Event Environment** - For Red Hat 2026 sales kickoff
  - Can be deployed to containers (Docker/Podman)
  - Can run as standalone JAR
  - Suitable for cloud platforms (OpenShift, Kubernetes)
  - Optimized for fast startup and low memory footprint

## Future Considerations

### Potential Enhancements (Post-MVP)
- **Database Integration** - PostgreSQL, MySQL, or MongoDB for persistent storage
- **Authentication** - OAuth2/OIDC for production user management
- **Caching** - Redis or Infinispan for improved performance
- **Messaging** - Kafka or AMQP for real-time leaderboard updates
- **Monitoring** - Quarkus Micrometer/Prometheus integration for observability

## Notes
- Tech stack optimized for rapid development and demonstration purposes
- In-memory storage acceptable for event context with limited concurrent users
- Architecture demonstrates Quarkus full-stack capabilities while maintaining simplicity
- Can be enhanced with additional Quarkus extensions as requirements evolve
