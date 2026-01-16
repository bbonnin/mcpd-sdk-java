package io.github.brunolab.mcpd.exceptions;

import java.util.Map;

/**
 * Exception raised when required pipeline processing fails.
 */
public class PipelineError extends McpdError {

    public static final String PIPELINE_FLOW_REQUEST = "request";

    public static final String PIPELINE_FLOW_RESPONSE = "response";

    public static final Map<String, String> PIPELINE_ERROR_FLOWS = Map.of(
            "request-pipeline-failure", PIPELINE_FLOW_REQUEST,
            "response-pipeline-failure", PIPELINE_FLOW_RESPONSE
    );

    private String serverName;

    private String operation;

    private String pipelineFlow;

    public PipelineError(String message) {
        super(message);
    }

    public String getServerName() {
        return serverName;
    }

    public String getOperation() {
        return operation;
    }

    public String getPipelineFlow() {
        return pipelineFlow;
    }

    public PipelineError setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }

    public PipelineError setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    public PipelineError setPipelineFlow(String pipelineFlow) {
        this.pipelineFlow = pipelineFlow;
        return this;
    }
}
