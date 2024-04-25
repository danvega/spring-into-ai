package dev.danvega.functions;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CityController {

    private final ChatClient chatClient;

    public CityController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/cities")
    public String cityFaq(@RequestParam(value = "message") String message) {
        SystemMessage systemMessage = new SystemMessage("You are a helpful AI Assistant answering questions about cities around the world.");
        UserMessage userMessage = new UserMessage(message);
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .withFunction("currentWeatherFunction")
                .build();
        ChatResponse response = chatClient.call(new Prompt(List.of(systemMessage,userMessage),chatOptions));
        return response.getResult().getOutput().getContent();
    }
}
