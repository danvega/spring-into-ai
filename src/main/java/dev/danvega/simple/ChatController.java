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
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a dad joke about dogs") String message) {
        return chatClient.call(message);
    }

}
