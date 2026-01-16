package io.github.brunolab.mcpd.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.brunolab.mcpd.exceptions.AuthenticationError;
import io.github.brunolab.mcpd.exceptions.ConnectionError;
import io.github.brunolab.mcpd.exceptions.McpdError;
import io.github.brunolab.mcpd.exceptions.PipelineError;
import io.github.brunolab.mcpd.exceptions.ServerNotFoundError;
import io.github.brunolab.mcpd.exceptions.ToolExecutionError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static java.lang.String.format;
import static io.github.brunolab.mcpd.exceptions.PipelineError.PIPELINE_ERROR_FLOWS;

public class McpdHttpClient {

    private static final Logger log = LoggerFactory.getLogger(McpdHttpClient.class);

    private static final String MCPD_ERROR_TYPE_HEADER = "Mcpd-Error-Type";

    private final String apiEndpoint;

    private final String apiKey;

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public McpdHttpClient(String apiEndpoint, String apiKey) {
        this.apiEndpoint = apiEndpoint.replaceAll("/+$", "").trim();
        if (this.apiEndpoint.isEmpty()) {
            throw new IllegalArgumentException("apiEndpoint must be set");
        }
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private <T> T safeParseJson(String jsonResponse) {
        try {
            JsonNode node = objectMapper.readTree(jsonResponse);

            if (node.isTextual()) {
                String innerJson = node.asText();
                return objectMapper.readValue(innerJson, new TypeReference<>() {});
            } else {
                return objectMapper.convertValue(node, new TypeReference<>() {});
            }

        } catch (JsonProcessingException e) {
            log.error("Invalid JSON: {}", jsonResponse);
            throw new McpdError("JSON parsing failed: " + e.getMessage());
        }
    }

    private HttpRequest.Builder createRequestBuilder(String uri) {
        URI targetUri = URI.create(uri);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(targetUri)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30));

        if (!apiKey.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + apiKey);
        }

        return requestBuilder;
    }

    private String sendRequest(HttpRequest request, String serverName, String toolName)
            throws IOException, InterruptedException {

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            raiseForHttpError(response, serverName, toolName);
        }

        return response.body();
    }

    public <T> T sendGetRequest(String uri, String serverName, String toolName) {
        try {
            HttpRequest.Builder requestBuilder = createRequestBuilder(uri);
            HttpRequest request = requestBuilder.GET().build();
            String responseBody = sendRequest(request, serverName, toolName);
            return safeParseJson(responseBody);
        } catch (ConnectException e) {
            throw new ConnectionError(format("Unable to connect to MCPD daemon at %s", apiEndpoint));
        } catch (InterruptedException | IOException e) {
            throw new McpdError(format("Error calling tool '%s' on server '%s': %s",
                    toolName, serverName, e.getMessage()));
        }
    }

    public <T> T sendPostRequest(String uri, Map<String, Object> params,
                                 String serverName, String toolName) {
        try {
            HttpRequest.Builder requestBuilder = createRequestBuilder(uri);
            String jsonBody = objectMapper.writeValueAsString(params);
            HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
            String responseBody = sendRequest(request, serverName, toolName);
            return safeParseJson(responseBody);
        } catch (ConnectException e) {
            throw new ConnectionError(format("Unable to connect to MCPD daemon at %s", apiEndpoint));
        } catch (InterruptedException | IOException e) {
            throw new McpdError(format("Error calling tool '%s' on server '%s': %s",
                    toolName, serverName, e.getMessage()));
        }
    }

    private void raiseForHttpError(HttpResponse<?> response, String serverName, String toolName) {
        int status = response.statusCode();

        if (status == 401) {
            throw new AuthenticationError(format(
                    "Authentication failed when calling '%s' on '%s': status=%s", toolName, serverName, status));
        }

        if (status == 404) {
            throw new ServerNotFoundError(format(
                    "Server '%s' not found", serverName));
        }

        if (status == 500) {
            String errorType = response.headers().firstValue(MCPD_ERROR_TYPE_HEADER).orElse("").toLowerCase();
            String flow = PIPELINE_ERROR_FLOWS.get(errorType);
            if (flow != null) {
                String msg = response.body() != null ?
                        response.body().toString() : "Pipeline failure";
                throw new PipelineError(msg)
                        .setServerName(serverName)
                        .setOperation(serverName + "." + toolName)
                        .setPipelineFlow(flow);
            }
        }

        if (status >= 500) {
            throw new ToolExecutionError(format(
                    "Server error when executing '%s' on '%s': status=%s", toolName, serverName, status))
                    .setServerName(serverName)
                    .setToolName(toolName);
        }

        throw new ToolExecutionError(format(
                "Error calling tool '%s' on server '%s': status=%s", toolName, serverName, status))
                .setServerName(serverName)
                .setToolName(toolName);
    }
}
