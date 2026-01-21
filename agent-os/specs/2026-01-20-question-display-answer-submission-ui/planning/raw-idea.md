# Raw Idea: Question Display and Answer Submission UI

## Feature Description

Build frontend components to display trivia questions with multiple-choice buttons, handle answer selection and submission via API calls, show informational messages alongside questions, and automatically transition to next question after submission.

## Roadmap Reference

This is roadmap item 8 from: /Users/jeremyrdavis/Workspace/quarkus-single/agent-os/product/roadmap.md

## Context

This is a frontend-focused feature that consumes the following backend APIs:
- GET /api/questions/random - Retrieves random trivia question with environment message
- POST /api/answers - Submits answer and receives validation result with next question

The frontend uses Vanilla TypeScript with Quarkus Web Bundler (NOT React).
