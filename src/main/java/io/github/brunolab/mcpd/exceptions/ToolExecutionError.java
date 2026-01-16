package io.github.brunolab.mcpd.exceptions;

/**
 * Exception raised when a tool execution fails on the server side.
 */
public class ToolExecutionError extends McpdError {

    private String serverName;

    private String toolName;

    private String details;

    public ToolExecutionError(String message) {
        super(message);
    }

    public String getServerName() {
        return serverName;
    }

    public String getToolName() {
        return toolName;
    }

    public String getDetails() {
        return details;
    }

    public ToolExecutionError setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public ToolExecutionError setToolName(String toolName) {
        this.toolName = toolName;
        return this;
    }

    public ToolExecutionError setDetails(String details) {
        this.details = details;
        return this;
    }
}
