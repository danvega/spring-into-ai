package dev.danvega.functions;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final ChatClient chatClient;

    public WeatherController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/by-city/{city}")
    public String byCity(@PathVariable String city) {
        UserMessage userMessage = new UserMessage("What's the current weather like in " + city);
        ChatResponse response = chatClient.call(new Prompt(List.of(userMessage),
                OpenAiChatOptions.builder()
                .withFunction("currentWeatherFunction")
                .build()));
        return response.getResult().getOutput().getContent();
    }
}
