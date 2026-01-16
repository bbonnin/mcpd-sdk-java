package io.github.brunolab.mcpd.client;

public enum HealthStatus {
    OK("ok"), TIMEOUT("timeout"), UNREACHABLE("unreachable"), UNKNOWN("unknown");

    private final String value;

    HealthStatus(String value) {
        this.value = value;
    }

    public static boolean isTransient(String status) {
        return status.equals(TIMEOUT.value) ||
                status.equals(UNKNOWN.value);
    }

    public static boolean isHealthy(String status) {
        return status.equals(OK.value);
    }
}
