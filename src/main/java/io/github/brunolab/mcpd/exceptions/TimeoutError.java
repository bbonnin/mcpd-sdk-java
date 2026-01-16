package io.github.brunolab.mcpd.exceptions;

/**
 * Exception raised when an operation times out.
 */
public class TimeoutError extends McpdError {

    private String operation;

    private long timeout;

    public TimeoutError(String message) {
        super(message);
    }

    public String getOperation() {
        return operation;
    }

    public long getTimeout() {
        return timeout;
    }

    public TimeoutError setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    public TimeoutError setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }
}
