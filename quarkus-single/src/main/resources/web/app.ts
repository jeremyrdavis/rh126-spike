/**
 * Vegas Trivia Application
 * Username-based authentication with session management
 */

import './styles.css';
import { StorageManager } from './utils/storage';
import { UsernameValidator } from './utils/validator';

class VegasTriviaApp {
    private storageManager: StorageManager;
    private validator: UsernameValidator;

    // DOM Elements
    private loginScreen: HTMLElement;
    private welcomeScreen: HTMLElement;
    private gameScreen: HTMLElement;
    private loginForm: HTMLFormElement;
    private usernameInput: HTMLInputElement;
    private startButton: HTMLButtonElement;
    private errorDisplay: HTMLElement;
    private welcomeMessage: HTMLElement;

    constructor() {
        this.storageManager = new StorageManager();
        this.validator = new UsernameValidator();

        // Initialize DOM elements
        this.loginScreen = document.getElementById('login-screen') as HTMLElement;
        this.welcomeScreen = document.getElementById('welcome-screen') as HTMLElement;
        this.gameScreen = document.getElementById('game-screen') as HTMLElement;
        this.loginForm = document.getElementById('login-form') as HTMLFormElement;
        this.usernameInput = document.getElementById('username-input') as HTMLInputElement;
        this.startButton = document.getElementById('start-button') as HTMLButtonElement;
        this.errorDisplay = document.getElementById('username-error') as HTMLElement;
        this.welcomeMessage = document.getElementById('welcome-message') as HTMLElement;

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
