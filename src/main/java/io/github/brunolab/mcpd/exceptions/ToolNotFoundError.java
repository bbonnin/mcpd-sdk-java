package io.github.brunolab.mcpd.exceptions;

/**
 * Exception raised when a specified tool doesn't exist on a server.
 */
public class ToolNotFoundError extends McpdError {

    private String serverName;

    private String toolName;

    public ToolNotFoundError(String message) {
        super(message);
    }

    public String getServerName() {
        return serverName;
    }

    public String getToolName() {
        return toolName;
    }

    public ToolNotFoundError setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public ToolNotFoundError setToolName(String toolName) {
        this.toolName = toolName;
        return this;
    }
}
