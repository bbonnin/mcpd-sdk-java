package io.github.brunolab.mcpd.exceptions;

/**
 * Exception raised when unable to connect to the MCPD daemon.
 */
public class ConnectionError extends McpdError {

    public ConnectionError(String message) {
        super(message);
    }
}
