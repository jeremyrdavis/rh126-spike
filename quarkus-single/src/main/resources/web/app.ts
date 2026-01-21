/**
 * Vegas Trivia Application
 * Username-based authentication with session management
 */

import './styles.css';
import { StorageManager } from './utils/storage';
import { UsernameValidator } from './utils/validator';

interface TriviaQuestion {
    questionText: string;
    option1: string;
    option2: string;
    option3: string;
    option4: string;
    environment: string;
}

interface AnswerSubmission {
    username: string;
    questionId: string;
    selectedAnswer: string;
}

interface AnswerResponse {
    isCorrect: boolean;
    correctAnswer: string;
    originalQuestion: {
        id: string;
        questionText: string;
    };
    nextQuestion: TriviaQuestion | null;
}

class VegasTriviaApp {
    private storageManager: StorageManager;
    private validator: UsernameValidator;

    // DOM Elements - Login
    private loginScreen: HTMLElement;
    private welcomeScreen: HTMLElement;
    private gameScreen: HTMLElement;
    private loginForm: HTMLFormElement;
    private usernameInput: HTMLInputElement;
    private startButton: HTMLButtonElement;
    private errorDisplay: HTMLElement;
    private welcomeMessage: HTMLElement;

    // DOM Elements - Question Display
    private questionLoading: HTMLElement;
    private questionError: HTMLElement;
    private errorText: HTMLElement;
    private retryButton: HTMLButtonElement;
    private questionDisplay: HTMLElement;
    private questionText: HTMLElement;
    private answerButtons: HTMLButtonElement[];
    private environmentMessage: HTMLElement;
    private submitButton: HTMLButtonElement;
    private resultDisplay: HTMLElement;
    private resultMessage: HTMLElement;
    private resultQuestion: HTMLElement;
    private resultCorrectAnswer: HTMLElement;

    // State
    private questionData: TriviaQuestion | null = null;
    private selectedAnswer: string | null = null;
    private currentQuestionId: string | null = null;

    constructor() {
        this.storageManager = new StorageManager();
        this.validator = new UsernameValidator();

        // Initialize login DOM elements
        this.loginScreen = document.getElementById('login-screen') as HTMLElement;
        this.welcomeScreen = document.getElementById('welcome-screen') as HTMLElement;
        this.gameScreen = document.getElementById('game-screen') as HTMLElement;
        this.loginForm = document.getElementById('login-form') as HTMLFormElement;
        this.usernameInput = document.getElementById('username-input') as HTMLInputElement;
        this.startButton = document.getElementById('start-button') as HTMLButtonElement;
        this.errorDisplay = document.getElementById('username-error') as HTMLElement;
        this.welcomeMessage = document.getElementById('welcome-message') as HTMLElement;

        // Initialize question DOM elements
        this.questionLoading = document.getElementById('question-loading') as HTMLElement;
        this.questionError = document.getElementById('question-error') as HTMLElement;
        this.errorText = document.getElementById('error-text') as HTMLElement;
        this.retryButton = document.getElementById('retry-question') as HTMLButtonElement;
        this.questionDisplay = document.getElementById('question-display') as HTMLElement;
        this.questionText = document.getElementById('question-text') as HTMLElement;
        this.environmentMessage = document.getElementById('environment-message') as HTMLElement;
        this.submitButton = document.getElementById('submit-answer') as HTMLButtonElement;
        this.resultDisplay = document.getElementById('result-display') as HTMLElement;
        this.resultMessage = document.getElementById('result-message') as HTMLElement;
        this.resultQuestion = document.getElementById('result-question') as HTMLElement;
        this.resultCorrectAnswer = document.getElementById('result-correct-answer') as HTMLElement;

        // Get all answer buttons
        this.answerButtons = [
            document.getElementById('answer-a') as HTMLButtonElement,
            document.getElementById('answer-b') as HTMLButtonElement,
            document.getElementById('answer-c') as HTMLButtonElement,
            document.getElementById('answer-d') as HTMLButtonElement,
        ];

        this.init();
    }

    private init(): void {
        // Check for existing session
        const existingUsername = this.storageManager.getUsername();

        if (existingUsername && this.validator.validate(existingUsername).isValid) {
            // Auto-login for returning users
            this.showGameScreen();
        } else {
            // Show login screen for new users
            this.setupLoginHandlers();
            this.showLoginScreen();
        }

        // Setup question handlers
        this.setupQuestionHandlers();
    }

    private setupLoginHandlers(): void {
        // Input validation on change
        this.usernameInput.addEventListener('input', () => {
            this.handleUsernameChange();
        });

        // Form submission
        this.loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            this.handleLogin();
        });
    }

    private setupQuestionHandlers(): void {
        // Answer button click handlers
        this.answerButtons.forEach((button) => {
            button.addEventListener('click', () => {
                const answer = button.getAttribute('data-answer');
                if (answer) {
                    this.handleAnswerSelection(answer);
                }
            });
        });

        // Submit button handler
        this.submitButton.addEventListener('click', () => {
            this.submitAnswer();
        });

        // Retry button handler
        this.retryButton.addEventListener('click', () => {
            this.fetchQuestion();
        });
    }

    private handleUsernameChange(): void {
        const username = this.usernameInput.value;
        const validationResult = this.validator.validate(username);

        if (username.length === 0) {
            // Empty input - clear error and disable button
            this.clearError();
            this.startButton.disabled = true;
            this.usernameInput.classList.remove('input-error', 'input-success');
        } else if (!validationResult.isValid) {
            // Invalid input - show error and disable button
            this.showError(validationResult.error || 'Invalid username');
            this.startButton.disabled = true;
            this.usernameInput.classList.add('input-error');
            this.usernameInput.classList.remove('input-success');
        } else {
            // Valid input - clear error and enable button
            this.clearError();
            this.startButton.disabled = false;
            this.usernameInput.classList.add('input-success');
            this.usernameInput.classList.remove('input-error');
        }
    }

    private handleLogin(): void {
        const username = this.usernameInput.value.trim();
        const validationResult = this.validator.validate(username);

        if (!validationResult.isValid) {
            this.showError(validationResult.error || 'Invalid username');
            return;
        }

        // Store username in sessionStorage
        this.storageManager.setUsername(username);

        // Show welcome message
        this.showWelcomeScreen(username);

        // Transition to game screen after 2.5 seconds
        setTimeout(() => {
            this.showGameScreen();
        }, 2500);
    }

    private showError(message: string): void {
        this.errorDisplay.textContent = message;
        this.errorDisplay.classList.add('visible');
    }

    private clearError(): void {
        this.errorDisplay.textContent = '';
        this.errorDisplay.classList.remove('visible');
    }

    private showLoginScreen(): void {
        this.loginScreen.classList.remove('hidden');
        this.welcomeScreen.classList.add('hidden');
        this.gameScreen.classList.add('hidden');
        this.usernameInput.focus();
    }

    private showWelcomeScreen(username: string): void {
        this.welcomeMessage.textContent = `Welcome, ${username}! Let's play!`;
        this.loginScreen.classList.add('hidden');
        this.welcomeScreen.classList.remove('hidden');
        this.gameScreen.classList.add('hidden');

        // Add fade-in animation
        this.welcomeScreen.classList.add('fade-in');
    }

    private showGameScreen(): void {
        this.loginScreen.classList.add('hidden');
        this.welcomeScreen.classList.add('hidden');
        this.gameScreen.classList.remove('hidden');

        // Fetch first question when game screen is shown
        this.fetchQuestion();
    }

    private async fetchQuestion(): Promise<void> {
        try {
            // Show loading state
            this.showLoadingState();

            // Fetch question from API
            const response = await fetch('/api/questions/random');

            if (!response.ok) {
                throw new Error(`Failed to fetch question: ${response.status} ${response.statusText}`);
            }

            const question: TriviaQuestion = await response.json();

            // Store question data
            this.questionData = question;

            // Display the question
            this.displayQuestion();
        } catch (error) {
            console.error('Error fetching question:', error);
            this.showQuestionError('Failed to load question. Please try again.');
        }
    }

    private displayQuestion(): void {
        if (!this.questionData) {
            return;
        }

        // Hide loading and error states
        this.questionLoading.classList.add('hidden');
        this.questionError.classList.add('hidden');
        this.resultDisplay.classList.add('hidden');

        // Show question display
        this.questionDisplay.classList.remove('hidden');

        // Populate question text
        this.questionText.textContent = this.questionData.questionText;

        // Populate answer buttons
        const options = [
            this.questionData.option1,
            this.questionData.option2,
            this.questionData.option3,
            this.questionData.option4,
        ];

        this.answerButtons.forEach((button, index) => {
            const answerTextSpan = button.querySelector('.answer-text') as HTMLElement;
            answerTextSpan.textContent = options[index];
        });

        // Display environment message
        this.environmentMessage.textContent = this.questionData.environment;

        // Reset state
        this.selectedAnswer = null;
        this.submitButton.disabled = true;

        // Clear selected class from all buttons
        this.answerButtons.forEach((button) => {
            button.classList.remove('selected');
            button.disabled = false;
        });
    }

    private handleAnswerSelection(answer: string): void {
        // Update selected answer
        this.selectedAnswer = answer;

        // Remove selected class from all buttons
        this.answerButtons.forEach((button) => {
            button.classList.remove('selected');
        });

        // Add selected class to clicked button
        const selectedButton = this.answerButtons.find(
            (button) => button.getAttribute('data-answer') === answer
        );
        if (selectedButton) {
            selectedButton.classList.add('selected');
        }

        // Enable submit button
        this.submitButton.disabled = false;
    }

    private async submitAnswer(): Promise<void> {
        if (!this.selectedAnswer || !this.currentQuestionId) {
            // If no question ID yet (first question), we need to handle this
            // For now, we'll use a placeholder - backend should handle this
            const username = this.storageManager.getUsername();
            if (!username) {
                this.showQuestionError('Please log in again.');
                return;
            }
        }

        try {
            // Disable all interactions during submission
            this.submitButton.disabled = true;
            this.answerButtons.forEach((button) => {
                button.disabled = true;
            });

            // Show loading
            this.showLoadingState();

            const username = this.storageManager.getUsername();
            if (!username) {
                throw new Error('Username not found in session');
            }

            // Prepare submission data
            const submission: AnswerSubmission = {
                username: username,
                questionId: this.currentQuestionId || 'initial',
                selectedAnswer: this.selectedAnswer!,
            };

            // Submit answer to API
            const response = await fetch('/api/answers', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(submission),
            });

            if (!response.ok) {
                throw new Error(`Failed to submit answer: ${response.status} ${response.statusText}`);
            }

            const result: AnswerResponse = await response.json();

            // Store question ID for next submission
            this.currentQuestionId = result.originalQuestion.id;

            // Display result
            this.displayResult(result);
        } catch (error) {
            console.error('Error submitting answer:', error);
            this.showQuestionError('Failed to submit answer. Please try again.');
        }
    }

    private displayResult(response: AnswerResponse): void {
        // Hide loading and question display
        this.questionLoading.classList.add('hidden');
        this.questionDisplay.classList.add('hidden');

        // Show result display
        this.resultDisplay.classList.remove('hidden');

        // Display result message
        if (response.isCorrect) {
            this.resultMessage.textContent = 'Correct!';
            this.resultMessage.className = 'result-message result-correct';
        } else {
            this.resultMessage.textContent = 'Incorrect';
            this.resultMessage.className = 'result-message result-incorrect';
        }

        // Display original question
        this.resultQuestion.textContent = response.originalQuestion.questionText;

        // Display correct answer
        this.resultCorrectAnswer.textContent = `Correct answer: ${response.correctAnswer}`;

        // Automatically load next question after delay
        setTimeout(() => {
            this.loadNextQuestion(response.nextQuestion);
        }, 2500);
    }

    private loadNextQuestion(nextQuestion: TriviaQuestion | null): void {
        if (nextQuestion) {
            // Use the next question from response
            this.questionData = nextQuestion;
            this.displayQuestion();
        } else {
            // Fetch a new question if none provided
            this.fetchQuestion();
        }
    }

    private showQuestionError(message: string): void {
        // Hide loading, question, and result displays
        this.questionLoading.classList.add('hidden');
        this.questionDisplay.classList.add('hidden');
        this.resultDisplay.classList.add('hidden');

        // Show error container
        this.questionError.classList.remove('hidden');
        this.errorText.textContent = message;
    }

    private showLoadingState(): void {
        // Hide all other displays
        this.questionDisplay.classList.add('hidden');
        this.questionError.classList.add('hidden');
        this.resultDisplay.classList.add('hidden');

        // Show loading
        this.questionLoading.classList.remove('hidden');
    }
}

// Initialize app when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        new VegasTriviaApp();
    });
} else {
    new VegasTriviaApp();
}
