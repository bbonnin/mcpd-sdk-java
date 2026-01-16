package io.github.brunolab.mcpd.exceptions;

/**
 * Exception raised when a specified MCP server doesn't exist.
 */
public class ServerNotFoundError extends McpdError {

    private String serverName;

    public ServerNotFoundError(String message) {
        super(message);
    }

    public String getServerName() {
        return serverName;
    }

    public ServerNotFoundError setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }
}
