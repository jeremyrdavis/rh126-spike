/**
 * Username Validator
 * Validates username input with length, character, and offensive language checks
 */

export interface ValidationResult {
    isValid: boolean;
    error?: string;
}

export class UsernameValidator {
    private static readonly MIN_LENGTH = 2;
    private static readonly MAX_LENGTH = 30;
    private static readonly VALID_PATTERN = /^[a-zA-Z0-9\s]+$/;

    // Basic offensive words list - can be extended
    private static readonly OFFENSIVE_WORDS = [
        'badword1', 'badword2', 'offensive',
        // Add more offensive words as needed
        // Note: In production, use a comprehensive library like 'bad-words'
    ];

    /**
     * Validate username against all rules
     */
    validate(username: string): ValidationResult {
        // Trim whitespace for validation
        const trimmed = username.trim();

        // Check length
        if (trimmed.length < UsernameValidator.MIN_LENGTH || trimmed.length > UsernameValidator.MAX_LENGTH) {
            return {
                isValid: false,
                error: 'Username must be between 2 and 30 characters'
            };
        }

        // Check valid characters (alphanumeric + spaces)
        if (!UsernameValidator.VALID_PATTERN.test(trimmed)) {
            return {
                isValid: false,
                error: 'Username can only contain letters, numbers, and spaces'
            };
        }

        // Check for offensive language
        if (this.containsOffensiveLanguage(trimmed)) {
            return {
                isValid: false,
                error: 'Please choose a different username'
            };
        }

        return { isValid: true };
    }

    /**
     * Check if username contains offensive language
     */
    private containsOffensiveLanguage(username: string): boolean {
        const lowerUsername = username.toLowerCase();

        return UsernameValidator.OFFENSIVE_WORDS.some(word =>
            lowerUsername.includes(word.toLowerCase())
        );
    }

    /**
     * Validate individual field for specific error
     */
    validateLength(username: string): boolean {
        const trimmed = username.trim();
        return trimmed.length >= UsernameValidator.MIN_LENGTH &&
               trimmed.length <= UsernameValidator.MAX_LENGTH;
    }

    /**
     * Validate characters only
     */
    validateCharacters(username: string): boolean {
        return UsernameValidator.VALID_PATTERN.test(username.trim());
    }
}
