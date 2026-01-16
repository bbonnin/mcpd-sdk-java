package io.github.brunolab.mcpd.exceptions;

/**
 * Exception raised when authentication with the mcpd daemon fails.
 */
public class AuthenticationError extends McpdError {

    public AuthenticationError(String message) {
        super(message);
    }
}
