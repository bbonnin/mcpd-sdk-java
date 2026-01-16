package io.github.brunolab.mcpd.langchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.McpGetPromptResult;
import dev.langchain4j.mcp.client.McpPrompt;
import dev.langchain4j.mcp.client.McpReadResourceResult;
import dev.langchain4j.mcp.client.McpResource;
import dev.langchain4j.mcp.client.McpResourceTemplate;
import dev.langchain4j.mcp.client.McpRoot;
import dev.langchain4j.service.tool.ToolExecutionResult;
import io.modelcontextprotocol.spec.McpSchema;
import io.github.brunolab.mcpd.client.McpdClient;
import io.github.brunolab.mcpd.exceptions.McpdError;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNullElseGet;

public class ToolClient implements McpClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String key;
    private final Map<String, ToolSpecification> tools;
    private final McpdClient mcpdClient;

    public ToolClient(Builder builder) {
        this.key = requireNonNullElseGet(builder.key, () -> UUID.randomUUID().toString());

        // For the mcpdClient, we have two options:
        // - Either mcpdClient is provided
        // - Or mcpdApiEndpoint and mcpdApiKey are provided to create an instance of McpdClient
        this.mcpdClient = requireNonNullElseGet(builder.mcpdClient,
                () -> new McpdClient(builder.mcpdApiEndpoint, builder.mcpdApiKey));

        // For the tools, we have two options:
        // - Either tools are provided in the builder
        // - Or we have to get them using the mcpdClient, in this case we need the serverName
        List<McpSchema.Tool> mcpdTools = builder.tools;

        if (mcpdTools == null || mcpdTools.isEmpty()) {
            mcpdTools = this.mcpdClient.toAgentTools(builder.serverName);
        }

        this.tools = mcpdTools != null && !mcpdTools.isEmpty() ?
                mcpdTools.stream()
                        .map(Mcp2LangchainConverter::convert)
                        .collect(Collectors.toMap(ToolSpecification::name, tool -> tool)):
                Map.of();
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public List<ToolSpecification> listTools() {
        return tools.values().stream().toList();
    }

    @Override
    public ToolExecutionResult executeTool(ToolExecutionRequest executionRequest) {
        ToolSpecification toolSpecification = tools.get(executionRequest.name());

        if (toolSpecification != null) {
            String[] toolInfos = executionRequest.name().split("__");
            // Convert the string "arguments" (that should be a JSON) into a Map
            try {
                Map<String, Object> params = objectMapper
                        .readValue(executionRequest.arguments(), new TypeReference<>() {});
                Map<String, Object> result = mcpdClient.call(toolInfos[0], toolInfos[1], params);
                String jsonResult = objectMapper.writeValueAsString(result);

                return ToolExecutionResult.builder().isError(false).resultText(jsonResult).result(result).build();

            } catch (JsonProcessingException e) {
                return ToolExecutionResult.builder().isError(true)
                        .resultText("Cannot parse arguments: " + executionRequest.arguments()).build();
            } catch (McpdError e) {
                return ToolExecutionResult.builder().isError(true)
                        .resultText("Problem when invoking the server: " + e.getMessage()).build();
            }
        }

        return ToolExecutionResult.builder().isError(true).resultText("Unknown tool").build();
    }

    @Override
    public List<McpResource> listResources() {
        // Not implemented
        return List.of();
    }

    @Override
    public List<McpResourceTemplate> listResourceTemplates() {
        // Not implemented
        return List.of();
    }

    @Override
    public McpReadResourceResult readResource(String uri) {
        // Not implemented
        return null;
    }

    @Override
    public List<McpPrompt> listPrompts() {
        // Not implemented
        return List.of();
    }

    @Override
    public McpGetPromptResult getPrompt(String name, Map<String, Object> arguments) {
        // Not implemented
        return null;
    }

    @Override
    public void checkHealth() {
        // Not implemented
    }

    @Override
    public void setRoots(List<McpRoot> roots) {
        // Not implemented
    }

    @Override
    public void close() throws Exception {
        // Not implemented
    }

    public static class Builder {
        private String key;
        private List<McpSchema.Tool> tools;
        private String mcpdApiEndpoint;
        private String mcpdApiKey;
        private McpdClient mcpdClient;
        private String serverName;

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder tools(List<McpSchema.Tool> tools) {
            this.tools = tools;
            return this;
        }

        public Builder mcpdApiEndpoint(String mcpdApiEndpoint) {
            this.mcpdApiEndpoint = mcpdApiEndpoint;
            return this;
        }

        public Builder mcpdApiKey(String mcpdApiKey) {
            this.mcpdApiKey = mcpdApiKey;
            return this;
        }

        public Builder mcpdClient(McpdClient mcpdClient) {
            this.mcpdClient = mcpdClient;
            return this;
        }

        public Builder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public ToolClient build() {
            return new ToolClient(this);
        }
    }
}
