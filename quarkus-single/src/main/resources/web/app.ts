/**
 * Vegas Trivia Application
 * Username-based authentication with session management
 */

import './styles.css';
import { StorageManager } from './utils/storage';
import { UsernameValidator } from './utils/validator';

interface TriviaQuestion {
    id: string;
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

interface Answer {
    id: string;
    text: string;
    isCorrect: boolean;
}

interface Question {
    id: string;
    questionText: string;
    optionalAnswers: Answer[];
}

interface AnswerResponse {
    isCorrect: boolean;
    correctAnswer: string;
    originalQuestion: Question;
    nextQuestion: TriviaQuestion | null;
}

interface LeaderboardEntry {
    username: string;
    score: number;
    rank: number;
    questionsAnsweredCorrectly: number;
}

// Leaderboard auto-refresh interval in milliseconds
const LEADERBOARD_REFRESH_INTERVAL = 60000;

class VegasTriviaApp {
    private storageManager: StorageManager;
    private validator: UsernameValidator;

    // DOM Elements - Login
    private loginScreen: HTMLElement;
    private welcomeScreen: HTMLElement;
    private gameScreen: HTMLElement;
    private leaderboardScreen: HTMLElement;
    private loginForm: HTMLFormElement;
    private usernameInput: HTMLInputElement;
    private startButton: HTMLButtonElement;
    private errorDisplay: HTMLElement;
    private welcomeMessage: HTMLElement;

    // DOM Elements - Navigation
    private viewLeaderboardButton: HTMLButtonElement;
    private backToGameButton: HTMLButtonElement;

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

    // DOM Elements - Leaderboard
    private leaderboardLoading: HTMLElement;
    private leaderboardError: HTMLElement;
    private leaderboardErrorText: HTMLElement;
    private retryLeaderboardButton: HTMLButtonElement;
    private leaderboardList: HTMLElement;

    // State
    private questionData: TriviaQuestion | null = null;
    private selectedAnswer: string | null = null;
    private leaderboardData: LeaderboardEntry[] = [];
    private leaderboardRefreshTimer: number | null = null;

    constructor() {
        this.storageManager = new StorageManager();
        this.validator = new UsernameValidator();

        // Initialize login DOM elements
        this.loginScreen = document.getElementById('login-screen') as HTMLElement;
        this.welcomeScreen = document.getElementById('welcome-screen') as HTMLElement;
        this.gameScreen = document.getElementById('game-screen') as HTMLElement;
        this.leaderboardScreen = document.getElementById('leaderboard-screen') as HTMLElement;
        this.loginForm = document.getElementById('login-form') as HTMLFormElement;
        this.usernameInput = document.getElementById('username-input') as HTMLInputElement;
        this.startButton = document.getElementById('start-button') as HTMLButtonElement;
        this.errorDisplay = document.getElementById('username-error') as HTMLElement;
        this.welcomeMessage = document.getElementById('welcome-message') as HTMLElement;

        // Initialize navigation DOM elements
        this.viewLeaderboardButton = document.getElementById('view-leaderboard') as HTMLButtonElement;
        this.backToGameButton = document.getElementById('back-to-game') as HTMLButtonElement;

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

        // Initialize leaderboard DOM elements
        this.leaderboardLoading = document.getElementById('leaderboard-loading') as HTMLElement;
        this.leaderboardError = document.getElementById('leaderboard-error') as HTMLElement;
        this.leaderboardErrorText = document.getElementById('leaderboard-error-text') as HTMLElement;
        this.retryLeaderboardButton = document.getElementById('retry-leaderboard') as HTMLButtonElement;
        this.leaderboardList = document.getElementById('leaderboard-list') as HTMLElement;

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

        // Setup navigation handlers
        this.setupNavigationHandlers();
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

    private setupNavigationHandlers(): void {
        // View leaderboard button handler
        this.viewLeaderboardButton.addEventListener('click', () => {
            this.showLeaderboardScreen();
        });

        // Back to game button handler
        this.backToGameButton.addEventListener('click', () => {
            this.showGameScreen();
        });

        // Retry leaderboard button handler
        this.retryLeaderboardButton.addEventListener('click', () => {
            this.fetchLeaderboard();
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
        this.leaderboardScreen.classList.add('hidden');
        this.usernameInput.focus();
        this.clearLeaderboardTimer();
    }

    private showWelcomeScreen(username: string): void {
        this.welcomeMessage.textContent = `Welcome, ${username}! Let's play!`;
        this.loginScreen.classList.add('hidden');
        this.welcomeScreen.classList.remove('hidden');
        this.gameScreen.classList.add('hidden');
        this.leaderboardScreen.classList.add('hidden');

        // Add fade-in animation
        this.welcomeScreen.classList.add('fade-in');
        this.clearLeaderboardTimer();
    }

    private showGameScreen(): void {
        this.loginScreen.classList.add('hidden');
        this.welcomeScreen.classList.add('hidden');
        this.gameScreen.classList.remove('hidden');
        this.leaderboardScreen.classList.add('hidden');

        // Clear leaderboard timer when navigating away from leaderboard
        this.clearLeaderboardTimer();

        // Fetch first question when game screen is shown
        this.fetchQuestion();
    }

    private showLeaderboardScreen(): void {
        this.loginScreen.classList.add('hidden');
        this.welcomeScreen.classList.add('hidden');
        this.gameScreen.classList.add('hidden');
        this.leaderboardScreen.classList.remove('hidden');

        // Fetch leaderboard data when screen is shown
        this.fetchLeaderboard();

        // Start auto-refresh timer
        this.startLeaderboardAutoRefresh();
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

    private async fetchLeaderboard(): Promise<void> {
        try {
            // Show loading state
            this.showLeaderboardLoadingState();

            // Fetch leaderboard from API
            const response = await fetch('/api/leaderboard');

            if (!response.ok) {
                throw new Error(`Failed to fetch leaderboard: ${response.status} ${response.statusText}`);
            }

            const leaderboard: LeaderboardEntry[] = await response.json();

            // Store leaderboard data
            this.leaderboardData = leaderboard;

            // Display the leaderboard
            this.displayLeaderboard();
        } catch (error) {
            console.error('Error fetching leaderboard:', error);
            this.showLeaderboardError('Failed to load leaderboard. Please try again.');
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

    private displayLeaderboard(): void {
        // Hide loading and error states
        this.leaderboardLoading.classList.add('hidden');
        this.leaderboardError.classList.add('hidden');

        // Show leaderboard list
        this.leaderboardList.classList.remove('hidden');

        // Clear existing list items
        this.leaderboardList.innerHTML = '';

        // Render leaderboard entries
        this.leaderboardData.forEach((entry) => {
            const item = document.createElement('div');
            item.className = 'leaderboard-item';

            // Add special class for top 3
            if (entry.rank <= 3) {
                item.classList.add(`rank-${entry.rank}`);
            }

            // Create rank badge
            const rankBadge = document.createElement('div');
            rankBadge.className = `rank-badge rank-badge-${entry.rank}`;

            // Add trophy icon for top 3
            if (entry.rank === 1) {
                rankBadge.innerHTML = '<span class="trophy">&#129351;</span><span class="rank-number">1</span>';
            } else if (entry.rank === 2) {
                rankBadge.innerHTML = '<span class="trophy">&#129352;</span><span class="rank-number">2</span>';
            } else if (entry.rank === 3) {
                rankBadge.innerHTML = '<span class="trophy">&#129353;</span><span class="rank-number">3</span>';
            } else {
                rankBadge.innerHTML = `<span class="rank-number">${entry.rank}</span>`;
            }

            // Create user info container
            const userInfo = document.createElement('div');
            userInfo.className = 'user-info';

            const username = document.createElement('div');
            username.className = 'username';
            username.textContent = entry.username;

            userInfo.appendChild(username);

            // Create stats container
            const stats = document.createElement('div');
            stats.className = 'stats';

            const score = document.createElement('div');
            score.className = 'score';
            score.textContent = `Score: ${entry.score}`;

            const questionsCorrect = document.createElement('div');
            questionsCorrect.className = 'questions-correct';
            questionsCorrect.textContent = `Correct: ${entry.questionsAnsweredCorrectly}`;

            stats.appendChild(score);
            stats.appendChild(questionsCorrect);

            // Append all parts to item
            item.appendChild(rankBadge);
            item.appendChild(userInfo);
            item.appendChild(stats);

            // Add item to list
            this.leaderboardList.appendChild(item);
        });
    }

    private startLeaderboardAutoRefresh(): void {
        // Clear any existing timer
        this.clearLeaderboardTimer();

        // Set up auto-refresh interval
        this.leaderboardRefreshTimer = window.setInterval(() => {
            this.fetchLeaderboard();
        }, LEADERBOARD_REFRESH_INTERVAL);
    }

    private clearLeaderboardTimer(): void {
        if (this.leaderboardRefreshTimer !== null) {
            clearInterval(this.leaderboardRefreshTimer);
            this.leaderboardRefreshTimer = null;
        }
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
        if (!this.selectedAnswer || !this.questionData) {
            return;
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
                questionId: this.questionData!.id,
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

        // Find the correct answer text from optionalAnswers
        const correctAnswerObj = response.originalQuestion.optionalAnswers.find(
            answer => answer.isCorrect
        );
        const correctAnswerText = correctAnswerObj ? correctAnswerObj.text : 'Unknown';

        // Display correct answer with letter and text
        this.resultCorrectAnswer.textContent = `Correct answer: ${response.correctAnswer} - ${correctAnswerText}`;

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

    private showLeaderboardError(message: string): void {
        // Hide loading and leaderboard list
        this.leaderboardLoading.classList.add('hidden');
        this.leaderboardList.classList.add('hidden');

        // Show error container
        this.leaderboardError.classList.remove('hidden');
        this.leaderboardErrorText.textContent = message;
    }

    private showLoadingState(): void {
        // Hide all other displays
        this.questionDisplay.classList.add('hidden');
        this.questionError.classList.add('hidden');
        this.resultDisplay.classList.add('hidden');

        // Show loading
        this.questionLoading.classList.remove('hidden');
    }

    private showLeaderboardLoadingState(): void {
        // Hide error and list
        this.leaderboardError.classList.add('hidden');
        this.leaderboardList.classList.add('hidden');

        // Show loading
        this.leaderboardLoading.classList.remove('hidden');
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
