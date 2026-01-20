/**
 * SessionStorage Manager
 * Handles username storage and retrieval
 */

const STORAGE_KEY = 'vegas-trivia-username';

export class StorageManager {
    /**
     * Store username in sessionStorage
     */
    setUsername(username: string): void {
        try {
            sessionStorage.setItem(STORAGE_KEY, username);
        } catch (error) {
            console.error('Failed to store username:', error);
        }
    }

    /**
     * Retrieve username from sessionStorage
     */
    getUsername(): string | null {
        try {
            return sessionStorage.getItem(STORAGE_KEY);
        } catch (error) {
            console.error('Failed to retrieve username:', error);
            return null;
        }
    }

    /**
     * Clear username from sessionStorage
     */
    clearUsername(): void {
        try {
            sessionStorage.removeItem(STORAGE_KEY);
        } catch (error) {
            console.error('Failed to clear username:', error);
        }
    }

    /**
     * Check if username exists in sessionStorage
     */
    hasUsername(): boolean {
        return this.getUsername() !== null;
    }
}
