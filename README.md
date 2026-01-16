# mcpd-sdk-java

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](
https://www.apache.org/licenses/LICENSE-2.0)
![Java](https://img.shields.io/badge/Java-21%2B-informational)


**mcpd-sdk-java** is a Java library for interacting with the mcpd application, 
functionally equivalent to Mozilla AI’s **mcpd-sdk-python** library.

It enables Java applications to interact with MCPD servers in order to expose, consume, and orchestrate tools, 
resources, and context for AI agents and models.

---

## ✨ Features

- MCPD client support
- Compatible with **Java 21**
- Easily extensible and integrable into existing Java applications

---

## 📦 Installation

### Maven

```xml
<dependency>
    <groupId>io.github.brunolab</groupId>
    <artifactId>mcpd-sdk-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

```kotlin
dependencies {
    implementation("io.github.brunolab:mcpd-sdk-java:0.1.0")
}
```

---

## 🚀 Quick Start

### Creating an MCPD client

```java
McpdClient client = new McpdClient("http://localhost:8090", "api key unused");
```

### Calling a remote tool

```java
Map<String, Object> result = client.call("time", "get_current_time", Map.of("timezone", "UTC")));
System.out.println(result);
```

### Integration with Langchain4j

See [Test.java](src/main/java/io/github/brunolab/mcpd/test/Test.java)

```java
List<McpSchema.Tool> tools = client.toAgentTools("time");

McpClient client = new ToolClient.Builder()
        .tools(tools)
        .mcpdClient(client)
        .serverName("time")
        .build();

McpToolProvider toolProvider = McpToolProvider.builder()
        .mcpClients(client)
        .build();

Assistant assistant = AiServices.builder(Assistant.class)
        .chatModel(model)
        .toolProvider(toolProvider)
        .build();

OllamaChatModel model = OllamaChatModel.builder()
        .baseUrl(System.getenv("OLLAMA_BASE_URL"))
        .modelName("llama3.3")
        .build();

String reponse = assistant.chat("What time is it for timezone Europe/London ?");
System.out.println(reponse);
```

---

## 🤝 Contributing

Contributions are welcome 🙌

1. Fork the repository
2. Create a branch (`feature/my-feature`)
3. Commit clear, well-documented changes
4. Open a Pull Request

Please follow Java coding conventions and add tests when relevant.

---

## 📄 License

This project is licensed under the **Apache License 2.0**.

---

## 🔗 Useful Links

* [Mozilla AI – MCPD](https://mozilla-ai.github.io/mcpd/)
* [mcpd-sdk-python](https://github.com/mozilla-ai/mcpd-sdk-python)
* [MCP protocol documentation](https://modelcontextprotocol.io/docs/getting-started/intro)

---

## 💻 TODO

* [ ] Unit tests 😇
* [ ] Health API
* [x] Agent tools (first version done)
* [ ] Integration with Java AI frameworks
  * See Langchain4j integration in the Test class
* [ ] Advanced documentation and full examples

---

## 📢 Project Status

🚧 Work in progress — the API may evolve.

Feedback and contributions are highly appreciated!
