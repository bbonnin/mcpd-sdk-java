package io.github.brunolab.mcpd.test;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import io.modelcontextprotocol.spec.McpSchema;
import io.github.brunolab.mcpd.client.McpdClient;
import io.github.brunolab.mcpd.langchain.ToolClient;

import java.util.List;
import java.util.Map;

/**
 * Some basic examples of how to use the library.
 * To be removed, it's just for test purpose.
 */
public class Test {

    private static final McpdClient client = new McpdClient("http://localhost:8090", null);

    private static void basicTests() {
        System.out.println("Servers: " +
                client.getServers());
        System.out.println("Call 'time/get_current_time': " +
                client.call("time", "get_current_time", Map.of("timezone", "UTC")));
        System.out.println("Tools 'time': " +
                client.getTools("time"));
        System.out.println("Tools: " +
                client.getTools());
    }

    public static void main(String[] args) {
        //basicTests();
        agentTests();
    }

    private static void agentTests() {

//        OpenAiChatModel model = OpenAiChatModel.builder()
//                .modelName("gpt-5.2")
//                .apiKey(System.getenv("OPENAI_API_KEY"))
//                .build();

        OllamaChatModel model = OllamaChatModel.builder()
                .baseUrl(System.getenv("OLLAMA_BASE_URL"))
                .modelName("llama3.3")
                .build();

        List<McpSchema.Tool> tools = client.toAgentTools("time");

        McpClient mcpClient = new ToolClient.Builder()
                .mcpdClient(client)
                .serverName("time")
                .build();

        McpToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .toolProvider(toolProvider)
                .build();

        String reponse = assistant.chat("What time is it for timezone Europe/London ?");
        System.out.println(reponse);
    }

    interface Assistant {
        String chat(String message);
    }
}
