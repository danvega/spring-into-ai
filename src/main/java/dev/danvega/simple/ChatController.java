package dev.danvega.simple;

import org.springframework.ai.chat.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/api/generate")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a Dad Joke") String message) {
        return Map.of("generation",chatClient.call(message));
    }

}
