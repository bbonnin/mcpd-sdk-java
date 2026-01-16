package io.github.brunolab.mcpd.langchain;

import dev.langchain4j.model.chat.request.json.JsonBooleanSchema;
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema;
import dev.langchain4j.model.chat.request.json.JsonNumberSchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.agent.tool.ToolSpecification;
import io.modelcontextprotocol.spec.McpSchema;
import io.github.brunolab.mcpd.exceptions.McpdError;

import java.util.Map;
import java.util.Objects;

/**
 * Convert a McpSchema.Tool to a LangChain4j ToolSpecification.
 * Only basic types are taken into account for the moment.
 */
public class Mcp2LangchainConverter {

    public static ToolSpecification convert(McpSchema.Tool mcpTool) {
        if (Objects.isNull(mcpTool)) {
            throw new IllegalArgumentException("MCP tool can not be null");
        }

        return ToolSpecification.builder()
                .name(mcpTool.name())
                .description(mcpTool.description())
                .parameters(convertJsonSchema(mcpTool.inputSchema()))
                .build();
    }

    private static JsonObjectSchema convertJsonSchema(McpSchema.JsonSchema mcpJsonSchema) {
        if (mcpJsonSchema == null) {
            return null;
        }

        String type = mcpJsonSchema.type();

        if ("object".equals(type)) {
            JsonObjectSchema.Builder targetSchemaBuilder = JsonObjectSchema.builder();
            targetSchemaBuilder.required(mcpJsonSchema.required());

            Map<String, Object> properties = mcpJsonSchema.properties();

            properties.forEach((propName, details) -> {
                if (details instanceof Map<?, ?> propDetails) {
                    String propType = (String) propDetails.get("type");
                    String propDescription = (String) propDetails.get("description");

                    JsonSchemaElement schemaElement = switch (propType) {
                        case "string" -> JsonStringSchema.builder().description(propDescription).build();
                        case "integer" -> JsonIntegerSchema.builder().description(propDescription).build();
                        case "boolean" -> JsonBooleanSchema.builder().description(propDescription).build();
                        case "number" -> JsonNumberSchema.builder().description(propDescription).build();
                        case null, default -> throw new McpdError("Unsupported type: " + propType);
                    };

                    targetSchemaBuilder.addProperty(propName, schemaElement);
                }
            });

            return targetSchemaBuilder.build();
        }

        return null;
    }
}
