package io.github.brunolab.mcpd.exceptions;

/**
 * Base exception for all MCPD SDK errors.
 */
public class McpdError extends RuntimeException {

    public McpdError(String message) {
        super(message);
    }
}
