package io.github.brunolab.mcpd.exceptions;

/**
 * Exception raised when a specified MCP server is not healthy.
 */
public class ServerUnhealthyError extends McpdError {

    private String serverName;

    private String healthStatus;

    public ServerUnhealthyError(String message) {
        super(message);
    }

    public String getServerName() {
        return serverName;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public ServerUnhealthyError setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public ServerUnhealthyError setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
        return this;
    }
}
