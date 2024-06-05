package dev.danvega.prompt;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TreasureController {

    private final ChatClient chatClient;

    public TreasureController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("Please respond to any question in the voice of a pirate.")
                .build();
    }

    @GetMapping("/treasure")
    public String treasureFacts() {
        return chatClient.prompt()
                .user("Tell me a really interesting fact about famous pirate treasures. Please keep your answer to 1 or 2 sentences.")
                .call()
                .content();
    }
}
