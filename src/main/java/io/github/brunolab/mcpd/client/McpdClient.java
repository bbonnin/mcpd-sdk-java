package io.github.brunolab.mcpd.client;

import io.modelcontextprotocol.spec.McpSchema;
import io.github.brunolab.mcpd.exceptions.McpdError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class McpdClient {

    private final String apiEndpoint;

    private final McpdHttpClient httpClient;

    public McpdClient(String apiEndpoint, String apiKey) {
        if (apiEndpoint == null || apiEndpoint.trim().isEmpty()) {
            throw new McpdError("apiEndpoint must be set");
        }
        this.apiEndpoint = apiEndpoint.replaceAll("/+$", "").trim();
        this.httpClient = new McpdHttpClient(apiEndpoint, apiKey);
    }

    public Map<String, Object> call(String serverName, String toolName, Map<String, Object> params) {
        String uri = format("%s/api/v1/servers/%s/tools/%s", apiEndpoint, serverName, toolName);
        return httpClient.sendPostRequest(uri, params, serverName, toolName);
    }

    public List<String> getServers() {
        String uri = format("%s/api/v1/servers", apiEndpoint);
        return httpClient.sendGetRequest(uri, null, null);
    }

    public List<Map<String, Object>> getTools(String serverName) {
        String uri = format("%s/api/v1/servers/%s/tools", apiEndpoint, serverName);
        Map<String, Object> response = httpClient.sendGetRequest(uri, null, null);
        return (List<Map<String, Object>>) response.get("tools");
    }

    public Map<String, List<Map<String, Object>>> getTools() {
        Map<String, List<Map<String, Object>>> tools = new HashMap<>();
        for (String server : getServers()) {
            tools.put(server, getTools(server));
        }
        return tools;
    }

    public List<McpSchema.Tool> toAgentTools(String serverName) {
        List<McpSchema.Tool> agentTools = new ArrayList<>();
        for (Map<String, Object> toolDef : getTools(serverName)) {
            String name = serverName + "__" + (String) toolDef.get("name");
            String description = (String) toolDef.get("description");
            Map<String, Object> inputSchema = (Map<String, Object>) toolDef.get("inputSchema");
            List<String> required = (List<String>) inputSchema.get("required");
            Map<String, Object> properties = (Map<String, Object>) inputSchema.get("properties");

            McpSchema.JsonSchema schema = new McpSchema.JsonSchema(
                    "object", properties, required, true, Map.of(), Map.of());

            McpSchema.Tool tool = new McpSchema.Tool(
                    name, name /* title */, description, schema, Map.of() /* output schema */, null, Map.of());

            agentTools.add(tool);
        }
        return agentTools;
    }
}
