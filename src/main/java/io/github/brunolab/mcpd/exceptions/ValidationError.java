package io.github.brunolab.mcpd.exceptions;

import java.util.List;

/**
 * Exception raised when input validation fails.
 */
public class ValidationError extends McpdError {

    private List<String> validationErrors;

    public ValidationError(String message) {
        super(message);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public ValidationError setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
        return this;
    }
}
